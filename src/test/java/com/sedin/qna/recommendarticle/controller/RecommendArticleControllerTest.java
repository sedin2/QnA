package com.sedin.qna.recommendarticle.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sedin.qna.account.model.Account;
import com.sedin.qna.authentication.service.JwtTokenProvider;
import com.sedin.qna.common.configuration.SecurityConfiguration;
import com.sedin.qna.common.response.ApiResponseDto;
import com.sedin.qna.configuration.WithCustomMockUser;
import com.sedin.qna.recommendarticle.service.RecommendArticleService;
import com.sedin.qna.util.ApiDocumentUtil;
import com.sedin.qna.util.DocumentFormatGenerator;
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
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.beneathPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RecommendArticleController.class)
@MockBean(JpaMetamodelMappingContext.class)
@ExtendWith({RestDocumentationExtension.class})
@Import({
        SecurityConfiguration.class,
        JwtTokenProvider.class
})
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "docs.api.com")
class RecommendArticleControllerTest {

    private static final Long ARTICLE_ID = 1L;
    private static final String AUTHORIZATION = "Authorization";
    private static final String VALID_TOKEN = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhY2NvdW50SWQiOjF9." +
            "LwF0Ms-3xGGJX9JBIrc7bzGl1gYUAq3R3gesg35BA1w";
    private static final String EMAIL = "cafe@mocha.com";

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecommendArticleService recommendArticleService;

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
    void When_Request_Recommend_Article_Create_With_Post_Method_Expect_HttpStatus_Is_Created() throws Exception {
        // given
        ApiResponseDto<String> response = ApiResponseDto.DEFAULT_OK;
        given(recommendArticleService.createRecommendArticle(EMAIL, ARTICLE_ID)).willReturn(response);

        // when
        ResultActions result = mockMvc.perform(post("/api/articles/{id}/recommend", ARTICLE_ID)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, VALID_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8));

        // then
        result.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("OK"))
                .andDo(document("create-recommend-article",
                        ApiDocumentUtil.getDocumentRequest(),
                        ApiDocumentUtil.getDocumentResponse(),
                        requestHeaders(headerWithName(AUTHORIZATION).description("Basic auth credentials")),
                        pathParameters(parameterWithName("id").description("게시글 id"))
                ));

        verify(recommendArticleService).createRecommendArticle(EMAIL, ARTICLE_ID);
    }

    @Test
    @WithCustomMockUser
    void When_Request_Recommend_Article_Delete_With_Delete_Method_Expect_HttpStatus_Is_OK() throws Exception {
        // given
        ApiResponseDto<String> response = ApiResponseDto.DEFAULT_OK;
        given(recommendArticleService.deleteRecommendArticle(EMAIL, ARTICLE_ID)).willReturn(response);

        // when
        ResultActions result = mockMvc.perform(delete("/api/articles/{id}/recommend", ARTICLE_ID)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, VALID_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8));

        // then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("OK"))
                .andDo(document("delete-recommend-article",
                        ApiDocumentUtil.getDocumentRequest(),
                        ApiDocumentUtil.getDocumentResponse(),
                        requestHeaders(headerWithName(AUTHORIZATION).description("Basic auth credentials")),
                        pathParameters(parameterWithName("id").description("게시글 id"))
                ));

        verify(recommendArticleService).deleteRecommendArticle(EMAIL, ARTICLE_ID);
    }
}
