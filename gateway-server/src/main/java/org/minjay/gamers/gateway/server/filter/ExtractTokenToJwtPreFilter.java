package org.minjay.gamers.gateway.server.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;

public class ExtractTokenToJwtPreFilter extends ZuulFilter {
    private static final String JWT_REDIS_KEY_PREFIX = "accounts:jwt:";

    @Autowired
    private ValueOperations<String, String> valueOperations;

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext ctx = RequestContext.getCurrentContext();
        String token = ctx.getRequest().getParameter("x-auth-token");
        if (StringUtils.isEmpty(token)) {
            ctx.getResponse().setStatus(HttpStatus.FORBIDDEN.value());
            return null;
        }

        String jwtToken = valueOperations.get(JWT_REDIS_KEY_PREFIX + token);
        if (StringUtils.isEmpty(jwtToken)) {
            ctx.getResponse().setStatus(HttpStatus.FORBIDDEN.value());
            return null;
        }

        DecodedJWT jwt = JWT.decode(jwtToken);
        ctx.addZuulRequestHeader("Authorization", "Bearer " + jwt.getSubject());
        return null;
    }

}
