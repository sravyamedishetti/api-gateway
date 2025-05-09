package com.learning.microservices.api_gateway.bean;

import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.Buildable;
import org.springframework.cloud.gateway.route.builder.PredicateSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Function;

@Configuration
public class ApiGatewayConfiguration {

    @Bean
    public RouteLocator gatewayRouter(RouteLocatorBuilder builder){
        Function<PredicateSpec, Buildable<Route>> routeFunction =
                p -> p.path("/get")
                        .filters(f ->
                                f.addRequestHeader("MyHeader","MyUri")
                                        .addRequestParameter("MyParam","MyValue"))
                        .uri("http://httpbin.org:80");
        Function<PredicateSpec, Buildable<Route>> routeFunction1 =
                p -> p.path("/currency-exchange/**")
                        .filters(f ->
                                f.addRequestHeader("MyHeader","MyUri")
                                        .addRequestParameter("MyParam","MyValue"))
                        .uri("lb://currency-exchange");

        return builder.routes()
                .route(routeFunction)
                .route(routeFunction1)
                .route(p -> p.path("/currency-conversion/**").uri("lb://currency-conversion"))
                .route(p -> p.path("/currency-conversion/**").uri("lb://currency-conversion"))
                .route(p -> p.path("/currency-conversion-feign/**").uri("lb://currency-conversion"))
                .route(p -> p.path("/currency-conversion-new/**")
                        .filters(f -> f.rewritePath("/currency-conversion-new/(?<segment>.*)",
                        "/currency-conversion-feign/${segment}")).uri("lb://currency-conversion"))
                .build();
    }
}
