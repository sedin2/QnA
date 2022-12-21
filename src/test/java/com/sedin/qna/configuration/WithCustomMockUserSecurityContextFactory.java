package com.sedin.qna.configuration;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

public class WithCustomMockUserSecurityContextFactory implements WithSecurityContextFactory<WithCustomMockUser> {

    @Override
    public SecurityContext createSecurityContext(WithCustomMockUser annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + annotation.role()));
        Authentication auth = new UsernamePasswordAuthenticationToken(annotation.username(), "", authorities);
        context.setAuthentication(auth);

        return context;
    }
}
