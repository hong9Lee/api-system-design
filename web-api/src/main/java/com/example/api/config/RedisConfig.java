package com.example.api.config;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.SocketOptions;
import io.lettuce.core.TimeoutOptions;
import io.lettuce.core.protocol.RedisCommand;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

import java.time.Duration;

@Configuration
@EnableRedisRepositories
public class RedisConfig {
    @Bean
    public LettuceConnectionFactory redisConnectionFactory(
            RedisProps redisProps) {

        final int REDIS_COMMAND_TIMEOUT = 1000 * 1; // 1초 (1000ms)

        TimeoutOptions timeoutOptions = TimeoutOptions.builder()
                .timeoutSource(new TimeoutOptions.TimeoutSource() {
                    @Override
                    public long getTimeout(RedisCommand<?, ?, ?> redisCommand) {
                        return REDIS_COMMAND_TIMEOUT;
                    }
                })
                .build();

        SocketOptions socketOptions = SocketOptions.builder()
                // 기본 timeout 설정 값은 10초이다. network hang 상황에서는 10초의 시간은 문제가 될 수 있기 때문에 500ms로 설정한다.
                .connectTimeout(Duration.ofMillis(500L))
                .build();

        ClientOptions clientOptions = ClientOptions.builder()
                .timeoutOptions(timeoutOptions)
                .socketOptions(socketOptions)
                .build();

        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                .clientOptions(clientOptions)
                .build();

        var redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(redisProps.getRedisHost());
        redisStandaloneConfiguration.setPort(redisProps.getRedisPort());

        return new LettuceConnectionFactory(redisStandaloneConfiguration, clientConfig);

    }

    @Bean
    public RedisTemplate<?, ?> redisTemplate(LettuceConnectionFactory connectionFactory) {
        RedisTemplate<byte[], byte[]> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        return template;
    }

    @Bean
    public RedissonClient redissonClient(RedisProps redisProps) {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://" + redisProps.getRedisHost() + ":" + redisProps.getRedisPort());
        return Redisson.create(config);
    }
}
