package co.com.crediya.loan.api.security.jwt.manager;

import co.com.crediya.loan.api.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtUtil jwtUtil;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono.just(authentication)
                .map(auth -> jwtUtil.getClaims(auth.getCredentials().toString()))
                .log()
                .onErrorResume(error -> Mono.error(new Throwable("Bad token")))
                .map(claims -> {
                    String token = authentication.getPrincipal().toString();
                    var authorities = List.of(new SimpleGrantedAuthority(String.valueOf(claims.get("role"))));
                    var userAuth = new UsernamePasswordAuthenticationToken(claims.getSubject(), null, authorities);
                    userAuth.setDetails(token);
                    return userAuth;
                });
    }

}
