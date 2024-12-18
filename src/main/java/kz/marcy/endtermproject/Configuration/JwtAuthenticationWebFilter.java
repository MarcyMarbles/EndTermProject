package kz.marcy.endtermproject.Configuration;

import kz.marcy.endtermproject.Service.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Slf4j
public class JwtAuthenticationWebFilter implements WebFilter {

    private final JwtUtils jwtUtils;

    public JwtAuthenticationWebFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (authHeader == null && exchange.getRequest().getURI().getQuery() != null) {
            String query = exchange.getRequest().getURI().getQuery();
            if (query.startsWith("token=")) {
                authHeader = "Bearer " + query.substring(6);
            }
        }

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            try {
                String username = jwtUtils.extractLogin(token);

                if (jwtUtils.validateToken(token, username)) {
                    String roles = jwtUtils.extractRoles(token);
                    Authentication authentication = jwtUtils.getAuthentication(username, roles);
                    SecurityContext context = SecurityContextHolder.createEmptyContext();
                    context.setAuthentication(authentication);

                    return chain.filter(exchange).contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(context)));
                }
            } catch (Exception ex) {
                System.err.println("Token validation failed: " + ex.getMessage());
            }
        }

        return chain.filter(exchange);
    }


}
