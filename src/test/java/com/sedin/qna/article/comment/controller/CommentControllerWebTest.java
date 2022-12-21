package com.sedin.qna.article.comment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sedin.qna.account.model.Account;
import com.sedin.qna.article.comment.model.CommentDto;
import com.sedin.qna.article.comment.service.CommentService;
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
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
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
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
@MockBean(JpaMetamodelMappingContext.class)
@ExtendWith(RestDocumentationExtension.class)
@Import({
        SecurityConfiguration.class,
        JwtTokenProvider.class
})
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "docs.api.com")
class CommentControllerWebTest {

    private static final Long AUTHORIZED_ID = 1L;
    private static final Long ARTICLE_ID = 1L;
    private static final String AUTHORIZATION = "Authorization";
    private static final String EMAIL = "cafe@mocha.com";
    private static final String CONTENT = "content";
    private static final String NAME = "author";
    private static final String PREFIX = "prefix";
    private static final String VALID_TOKEN = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhY2NvdW50SWQiOjF9." +
            "LwF0Ms-3xGGJX9JBIrc7bzGl1gYUAq3R3gesg35BA1w";

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CommentService commentService;

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
    void When_Request_Create_Comment_With_Post_Method_And_Article_Id_Expect_HttpStatus_Is_Created() throws Exception {
        // given
        CommentDto.Response response = CommentDto.Response.builder()
                .id(1L)
                .content(CONTENT)
                .author(NAME)
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();

        given(commentService.create(eq(EMAIL), eq(ARTICLE_ID), any(CommentDto.Create.class)))
                .willReturn(response);

        // when
        CommentDto.Create create = CommentDto.Create.builder()
                .content(CONTENT)
                .build();

        String requestBody = objectMapper.writeValueAsString(create);

        ResultActions result = mockMvc.perform(post("/api/articles/{articleId}/comments", ARTICLE_ID)
                .content(requestBody)
                .header(AUTHORIZATION, VALID_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8));

        // then
        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.comment.id").value(1L))
                .andExpect(jsonPath("$.data.comment.content").value(CONTENT))
                .andExpect(jsonPath("$.data.comment.author").value(NAME))
                .andDo(document("create-comment",
                                ApiDocumentUtil.getDocumentRequest(),
                                ApiDocumentUtil.getDocumentResponse(),
                                requestHeaders(headerWithName(AUTHORIZATION).description("Basic auth credentials")),
                                requestFields(
                                        fieldWithPath("content").type(JsonFieldType.STRING).description("내용")
                                ),
                                responseFields(beneathPath("data").withSubsectionId("data"))
                                        .andWithPrefix("comment.",
                                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("아이디"),
                                                fieldWithPath("content").type(JsonFieldType.STRING).description("내용"),
                                                fieldWithPath("author").type(JsonFieldType.STRING).description("작성자"),
                                                fieldWithPath("createdAt").type(JsonFieldType.STRING)
                                                        .attributes(DocumentFormatGenerator.getDateFormat())
                                                        .description("생성시간"),
                                                fieldWithPath("modifiedAt").type(JsonFieldType.STRING)
                                                        .attributes(DocumentFormatGenerator.getDateFormat())
                                                        .description("수정시간")
                                        )
                        )
                );

        verify(commentService, times(1))
                .create(eq(EMAIL), eq(ARTICLE_ID), any(CommentDto.Create.class));
    }

    @Test
    void When_Request_Read_All_Comments_With_Get_Method_Expect_HttpStatus_Is_OK() throws Exception {
        // given
        List<CommentDto.Response> responseList = List.of(CommentDto.Response.builder()
                        .id(1L)
                        .content(CONTENT)
                        .author(NAME)
                        .createdAt(LocalDateTime.now())
                        .modifiedAt(LocalDateTime.now())
                        .build(),
                CommentDto.Response.builder()
                        .id(2L)
                        .content(CONTENT)
                        .author(NAME)
                        .createdAt(LocalDateTime.now())
                        .modifiedAt(LocalDateTime.now())
                        .build());

        given(commentService.findAll(ARTICLE_ID))
                .willReturn(responseList);

        // when
        ResultActions result = mockMvc.perform(get("/api/articles/{articleId}/comments", ARTICLE_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.comments[0].id").value(1L))
                .andExpect(jsonPath("$.data.comments[0].content").value(CONTENT))
                .andExpect(jsonPath("$.data.comments[0].author").value(NAME))
                .andDo(document("read-all-comments",
                                ApiDocumentUtil.getDocumentRequest(),
                                ApiDocumentUtil.getDocumentResponse(),
                                responseFields(beneathPath("data").withSubsectionId("data"))
                                        .andWithPrefix("comments.[].",
                                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("아이디"),
                                                fieldWithPath("content").type(JsonFieldType.STRING).description("내용"),
                                                fieldWithPath("author").type(JsonFieldType.STRING).description("작성자"),
                                                fieldWithPath("createdAt").type(JsonFieldType.STRING)
                                                        .attributes(DocumentFormatGenerator.getDateFormat())
                                                        .description("생성시간"),
                                                fieldWithPath("modifiedAt").type(JsonFieldType.STRING)
                                                        .attributes(DocumentFormatGenerator.getDateFormat())
                                                        .description("수정시간")
                                        )
                        )
                );

        verify(commentService, times(1)).findAll(ARTICLE_ID);
    }

    @Test
    void When_Request_Comment_Detail_With_Get_Method_Expect_HttpStatus_Is_OK() throws Exception {
        // given
        CommentDto.Response response = CommentDto.Response.builder()
                .id(1L)
                .content(CONTENT)
                .author(NAME)
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();

        given(commentService.findById(ARTICLE_ID, 1L))
                .willReturn(response);

        // when
        ResultActions result = mockMvc.perform(get("/api/articles/{articleId}/comments/{commentId}",
                ARTICLE_ID, 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.comment.id").value(1L))
                .andExpect(jsonPath("$.data.comment.content").value(CONTENT))
                .andExpect(jsonPath("$.data.comment.author").value(NAME))
                .andDo(document("read-detail-comment",
                                ApiDocumentUtil.getDocumentRequest(),
                                ApiDocumentUtil.getDocumentResponse(),
                                responseFields(beneathPath("data").withSubsectionId("data"))
                                        .andWithPrefix("comment.",
                                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("아이디"),
                                                fieldWithPath("content").type(JsonFieldType.STRING).description("내용"),
                                                fieldWithPath("author").type(JsonFieldType.STRING).description("작성자"),
                                                fieldWithPath("createdAt").type(JsonFieldType.STRING)
                                                        .attributes(DocumentFormatGenerator.getDateFormat())
                                                        .description("생성시간"),
                                                fieldWithPath("modifiedAt").type(JsonFieldType.STRING)
                                                        .attributes(DocumentFormatGenerator.getDateFormat())
                                                        .description("수정시간")
                                        )
                        )
                );

        verify(commentService, times(1)).findById(ARTICLE_ID, 1L);
    }

    @Test
    @WithCustomMockUser
    void When_Request_Update_Comment_With_Patch_Method_Expect_HttpStatus_Is_OK() throws Exception {
        // given
        CommentDto.Response response = CommentDto.Response.builder()
                .id(1L)
                .content(PREFIX + CONTENT)
                .author(NAME)
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();

        CommentDto.Update update = CommentDto.Update.builder()
                .content(PREFIX + CONTENT)
                .build();

        String requestBody = objectMapper.writeValueAsString(update);

        given(commentService.update(eq(EMAIL), eq(ARTICLE_ID), eq(1L), any(CommentDto.Update.class)))
                .willReturn(response);

        // when
        ResultActions result = mockMvc.perform(patch("/api/articles/{articleId}/comments/{commentId}",
                ARTICLE_ID, 1L)
                .content(requestBody)
                .header(AUTHORIZATION, VALID_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.comment.id").value(1L))
                .andExpect(jsonPath("$.data.comment.content").value(PREFIX + CONTENT))
                .andDo(document("update-comment",
                                ApiDocumentUtil.getDocumentRequest(),
                                ApiDocumentUtil.getDocumentResponse(),
                                requestHeaders(headerWithName(AUTHORIZATION).description("Basic auth credentials")),
                                pathParameters(
                                        parameterWithName("articleId").description("게시글 id"),
                                        parameterWithName("commentId").description("댓글 id")
                                ),
                                requestFields(
                                        fieldWithPath("content").type(JsonFieldType.STRING).description("내용")
                                ),
                                responseFields(beneathPath("data").withSubsectionId("data"))
                                        .andWithPrefix("comment.",
                                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("아이디"),
                                                fieldWithPath("content").type(JsonFieldType.STRING).description("내용"),
                                                fieldWithPath("author").type(JsonFieldType.STRING).description("작성자"),
                                                fieldWithPath("createdAt").type(JsonFieldType.STRING)
                                                        .attributes(DocumentFormatGenerator.getDateFormat())
                                                        .description("생성시간"),
                                                fieldWithPath("modifiedAt").type(JsonFieldType.STRING)
                                                        .attributes(DocumentFormatGenerator.getDateFormat())
                                                        .description("수정시간")
                                        )
                        )
                );

        verify(commentService, times(1))
                .update(eq(EMAIL), eq(ARTICLE_ID), eq(1L), any(CommentDto.Update.class));
    }

    @Test
    @WithCustomMockUser
    void When_Request_Delete_Comment_With_Delete_Method_Expect_HttpStatus_Is_OK() throws Exception {
        // given
        doNothing()
                .when(commentService)
                .delete(EMAIL, ARTICLE_ID, 1L);

        // when
        ResultActions result = mockMvc.perform(delete("/api/articles/{articleId}/comments/{commentId}",
                ARTICLE_ID, 1L)
                .header(AUTHORIZATION, VALID_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(ApiResponseCode.OK.getText()))
                .andDo(document("delete-comment",
                        ApiDocumentUtil.getDocumentRequest(),
                        ApiDocumentUtil.getDocumentResponse(),
                        requestHeaders(headerWithName(AUTHORIZATION).description("Basic auth credentials")),
                        pathParameters(
                                parameterWithName("articleId").description("게시글 id"),
                                parameterWithName("commentId").description("댓글 id")
                        )
                ));

        verify(commentService, times(1)).delete(any(String.class), anyLong(), anyLong());
    }
}
