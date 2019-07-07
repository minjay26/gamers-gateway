package org.minjay.gamers.gateway.server.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class ExtractTokenToJwtFilter implements GlobalFilter, Ordered {
    private static final String JWT_REDIS_KEY_PREFIX = "accounts:jwt:";

    @Autowired
    private ValueOperations<String, String> valueOperations;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        String token = request.getHeaders().getFirst("x-auth-token");
        ServerHttpResponse response = exchange.getResponse();
        if (StringUtils.isEmpty(token)) {
            response.setStatusCode(HttpStatus.FORBIDDEN);
            return null;
        }
        String jwtToken = valueOperations.get(JWT_REDIS_KEY_PREFIX + token);
        DecodedJWT jwt = JWT.decode(jwtToken);
        ServerHttpRequest.Builder mutate = request.mutate();
        mutate.header("Authorization", "Bearer " + jwt.getSubject());
        chain.filter(exchange.mutate().request(mutate.build()).build());

        return Mono.empty();
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
