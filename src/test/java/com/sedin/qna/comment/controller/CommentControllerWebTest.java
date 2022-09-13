package com.sedin.qna.comment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sedin.qna.account.model.Account;
import com.sedin.qna.comment.model.CommentDto;
import com.sedin.qna.comment.service.CommentService;
import com.sedin.qna.interceptor.AuthenticationInterceptor;
import com.sedin.qna.util.ApiDocumentUtil;
import com.sedin.qna.util.DocumentFormatGenerator;
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
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.beneathPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
@MockBean(JpaMetamodelMappingContext.class)
@ExtendWith({RestDocumentationExtension.class})
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "docs.api.com")
class CommentControllerWebTest {

    private static final Long AUTHORIZED_ID = 1L;
    private static final Long ARTICLE_ID = 1L;
    private static final String AUTHORIZATION = "Authorization";
    private static final String CONTENT = "content";
    private static final String NAME = "author";
    private static final String TITLE = "title";
    private static final String VALID_TOKEN = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhY2NvdW50SWQiOjF9." +
            "LwF0Ms-3xGGJX9JBIrc7bzGl1gYUAq3R3gesg35BA1w";

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationInterceptor authenticationInterceptor;

    @MockBean
    private CommentService commentService;

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
    void When_Request_Create_Comment_With_Post_Method_And_Article_Id_Expect_HttpStatus_Is_Created() throws Exception {

        // given
        CommentDto.Response response = CommentDto.Response.builder()
                .id(1L)
                .content(CONTENT)
                .author(NAME)
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();

        given(commentService.create(eq(authenticatedAccount), eq(ARTICLE_ID), any(CommentDto.Create.class)))
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
                        responseFields(
                                beneathPath("data").withSubsectionId("data"),
                                fieldWithPath("comment.id").type(JsonFieldType.NUMBER).description("아이디"),
                                fieldWithPath("comment.content").type(JsonFieldType.STRING).description("내용"),
                                fieldWithPath("comment.author").type(JsonFieldType.STRING).description("작성자"),
                                fieldWithPath("comment.createdAt").type(JsonFieldType.STRING)
                                        .attributes(DocumentFormatGenerator.getDateFormat())
                                        .description("생성시간"),
                                fieldWithPath("comment.modifiedAt").type(JsonFieldType.STRING)
                                        .attributes(DocumentFormatGenerator.getDateFormat())
                                        .description("수정시간")
                        )));

        verify(commentService, times(1))
                .create(eq(authenticatedAccount), eq(ARTICLE_ID), any(CommentDto.Create.class));
    }
}
