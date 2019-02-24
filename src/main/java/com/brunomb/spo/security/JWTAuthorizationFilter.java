package com.brunomb.spo.security;

import com.brunomb.spo.ApplicationConfig;
import io.jsonwebtoken.Jwts;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

    private ApplicationConfig applicationConfig;


    public JWTAuthorizationFilter(AuthenticationManager authManager, ApplicationConfig applicationConfig) {
        super(authManager);
        this.applicationConfig = applicationConfig;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws IOException, ServletException {
        String header = req.getHeader(applicationConfig.getJwt().getHeader());

        if (header == null || !header.startsWith(applicationConfig.getJwt().getTokenPrefix())) {
            chain.doFilter(req, res);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = getAuthentication(req);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        chain.doFilter(req, res);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(applicationConfig.getJwt().getHeader());

        if (token != null) {
            String user = Jwts.parser()
                    .setSigningKey(applicationConfig.getJwt().getKey())
                    .parseClaimsJws(token.replace(applicationConfig.getJwt().getTokenPrefix(), ""))
                    .getBody()
                    .getSubject();

            if (user != null) {
                return new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
            }

            return null;
        }

        return null;
    }
}
