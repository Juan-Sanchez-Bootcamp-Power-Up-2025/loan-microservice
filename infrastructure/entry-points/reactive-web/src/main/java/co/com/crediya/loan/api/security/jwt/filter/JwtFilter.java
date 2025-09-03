package co.com.crediya.loan.api.security.jwt.filter;

import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class JwtFilter implements WebFilter {

    private static final List<String> WHITELIST = List.of(
            "/swagger",
            "/webjars",
            "/v3",
            "/favicon.ico"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();
        if (pathContains(path)) {
            return chain.filter(exchange);
        }
        String auth = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (auth == null) {
            return Mono.error(new Throwable("No token was found"));
        }
        if (!auth.startsWith("Bearer ")) {
            return Mono.error(new Throwable("Invalid auth"));
        }
        String token = auth.replace("Bearer ", "");
        exchange.getAttributes().put("token", token);
        return chain.filter(exchange);
    }

    private boolean pathContains(String path) {
        for (String pathList : WHITELIST) {
            if (path.contains(pathList)) return true;
        }
        return false;
    }

}
