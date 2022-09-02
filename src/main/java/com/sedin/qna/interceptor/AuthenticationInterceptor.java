package com.sedin.qna.interceptor;

import com.sedin.qna.athentication.service.AuthenticationService;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

    private final String ACCOUNT_ID = "accountId";
    private final String AUTHORIZATION = "Authorization";

    private final AuthenticationService authenticationService;

    public AuthenticationInterceptor(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {

        if (request.getRequestURI().startsWith("/api/accounts") && request.getMethod().equals(HttpMethod.POST.toString())) {
            return true;
        }

        String authorization = request.getHeader(AUTHORIZATION);
        String accessToken = authenticationService.getAccessToken(authorization);
        Long accountId = authenticationService.decodeAccessToken(accessToken);

        request.setAttribute(ACCOUNT_ID, accountId);

        return true;
    }
}
