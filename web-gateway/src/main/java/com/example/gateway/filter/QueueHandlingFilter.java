package com.example.gateway.filter;

import com.example.gateway.service.QueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

@Component
@Slf4j
public class QueueHandlingFilter extends AbstractGatewayFilterFactory<QueueHandlingFilter.Config> {

    private final QueueService queueService;

    public QueueHandlingFilter(QueueService queueService) {
        super(Config.class);
        this.queueService = queueService;
    }

    public static class Config {
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            /*
            double testTime = System.currentTimeMillis() / 1000.0;
            queueService.enqueueRequest("100000", testTime);
            queueService.enqueueRequest("100001", testTime);
            queueService.enqueueRequest("100002", testTime);
            queueService.enqueueRequest("100003", testTime);
            queueService.enqueueRequest("100004", testTime);
            queueService.enqueueRequest("100005", testTime);
             */


            String userId = extractUserId(exchange);
            log.info("userId:{}", userId);
            double requestTime = System.currentTimeMillis() / 1000.0;

            queueService.enqueueRequest(userId, requestTime);
            Long userRank = queueService.getUserRank(userId);

            if (queueService.canProcessImmediately(userRank)) {
                log.info("remove user from queue:{}, userRank:{}", userId, userRank);
                queueService.removeUserFromQueue(userId);
                return chain.filter(exchange);
            } else {
                exchange.getResponse().getHeaders().add("Queue-Position", String.valueOf(userRank));
                exchange.getResponse().setStatusCode(HttpStatus.ACCEPTED);
                return exchange.getResponse().setComplete();
            }
        };
    }

    private String extractUserId(ServerWebExchange exchange) {
        return exchange.getRequest().getQueryParams().getFirst("userId");
    }
}
