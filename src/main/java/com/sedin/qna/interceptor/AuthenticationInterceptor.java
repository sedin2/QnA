package com.sedin.qna.interceptor;

import com.sedin.qna.account.model.Account;
import com.sedin.qna.athentication.service.AuthenticationService;
import com.sedin.qna.common.LoginRequired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

    private final String ACCOUNT = "account";
    private final String AUTHORIZATION = "Authorization";

    private final AuthenticationService authenticationService;

    public AuthenticationInterceptor(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {

        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            if (!handlerMethod.hasMethodAnnotation(LoginRequired.class)) {
                return true;
            }
        }

        String authorization = request.getHeader(AUTHORIZATION);
        String accessToken = authenticationService.getAccessToken(authorization);
        Account account = authenticationService.decodeAccessToken(accessToken);

        request.setAttribute(ACCOUNT, account);

        return true;
    }
}
