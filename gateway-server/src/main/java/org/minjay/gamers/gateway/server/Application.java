package org.minjay.gamers.gateway.server;

import com.alibaba.cloud.nacos.ribbon.NacosRule;
import com.netflix.loadbalancer.IRule;
import org.minjay.gamers.gateway.server.filter.ExtractTokenToJwtPreFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.codec.support.DefaultServerCodecConfigurer;

@EnableDiscoveryClient
@EnableZuulProxy
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public ServerCodecConfigurer serverCodecConfigurer() {
        return new DefaultServerCodecConfigurer();
    }

    @Bean
    public ValueOperations<String, String> valueOperations(RedisTemplate<String, String> redisTemplate) {
        return redisTemplate.opsForValue();
    }

    @Bean
    public ExtractTokenToJwtPreFilter extractTokenToJwtFilter() {
        return new ExtractTokenToJwtPreFilter();
    }

    @Bean
    public IRule rule() {
        return new NacosRule();
    }

//    @Bean
//    public JwtTokenConverter jwtTokenConverter() {
//        Signer signer = getSigner(jwtProperties.keyValue);
//        return new LoginUserJwtTokenConverter(signer);
//    }

}
