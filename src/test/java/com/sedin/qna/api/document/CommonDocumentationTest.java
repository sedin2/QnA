package com.sedin.qna.api.document;

import com.sedin.qna.account.model.Gender;
import com.sedin.qna.api.document.controller.RestDocsController;
import com.sedin.qna.athentication.service.AuthenticationService;
import com.sedin.qna.network.ApiResponseCode;
import com.sedin.qna.util.CustomResponseFieldsSnippet;
import com.sedin.qna.util.EnumType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.PayloadSubsectionExtractor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Arrays;
import java.util.Map;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.beneathPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.restdocs.snippet.Attributes.attributes;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs
@WebMvcTest(RestDocsController.class)
@ExtendWith({RestDocumentationExtension.class})
public class CommonDocumentationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationService authenticationService;

    @Test
    void common() throws Exception {
        ResultActions result = this.mockMvc.perform(get("/docs")
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk())
                .andDo(document("common",
                        customResponseFields("custom-response", null,
                                attributes(key("title").value("공통응답")),
                                subsectionWithPath("data").description("데이터"),
                                fieldWithPath("code").type(JsonFieldType.STRING).description("결과 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지")
                        ),
                        customResponseFields("custom-response",
                                beneathPath("data.apiResponseCodes").withSubsectionId("apiResponseCodes"),
                                attributes(key("title").value("응답 코드")),
                                enumConvertFieldDescriptor(ApiResponseCode.values())
                        ),
                        customResponseFields("custom-response", beneathPath("data.genders").withSubsectionId("genders"),
                                attributes(key("title").value("성별")),
                                enumConvertFieldDescriptor(Gender.values())
                        )
                ));
    }

    public static CustomResponseFieldsSnippet customResponseFields(String type,
                                                                   PayloadSubsectionExtractor<?> subsectionExtractor,
                                                                   Map<String, Object> attributes,
                                                                   FieldDescriptor... descriptors) {
        return new CustomResponseFieldsSnippet(
                type,
                subsectionExtractor,
                Arrays.asList(descriptors),
                attributes,
                true
        );
    }

    private FieldDescriptor[] enumConvertFieldDescriptor(EnumType[] enumTypes) {
        return Arrays.stream(enumTypes)
                .map(enumType -> fieldWithPath(enumType.getId()).description(enumType.getText()))
                .toArray(FieldDescriptor[]::new);
    }
}
