package com.harden.webflux.webflux.intercpetor;


import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadConfig;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.web.reactive.DispatcherHandler;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.resource.ResourceWebHandler;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ResourceBulkheadHandlerFilter implements InitializingBean, WebFilter, ApplicationListener<ContextRefreshedEvent> {
    private BulkheadConfig config;

    private Map<Object, Bulkhead> bulkheadsCache;

    private List<HandlerMapping> handlerMappings;





    /**
     * 在webflux中会出现这类情况，如果通过
     * RouteFunction和Mapping配置了相同的url不会出错,在dispatch中
     * 优先处理RouterFunctionMapping，因此会忽略Mapping Controller
     * 详细见
     * {@link org.springframework.web.reactive.DispatcherHandler#initStrategies(org.springframework.context.ApplicationContext)}
     * RequestMappingHandlerMapping
     * RouterFunctionMapping
     * 处理Controller 和 RouterFunction的核心 两个Mapping
     * 利用org.springframework.web.reactive.DispatcherHandler#handle(org.springframework.web.server.ServerWebExchange)
     * 的逻辑找到对应mapping然后做具体的处理
     * 因此在处理时应当跟注意
     * @param exchange the current server exchange
     * @param chain provides a way to delegate to the next filter
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return Flux.fromIterable(this.handlerMappings)
                .concatMap(mapping -> mapping.getHandler(exchange))
                .next()
                .flatMap(handler -> invokeHandler(exchange,chain, handler));
    }

    private Mono<Void> invokeHandler(ServerWebExchange exchange,WebFilterChain chain, Object handler) {
        //处理未找到handler情况
        if(handler instanceof ResourceWebHandler){
            return Mono.just(exchange).flatMap(chain::filter);
        }
        Bulkhead bulkhead = bulkheadsCache.computeIfAbsent(handler, n -> Bulkhead.of(handler.getClass().getName(), config));
        bulkhead.acquirePermission();
        return Mono.just(exchange).flatMap(chain::filter)
                .doOnSuccess(e->bulkhead.releasePermission());
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext applicationContext = event.getApplicationContext();
        DispatcherHandler dispatcherHandler = applicationContext.getBean(DispatcherHandler.class);
        this.handlerMappings=dispatcherHandler.getHandlerMappings();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.config = BulkheadConfig.ofDefaults();
        this.bulkheadsCache = new ConcurrentHashMap<>();
    }
}
