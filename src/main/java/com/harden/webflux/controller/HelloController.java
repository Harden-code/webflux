package com.harden.webflux.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/mapping")
public class HelloController {
    @GetMapping("/v1/hello")
    public Mono<String> say_v1(@RequestParam("world")String world){
        return Mono.create(e->e.success("say_v1->"+world));
    }

    @GetMapping("/v2/hello")
    public Mono<String> say_v2(ServerWebExchange webExchange){
        String world = webExchange.getRequest()
                .getQueryParams().getFirst("world");
        return Mono.create(e->e.success("say_v2->"+world));
    }


}
