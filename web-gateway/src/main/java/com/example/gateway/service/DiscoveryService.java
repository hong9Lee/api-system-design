package com.example.gateway.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DiscoveryService {

    private final DiscoveryClient discoveryClient;
    private final DiscoveryServiceCache discoveryServiceCache;

    public List<ServiceInstance> discoverServiceInstances(String serviceId) {
        List<ServiceInstance> cachedInstances = discoveryServiceCache.getServiceInstances(serviceId);

        if (cachedInstances != null && !cachedInstances.isEmpty()) {
            return cachedInstances;
        }

        /** Eureka에서 서비스 인스턴스 정보 조회 */
        List<ServiceInstance> instances = discoveryClient.getInstances(serviceId);
        if (instances != null && !instances.isEmpty()) {
            for (ServiceInstance cachedInstance : instances) {
                log.info("redis에 Eureka 에서 가져온 cachedInstance instance 등록 : {} : {}", cachedInstance.getInstanceId(), cachedInstance.getPort());
            }
            discoveryServiceCache.cacheServiceInstance(serviceId, instances);
        }
        return instances;
    }
}
