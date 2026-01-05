package com.example.apigatewayservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class FilterConfig {

    public RouteLocator getRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(r -> r.path("/first-service/**")
                        .filters(f -> f.addRequestHeader("f-request", "1st-request-header-by-java")
                                .addResponseHeader("f-response", "1st-response-header-by-java"))
                        .uri("http://127.0.0.1:8081"))
                .route(r -> r.path("/second-service/**")
                        .filters(f -> f.addRequestHeader("s-request", "2nd-request-header-by-java")
                                .addResponseHeader("s-response", "2nd-response-header-by-java"))
                        .uri("http://127.0.0.1:8082"))
                .build();
    }

}
