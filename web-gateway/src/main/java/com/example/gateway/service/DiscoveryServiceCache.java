package com.example.gateway.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class DiscoveryServiceCache {

    private final RedisTemplate<String, String> redisTemplate;

    private final ObjectMapper objectMapper;

    private String buildCacheKey(String serviceId) {
        return "serviceInstances:" + serviceId;
    }

    public void cacheServiceInstance(String serviceId, List<ServiceInstance> instances) {
        String key = buildCacheKey(serviceId);
        ValueOperations<String, String> ops = redisTemplate.opsForValue();

        for (ServiceInstance cachedInstance : instances) {
            log.info("Eureka에 instance 캐싱: {} : {}", cachedInstance.getInstanceId(), cachedInstance.getPort());
        }

        try {
            String jsonInstances = objectMapper.writeValueAsString(instances);
            ops.set(key, jsonInstances, 10, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public List<ServiceInstance> getServiceInstances(String serviceId) {
        String key = buildCacheKey(serviceId);

        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        try {
            String jsonInstances = ops.get(key);
            if (jsonInstances != null) {
                List<DefaultServiceInstance> defaultServiceInstances = objectMapper.readValue(jsonInstances, new TypeReference<List<DefaultServiceInstance>>() {
                });

                List<ServiceInstance> list = new ArrayList<>();
                list.addAll(defaultServiceInstances);
                return list;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return Collections.emptyList();
    }
}
