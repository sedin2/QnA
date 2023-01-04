package com.sedin.qna.article.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sedin.qna.account.model.Account;
import com.sedin.qna.article.model.ArticleDto;
import com.sedin.qna.article.service.ArticleService;
import com.sedin.qna.authentication.service.JwtTokenProvider;
import com.sedin.qna.common.configuration.SecurityConfiguration;
import com.sedin.qna.common.response.ApiResponseCode;
import com.sedin.qna.configuration.WithCustomMockUser;
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
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.beneathPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ArticleController.class)
@MockBean(JpaMetamodelMappingContext.class)
@ExtendWith({RestDocumentationExtension.class})
@Import({
        SecurityConfiguration.class,
        JwtTokenProvider.class
})
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "docs.api.com")
class ArticleControllerWebTest {

    private static final Long AUTHORIZED_ID = 1L;
    private static final String PREFIX = "prefix";
    private static final String EMAIL = "cafe@mocha.com";
    private static final String TITLE = "title";
    private static final String CONTENT = "content";
    private static final String AUTHOR = "author";
    private static final String AUTHORIZATION = "Authorization";
    private static final String VALID_TOKEN = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhY2NvdW50SWQiOjF9." +
            "LwF0Ms-3xGGJX9JBIrc7bzGl1gYUAq3R3gesg35BA1w";

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ArticleService articleService;

    private Account authenticatedAccount;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .apply(springSecurity())
                .apply(documentationConfiguration(restDocumentation))
                .build();

        authenticatedAccount = Account.builder()
                .id(AUTHORIZED_ID)
                .build();
    }

    @Test
    @WithCustomMockUser
    void When_Request_Create_Article_With_Post_Method_Expect_HttpStatus_Is_Created() throws Exception {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // given
        ArticleDto.ResponseChange response = ArticleDto.ResponseChange.builder()
                .id(1L)
                .title(TITLE)
                .content(CONTENT)
                .author(AUTHOR)
                .commentsCount(0L)
                .articleViewCount(0L)
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();

        ArticleDto.Create create = ArticleDto.Create.builder()
                .title(TITLE)
                .content(CONTENT)
                .build();

        String requestBody = objectMapper.writeValueAsString(create);

        given(articleService.create(eq(EMAIL), any(ArticleDto.Create.class)))
                .willReturn(response);

        // when
        ResultActions result = mockMvc.perform(post("/api/articles")
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, VALID_TOKEN)
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8));

        // then
        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.article.id").value(1L))
                .andExpect(jsonPath("$.data.article.title").value(TITLE))
                .andExpect(jsonPath("$.data.article.content").value(CONTENT))
                .andDo(document("create-article",
                        ApiDocumentUtil.getDocumentRequest(),
                        ApiDocumentUtil.getDocumentResponse(),
                        requestHeaders(headerWithName(AUTHORIZATION).description("Basic auth credentials")),
                        requestFields(
                                fieldWithPath("title").type(JsonFieldType.STRING).description("제목"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("내용")
                        ),
                        responseFields(beneathPath("data").withSubsectionId("data"))
                                .andWithPrefix("article.",
                                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("아이디"),
                                        fieldWithPath("title").type(JsonFieldType.STRING).description("제목"),
                                        fieldWithPath("content").type(JsonFieldType.STRING).description("내용"),
                                        fieldWithPath("commentsCount").type(JsonFieldType.NUMBER).description("댓글 수"),
                                        fieldWithPath("articleViewCount").type(JsonFieldType.NUMBER).description("조회 수"),
                                        fieldWithPath("author").type(JsonFieldType.STRING).description("작성자"),
                                        fieldWithPath("createdAt").type(JsonFieldType.STRING)
                                                .attributes(DocumentFormatGenerator.getDateFormat())
                                                .description("생성시간"),
                                        fieldWithPath("modifiedAt").type(JsonFieldType.STRING)
                                                .attributes(DocumentFormatGenerator.getDateFormat())
                                                .description("수정시간")
                                )));

        verify(articleService).create(any(), any());
    }

    @Test
    void When_Request_All_Articles_With_Get_Method_Expect_HttpStatus_Is_OK() throws Exception {

        // given
        List<ArticleDto.ResponseAll> list = Arrays.asList(
                ArticleDto.ResponseAll.builder()
                        .id(2L)
                        .title(TITLE)
                        .author(AUTHOR)
                        .commentsCount(0L)
                        .articleViewCount(0L)
                        .createdAt(LocalDateTime.now())
                        .modifiedAt(LocalDateTime.now())
                        .build(),
                ArticleDto.ResponseAll.builder()
                        .id(1L)
                        .title(TITLE)
                        .author(AUTHOR)
                        .commentsCount(0L)
                        .articleViewCount(0L)
                        .createdAt(LocalDateTime.now())
                        .modifiedAt(LocalDateTime.now())
                        .build()
        );

        given(articleService.findAll(any(Pageable.class)))
                .willReturn(list);

        // when
        ResultActions result = mockMvc.perform(get("/api/articles?page=0&size=10")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .header(AUTHORIZATION, VALID_TOKEN));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.articles[0].id").value(2L))
                .andExpect(jsonPath("$.data.articles[1].id").value(1L))
                .andExpect(jsonPath("$.data.articles[0].content").doesNotHaveJsonPath())
                .andDo(document("read-all-articles",
                        ApiDocumentUtil.getDocumentRequest(),
                        ApiDocumentUtil.getDocumentResponse(),
                        requestParameters(
                                parameterWithName("page").description("페이지 번호").optional(),
                                parameterWithName("size").description("한 페이지 데이터 수").optional()
                        ),
                        responseFields(beneathPath("data").withSubsectionId("data"))
                                .andWithPrefix("articles.[].",
                                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("아이디"),
                                        fieldWithPath("title").type(JsonFieldType.STRING).description("제목"),
                                        fieldWithPath("author").type(JsonFieldType.STRING).description("작성자"),
                                        fieldWithPath("commentsCount").type(JsonFieldType.NUMBER).description("댓글 수"),
                                        fieldWithPath("articleViewCount").type(JsonFieldType.NUMBER).description("조회 수"),
                                        fieldWithPath("createdAt").type(JsonFieldType.STRING)
                                                .attributes(DocumentFormatGenerator.getDateFormat())
                                                .description("생성시간"),
                                        fieldWithPath("modifiedAt").type(JsonFieldType.STRING)
                                                .attributes(DocumentFormatGenerator.getDateFormat())
                                                .description("수정시간")
                                )));

        verify(articleService).findAll(any(Pageable.class));
    }

    @Test
    void When_Request_Article_Detail_With_Get_Method_Expect_HttpStatus_Is_OK() throws Exception {

        // given
        ArticleDto.ResponseDetail detail = ArticleDto.ResponseDetail.builder()
                .id(1L)
                .title(TITLE)
                .content(CONTENT)
                .author(AUTHOR)
                .commentsCount(0L)
                .articleViewCount(0L)
                .comments(new ArrayList<>())
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();

        given(articleService.findById(1L))
                .willReturn(detail);

        // when
        ResultActions result = mockMvc.perform(get("/api/articles/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.article.id").value(1L))
                .andExpect(jsonPath("$.data.article.content").value(CONTENT))
                .andDo(document("read-detail-article",
                        ApiDocumentUtil.getDocumentRequest(),
                        ApiDocumentUtil.getDocumentResponse(),
                        pathParameters(parameterWithName("id").description("게시글 id")),
                        responseFields(beneathPath("data").withSubsectionId("data"))
                                .andWithPrefix("article.",
                                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("아이디"),
                                        fieldWithPath("title").type(JsonFieldType.STRING).description("제목"),
                                        fieldWithPath("content").type(JsonFieldType.STRING).description("내용"),
                                        fieldWithPath("author").type(JsonFieldType.STRING).description("작성자"),
                                        fieldWithPath("commentsCount").type(JsonFieldType.NUMBER).description("댓글 수"),
                                        fieldWithPath("articleViewCount").type(JsonFieldType.NUMBER).description("조회 수"),
                                        fieldWithPath("comments").type(JsonFieldType.ARRAY).description("댓글"),
                                        fieldWithPath("createdAt").type(JsonFieldType.STRING)
                                                .attributes(DocumentFormatGenerator.getDateFormat())
                                                .description("생성시간"),
                                        fieldWithPath("modifiedAt").type(JsonFieldType.STRING)
                                                .attributes(DocumentFormatGenerator.getDateFormat())
                                                .description("수정시간")
                                )));

        verify(articleService).findById(anyLong());
    }

    @Test
    @WithCustomMockUser
    void When_Request_Update_Article_With_Patch_Method_Expect_HttpStatus_Is_OK() throws Exception {

        // given
        ArticleDto.ResponseChange response = ArticleDto.ResponseChange.builder()
                .id(1L)
                .title(PREFIX + TITLE)
                .content(PREFIX + CONTENT)
                .author(AUTHOR)
                .commentsCount(0L)
                .articleViewCount(0L)
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();

        ArticleDto.Update update = ArticleDto.Update.builder()
                .title(TITLE)
                .content(CONTENT)
                .build();

        String requestBody = objectMapper.writeValueAsString(update);

        given(articleService.update(eq(EMAIL), eq(1L), any(ArticleDto.Update.class)))
                .willReturn(response);

        // when
        ResultActions result = mockMvc.perform(patch("/api/articles/{id}", 1L)
                .content(requestBody)
                .header(AUTHORIZATION, VALID_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.article.title").value(PREFIX + TITLE))
                .andExpect(jsonPath("$.data.article.content").value(PREFIX + CONTENT))
                .andDo(document("update-article",
                        ApiDocumentUtil.getDocumentRequest(),
                        ApiDocumentUtil.getDocumentResponse(),
                        requestHeaders(headerWithName(AUTHORIZATION).description("Basic auth credentials")),
                        pathParameters(parameterWithName("id").description("게시글 id")),
                        requestFields(
                                fieldWithPath("title").type(JsonFieldType.STRING).description("제목"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("내용")
                        ),
                        responseFields(
                                beneathPath("data").withSubsectionId("data"))
                                .andWithPrefix("article.",
                                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("아이디"),
                                        fieldWithPath("title").type(JsonFieldType.STRING).description("제목"),
                                        fieldWithPath("content").type(JsonFieldType.STRING).description("내용"),
                                        fieldWithPath("author").type(JsonFieldType.STRING).description("작성자"),
                                        fieldWithPath("commentsCount").type(JsonFieldType.NUMBER).description("댓글 수"),
                                        fieldWithPath("articleViewCount").type(JsonFieldType.NUMBER).description("조회 수"),
                                        fieldWithPath("createdAt").type(JsonFieldType.STRING)
                                                .attributes(DocumentFormatGenerator.getDateFormat())
                                                .description("생성시간"),
                                        fieldWithPath("modifiedAt").type(JsonFieldType.STRING)
                                                .attributes(DocumentFormatGenerator.getDateFormat())
                                                .description("수정시간")
                                )));

        verify(articleService)
                .update(any(String.class), anyLong(), any(ArticleDto.Update.class));
    }

    @Test
    @WithCustomMockUser
    void When_Request_Delete_Article_With_Delete_Method_Expect_HttpStatus_Is_OK() throws Exception {
        // given
        doNothing()
                .when(articleService)
                .delete(EMAIL, 1L);

        // when
        ResultActions result = mockMvc.perform(delete("/api/articles/{id}", 1L)
                .header(AUTHORIZATION, VALID_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(ApiResponseCode.OK.getText()))
                .andDo(document("delete-article",
                        ApiDocumentUtil.getDocumentRequest(),
                        ApiDocumentUtil.getDocumentResponse(),
                        requestHeaders(headerWithName(AUTHORIZATION).description("Basic auth credentials")),
                        pathParameters(parameterWithName("id").description("게시글 id"))
                ));

        verify(articleService).delete(any(String.class), anyLong());
    }
}
