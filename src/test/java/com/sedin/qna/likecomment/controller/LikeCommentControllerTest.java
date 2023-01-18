package com.sedin.qna.likecomment.controller;

import com.sedin.qna.authentication.service.JwtTokenProvider;
import com.sedin.qna.common.configuration.SecurityConfiguration;
import com.sedin.qna.common.response.ApiResponseDto;
import com.sedin.qna.configuration.WithCustomMockUser;
import com.sedin.qna.likecomment.service.LikeCommentService;
import com.sedin.qna.util.ApiDocumentUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.nio.charset.StandardCharsets;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LikeCommentController.class)
@MockBean(JpaMetamodelMappingContext.class)
@ExtendWith({RestDocumentationExtension.class})
@Import({
        SecurityConfiguration.class,
        JwtTokenProvider.class
})
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "docs.api.com")
class LikeCommentControllerTest {


    private static final Long COMMENT_ID = 1L;
    private static final String AUTHORIZATION = "Authorization";
    private static final String VALID_TOKEN = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhY2NvdW50SWQiOjF9." +
            "LwF0Ms-3xGGJX9JBIrc7bzGl1gYUAq3R3gesg35BA1w";
    private static final String EMAIL = "cafe@mocha.com";

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LikeCommentService likeCommentService;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .addFilter(new CharacterEncodingFilter(StandardCharsets.UTF_8.name(), true))
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .apply(springSecurity())
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }

    @Test
    @WithCustomMockUser
    void When_Request_Like_Comment_Create_With_Post_Method_Expect_HttpStatus_Is_Created() throws Exception {
        // given
        ApiResponseDto<String> response = ApiResponseDto.DEFAULT_OK;
        given(likeCommentService.create(EMAIL, COMMENT_ID)).willReturn(response);

        // when
        ResultActions result = mockMvc.perform(post("/api/comments/{id}/like", COMMENT_ID)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, VALID_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8));

        // then
        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("OK"))
                .andDo(document("create-like-comment",
                        ApiDocumentUtil.getDocumentRequest(),
                        ApiDocumentUtil.getDocumentResponse(),
                        requestHeaders(headerWithName(AUTHORIZATION).description("Basic auth credentials")),
                        pathParameters(parameterWithName("id").description("댓글 id"))
                ));

        verify(likeCommentService).create(EMAIL, COMMENT_ID);
    }

    @Test
    @WithCustomMockUser
    void When_Request_Like_Comment_Delete_With_Delete_Method_Expect_HttpStatus_Is_OK() throws Exception {
        // given
        ApiResponseDto<String> response = ApiResponseDto.DEFAULT_OK;
        given(likeCommentService.delete(EMAIL, COMMENT_ID)).willReturn(response);

        // when
        ResultActions result = mockMvc.perform(delete("/api/comments/{id}/like", COMMENT_ID)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, VALID_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("OK"))
                .andDo(document("delete-like-comment",
                        ApiDocumentUtil.getDocumentRequest(),
                        ApiDocumentUtil.getDocumentResponse(),
                        requestHeaders(headerWithName(AUTHORIZATION).description("Basic auth credentials")),
                        pathParameters(parameterWithName("id").description("댓글 id"))
                ));

        verify(likeCommentService).delete(EMAIL, COMMENT_ID);
    }
}
