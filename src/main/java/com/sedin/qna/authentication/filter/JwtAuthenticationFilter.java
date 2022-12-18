package com.sedin.qna.authentication.filter;

import com.sedin.qna.authentication.service.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION = "Authorization";

    private final JwtTokenProvider jwtTokenProvider;
//    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String tokenInHeader = request.getHeader(AUTHORIZATION);

        if (!jwtTokenProvider.isExistedToken(tokenInHeader) || !jwtTokenProvider.isBearerScheme(tokenInHeader)) {
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = jwtTokenProvider.resolveToken(tokenInHeader);

        try {
            jwtTokenProvider.isValidToken(accessToken);
            Authentication authentication = jwtTokenProvider.decode(accessToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception e) {
            filterChain.doFilter(request, response);
            return;
        }

        filterChain.doFilter(request, response);
    }

}
