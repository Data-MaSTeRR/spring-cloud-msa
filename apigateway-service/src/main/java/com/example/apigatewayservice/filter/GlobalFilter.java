package com.example.apigatewayservice.filter;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class GlobalFilter extends AbstractGatewayFilterFactory<GlobalFilter.Config> {

    public GlobalFilter() {
        // 부모 클래스에 Config 클래스 정보를 전달하여,
        // application.yml의 설정값을 이 클래스로 매핑하도록 합니다.
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        // exchange: HTTP 요청과 응답을 담고 있는 하나의 바구니 (비동기 환경의 Context)
        // chain: 현재 필터 다음에 실행될 필터들의 연결 고리
        return ((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            // [PRE Filter 로직 - 서비스 호출 전]
            // 이 부분은 필터가 실행되자마자 즉시(동기적으로) 실행됩니다.
            log.info("Global Filter baseMessage: {} {}", config.getBaseMessage(), request.getRemoteAddress());

            if (config.isPreLogger()) {
                // yml 설정에서 preLogger가 true일 때만 요청 ID를 로그로 남깁니다.
                log.info("Global Filter Start: request id -> {}", request.getId());
            }

            // [리액티브 파이프라인 반환]
            // 1. chain.filter(exchange): "다음 필터들을 실행하고 최종적으로 마이크로서비스까지 다녀와라"는 약속(Mono)입니다.
            // 2. .then(...): 위 작업(서비스 호출 포함)이 '완료된 직후'에 실행될 콜백을 예약합니다.
            return  chain.filter(exchange).then(Mono.fromRunnable(() -> {

                // [POST Filter 로직 - 서비스 호출 후]
                // 마이크로서비스로부터 응답이 돌아온 시점에 실행되는 비동기 로직입니다.
                if (config.isPostLogger()) {
                    // yml 설정에서 postLogger가 true일 때만 응답 상태 코드를 로그로 남깁니다.
                    log.info("Global Filter End: response code -> {}", response.getStatusCode());
                }

            })); // 이 전체 '실행 계획서'를 반환하면 Netty 엔진이 순서대로 실행합니다.
        });
    }

    // application.yml에 정의된 필터 인자(args)들이 매핑되는 클래스
    @Data
    public static class Config {
        private String BaseMessage; // 공통 메시지
        private boolean preLogger;  // 전처리 로그 여부
        private boolean postLogger; // 후처리 로그 여부
    }
}