package com.example.apigatewayservice.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class CustomFilter extends AbstractGatewayFilterFactory<CustomFilter.Config> {

    public CustomFilter() {
        // 부모 클래스에 Config 정보를 전달하여 필터가 설정을 인식하게 함
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        // exchange: Request/Response가 담긴 바구니
        // chain: 다음 필터로 연결되는 통로
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            // [PRE Filter 로직]
            // 서비스로 요청이 넘어가기 전, 즉시 실행되는 부분입니다.
            log.info("Custom PRE filter: request id -> {}", request.getId());

            // [흐름 제어 및 POST Filter 로직 정의]
            // 1. chain.filter(exchange): "다음 필터를 실행해라"라는 작업(Mono)을 반환합니다.
            // 2. .then(): "앞의 작업(다음 필터 + 마이크로서비스 호출)이 끝나면 아래 로직을 실행해라" (비동기 콜백)
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {

                // [POST Filter 로직]
                // 마이크로서비스로부터 응답이 돌아왔을 때(성공/실패 불문) 실행됩니다.
                // MVC의 인터셉터 postHandle과 유사하지만, 별도의 스레드에서 비동기로 동작할 수 있습니다.
                log.info("Custom POST filter: response code -> {}", response.getStatusCode());

            }));
        };
    }

    // application.yml 파일로부터 설정을 주입받을 때 사용하는 클래스
    public static class Config {
        // 여기에 설정 변수(예: boolean preLogger)를 선언하면 필터 로직에서 사용 가능합니다.
    }

}