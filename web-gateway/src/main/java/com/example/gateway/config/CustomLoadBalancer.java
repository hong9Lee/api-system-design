package com.example.gateway.config;

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

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomLoadBalancer implements ReactiveLoadBalancer<ServiceInstance> {

    private final DiscoveryClient discoveryClient;
    private String serviceId = "DISCOVERYWEBAPISERVICE";
    private int currentIndex = 0;

    @Override
    public Mono<Response<ServiceInstance>> choose(Request request) {
        return Mono.just(new DefaultResponse(getNextInstance()));
    }

    /** 라운드로빈 알고리즘 */
    private ServiceInstance getNextInstance() {
        List<ServiceInstance> instances = discoveryClient.getInstances(serviceId);
        if (instances.isEmpty()) {
            return null;
        }

        for (ServiceInstance instance : instances) {
            log.info("{} = {} : {}", instance.getServiceId(), instance.getInstanceId(), instance.getPort());
        }

        ServiceInstance instance = instances.get(currentIndex % instances.size());
        currentIndex++;
        return instance;
    }
}

