package com.example.api.service;

import com.example.api.config.RedisProps;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.Test;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

@SpringBootTest
@EnableConfigurationProperties(RedisProps.class)
class CouponServiceTest {

    @Autowired
    private RedisProps redisProps;
    private RedissonClient redissonClient;

    @PostConstruct
    private void init() {
        redissonClient = createRedissonClient();
    }

    @Test
    public void testDistributedLock() throws InterruptedException {
        int numberOfThreads = 10;
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        for (int i = 0; i < numberOfThreads; i++) {
            executor.submit(() -> {
                RLock lock = redissonClient.getLock("testLock");
                try {
                    boolean isLocked = lock.tryLock(100, 10, TimeUnit.SECONDS);
                    if (isLocked) {
                        System.out.println("Lock acquired by " + Thread.currentThread().getName());
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    lock.unlock();
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();
    }

    @Test
    public void testFairDistributedLock() throws InterruptedException {
        final int numberOfThreads = 10;
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch startLatch = new CountDownLatch(1); // 모든 스레드가 동시에 시작하도록 제어
        CountDownLatch doneLatch = new CountDownLatch(numberOfThreads);

        // 락 요청 순서와 락 획득 순서를 기록하는 리스트
        List<Integer> lockRequestOrder = new ArrayList<>();
        List<Integer> lockAcquisitionOrder = Collections.synchronizedList(new ArrayList<>());

        IntStream.range(0, numberOfThreads).forEach(threadId -> {
            executor.submit(() -> {
                try {
                    // 락을 요청하기 직전에 순서 기록
                    synchronized (lockRequestOrder) {
                        lockRequestOrder.add(threadId);
                    }
                    startLatch.await(); // 모든 스레드가 동시에 시작하도록 대기
                    RLock lock = redissonClient.getFairLock("sharedFairLock");
                    if (lock.tryLock(100, 10, TimeUnit.SECONDS)) {
                        try {
                            // 락 획득 순서 기록
                            synchronized (lockAcquisitionOrder) {
                                lockAcquisitionOrder.add(threadId);
                            }
                        } finally {
                            lock.unlock();
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    doneLatch.countDown();
                }
            });
        });

        startLatch.countDown(); // 모든 스레드의 실행을 시작
        doneLatch.await(); // 모든 작업이 완료될 때까지 대기
        executor.shutdown();

        // 락 요청 순서와 락 획득 순서 출력
        System.out.println("Lock request order: " + lockRequestOrder);
        System.out.println("Lock acquisition order: " + lockAcquisitionOrder);
    }


    private RedissonClient createRedissonClient() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://" + redisProps.getRedisHost() + ":" + redisProps.getRedisPort());
        return Redisson.create(config);
    }

}
