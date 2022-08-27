package com.sedin.qna.athentication.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthenticationDto {

    private String accessToken;

    @Getter
    public static class Response {

        private String accessToken;

        @Builder
        private Response(String accessToken) {
            this.accessToken = accessToken;
        }

        public static Response of(String accessToken) {
            return Response.builder()
                    .accessToken(accessToken)
                    .build();
        }
    }
}
