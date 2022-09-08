package com.sedin.qna.article.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sedin.qna.account.model.Account;
import com.sedin.qna.article.model.ArticleDto;
import com.sedin.qna.article.service.ArticleService;
import com.sedin.qna.interceptor.AuthenticationInterceptor;
import com.sedin.qna.network.ApiResponseCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ArticleController.class)
@MockBean(JpaMetamodelMappingContext.class)
@ExtendWith({RestDocumentationExtension.class})
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "docs.api.com")
class ArticleControllerWebTest {

    private static final Long AUTHORIZED_ID = 1L;
    private static final String PREFIX = "prefix";
    private static final String TITLE = "title";
    private static final String CONTENT = "content";
    private static final String AUTHOR = "author";
    private static final String AUTHORIZATION = "Authorization";
    private static final String VALID_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJhY2NvdW50SWQiOjF9." +
            "LwF0Ms-3xGGJX9JBIrc7bzGl1gYUAq3R3gesg35BA1w";

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationInterceptor authenticationInterceptor;

    @MockBean
    private ArticleService articleService;

    private Account authenticatedAccount;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) throws Exception {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(documentationConfiguration(restDocumentation))
                .build();

        authenticatedAccount = Account.builder()
                .id(AUTHORIZED_ID)
                .build();

        when(authenticationInterceptor.preHandle(any(), any(), any()))
                .then(invocation -> {
                    HttpServletRequest request = invocation.getArgument(0);
                    request.setAttribute("account", authenticatedAccount);
                    return true;
                });
    }

    @Test
    void When_Request_Create_Article_With_Post_Method_Expect_HttpStatus_Is_Created() throws Exception {

        // given
        ArticleDto.ResponseDetail response = ArticleDto.ResponseDetail.builder()
                .id(1L)
                .title(TITLE)
                .content(CONTENT)
                .author(AUTHOR)
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();

        ArticleDto.Create create = ArticleDto.Create.builder()
                .title(TITLE)
                .content(CONTENT)
                .build();

        String requestBody = objectMapper.writeValueAsString(create);

        given(articleService.create(eq(authenticatedAccount), any(ArticleDto.Create.class)))
                .willReturn(response);

        // when
        ResultActions result = mockMvc.perform(post("/api/articles")
                .content(requestBody)
                .header(AUTHORIZATION, VALID_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8));

        // then
        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.article.id").value(1L))
                .andExpect(jsonPath("$.data.article.title").value(TITLE))
                .andExpect(jsonPath("$.data.article.content").value(CONTENT));
    }

    @Test
    void When_Request_All_Articles_With_Get_Method_Expect_HttpStatus_Is_OK() throws Exception {

        // given
        List<ArticleDto.Response> list = Arrays.asList(
                ArticleDto.Response.builder()
                        .id(1L)
                        .title(TITLE)
                        .author(AUTHOR)
                        .createdAt(LocalDateTime.now())
                        .modifiedAt(LocalDateTime.now())
                        .build(),
                ArticleDto.Response.builder()
                        .id(2L)
                        .title(TITLE)
                        .author(AUTHOR)
                        .createdAt(LocalDateTime.now())
                        .modifiedAt(LocalDateTime.now())
                        .build()
        );

        given(articleService.findAll())
                .willReturn(list);

        // when
        ResultActions result = mockMvc.perform(get("/api/articles")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .header(AUTHORIZATION, VALID_TOKEN));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.articles[0].id").value(1L))
                .andExpect(jsonPath("$.data.articles[1].id").value(2L))
                .andExpect(jsonPath("$.data.articles[0].content").doesNotHaveJsonPath());
    }

    @Test
    void When_Request_Article_Detail_With_Get_Method_Expect_HttpStatus_Is_OK() throws Exception {

        // given
        ArticleDto.ResponseDetail detail = ArticleDto.ResponseDetail.builder()
                .id(1L)
                .title(TITLE)
                .content(CONTENT)
                .author(AUTHOR)
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();

        given(articleService.findById(1L))
                .willReturn(detail);

        // when
        ResultActions result = mockMvc.perform(get("/api/articles/" + 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .header(AUTHORIZATION, VALID_TOKEN));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.article.id").value(1L))
                .andExpect(jsonPath("$.data.article.content").value(CONTENT));
    }

    @Test
    void When_Request_Update_Article_With_Patch_Method_Expect_HttpStatus_Is_OK() throws Exception {

        // given
        ArticleDto.ResponseDetail detail = ArticleDto.ResponseDetail.builder()
                .id(1L)
                .title(PREFIX + TITLE)
                .content(PREFIX + CONTENT)
                .author(AUTHOR)
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();

        ArticleDto.Update update = ArticleDto.Update.builder()
                .title(TITLE)
                .content(CONTENT)
                .build();

        String requestBody = objectMapper.writeValueAsString(update);

        given(articleService.update(eq(authenticatedAccount), eq(1L), any(ArticleDto.Update.class)))
                .willReturn(detail);

        // when
        ResultActions result = mockMvc.perform(patch("/api/articles/" + 1L)
                .content(requestBody)
                .header(AUTHORIZATION, VALID_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.article.title").value(PREFIX + TITLE))
                .andExpect(jsonPath("$.data.article.content").value(PREFIX + CONTENT));
    }

    @Test
    void When_Request_Delete_Article_With_Delete_Method_Expect_HttpStatus_Is_OK() throws Exception {

        // given
        doNothing()
                .when(articleService)
                .delete(authenticatedAccount, 1L);

        // when
        ResultActions result = mockMvc.perform(delete("/api/articles/" + 1L)
                .header(AUTHORIZATION, VALID_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(ApiResponseCode.OK.getText()));
    }
}
