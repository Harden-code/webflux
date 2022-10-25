##### 代码 com.harden.webflux.webflux.intercpetor.ResourceBulkheadHandlerFilter



```
在webflux中会出现这类情况，如果通过
RouteFunction和Mapping配置了相同的url不会出错,在dispatch中
优先处理RouterFunctionMapping，因此会忽略Mapping Controller
详细见
{@link org.springframework.web.reactive.DispatcherHandler#initStrategies(org.springframework.context.ApplicationContext)}
RequestMappingHandlerMapping ,RouterFunctionMapping
处理Controller 和 RouterFunction的核心 两个Mapping
利用org.springframework.web.reactive.DispatcherHandler#handle(org.springframework.web.server.ServerWebExchange)
的逻辑找到对应mapping然后做具体的处理
因此在处理时应当跟注意
```

注意未找到handler情况

```
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
```