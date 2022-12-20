package com.sedin.qna.configuration;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class WithCustomMockUserSecurityContextFactory implements WithSecurityContextFactory<WithCustomMockUser> {

    @Override
    public SecurityContext createSecurityContext(WithCustomMockUser annotation) {
//        List<SimpleGrantedAuthority> authorities = Arrays.stream(annotation.role().split(","))
//                .map(SimpleGrantedAuthority::new)
//                .collect(Collectors.toList());

        SecurityContext context = SecurityContextHolder.createEmptyContext();

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(annotation.role()));
        Authentication auth = new UsernamePasswordAuthenticationToken(annotation.username(), "", authorities);
        context.setAuthentication(auth);

        return context;
    }
}
