package com.example.gateway.config;

import com.example.gateway.service.DiscoveryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.DefaultResponse;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.cloud.client.loadbalancer.reactive.ReactiveLoadBalancer;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomLoadBalancer implements ReactiveLoadBalancer<ServiceInstance> {

    private final DiscoveryService discoveryService;
    private final DiscoveryClient discoveryClient;
    private final String serviceId = "DISCOVERYWEBAPISERVICE";
    private AtomicInteger currentIndex = new AtomicInteger(0);

    @Override
    public Mono<Response<ServiceInstance>> choose(Request request) {
        List<ServiceInstance> instances = getRedisInstances();

        if (instances.isEmpty()) {
            return Mono.error(new IllegalStateException("No instances available for " + serviceId));
        }

        // 라운드 로빈 알고리즘을 사용.
        ServiceInstance instance = instances.get(currentIndex.getAndUpdate(i -> (i + 1) % instances.size()));
//        log.info("Selected instance: {} : {}", instance.getInstanceId(), instance.getPort());

        return Mono.just(new DefaultResponse(instance));
    }

    /** discovery instance 캐싱 */
    private List<ServiceInstance> getServiceInstances() {
        return discoveryService.discoverServiceInstances(serviceId);
    }

    /** discovery instance 캐싱 X */
    private List<ServiceInstance> getRedisInstances() {
        return discoveryClient.getInstances(serviceId);
    }
}

