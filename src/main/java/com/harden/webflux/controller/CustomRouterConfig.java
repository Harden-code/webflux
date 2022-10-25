package com.harden.webflux.controller;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class CustomRouterConfig {

    /**
     * org.springframework.web.reactive.function.server.support
     * .RouterFunctionMapping#initRouterFunctions() 合并
     * @return
     */
    @Bean
    public RouterFunction<ServerResponse> myRouter(){
        return RouterFunctions.route()
                .GET("/mapping/v1/hello",e-> {
                    String world = e.exchange().getRequest()
                            .getQueryParams().getFirst("world");
                    return ServerResponse.ok().bodyValue("say_v3->"+world);
                }).build();
    }

    @Bean
    public RouterFunction<ServerResponse> myRouter_bak(){
        return RouterFunctions.route()
                .GET("/mapping/v2/hello",e-> {
                    String world = e.exchange().getRequest()
                            .getQueryParams().getFirst("world");
                    return ServerResponse.ok().bodyValue("say_v3_->"+world);
                }).build();
    }
}
