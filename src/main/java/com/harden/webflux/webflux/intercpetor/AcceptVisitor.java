package com.harden.webflux.webflux.intercpetor;

import org.springframework.core.io.Resource;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.function.Function;

public class AcceptVisitor implements RouterFunctions.Visitor {

    private boolean accpet;

    private ServerRequest request;

    public AcceptVisitor(ServerRequest request) {
        this.request = request;
    }

    @Override
    public void startNested(RequestPredicate predicate) {

    }

    @Override
    public void endNested(RequestPredicate predicate) {
//        throw new UnsupportedOperationException();
    }

    @Override
    public void route(RequestPredicate predicate, HandlerFunction<?> handlerFunction) {
//        throw new UnsupportedOperationException();
        if (predicate.test(this.request)) {
            this.accpet =true;
        }
    }

    @Override
    public void resources(Function<ServerRequest, Mono<Resource>> lookupFunction) {
//        throw new UnsupportedOperationException();
    }

    @Override
    public void attributes(Map<String, Object> attributes) {
//        throw new UnsupportedOperationException();
    }

    @Override
    public void unknown(RouterFunction<?> routerFunction) {
        throw new UnsupportedOperationException();
    }
}
