package com.example.project.direction.controller

import com.example.project.direction.dto.OutputDto
import com.example.project.pharmacy.service.PharmacyRecommendationService
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

class FormControllerTest extends Specification {

    private MockMvc mockMvc
    private PharmacyRecommendationService pharmacyRecommendationService = Mock()
    private List<OutputDto> outputDtoList

    def setup() {
        // FormController MockMvc 객체로 만든다.
        mockMvc = MockMvcBuilders.standaloneSetup(new FormController(pharmacyRecommendationService))
                                    .build()

        outputDtoList = new ArrayList<>()
        outputDtoList.addAll(
                OutputDto.builder()
                    .pharmacyName("pharmacy1")
                    .build(),
                OutputDto.builder()
                    .pharmacyName("pharmacy2")
                    .build()
        )
    }

    def "GET /"() {
        // 간단한 테스트의 경우 expect 만으로도 가능하다.
        // FormController 의 "/" URI를 get방식으로 호출
        expect:
        mockMvc.perform(get("/"))
            .andExpect(handler().handlerType(FormController.class))
            .andExpect(handler().methodName("main")) // public String main() {
            .andExpect(status().isOk())
            .andExpect(view().name("main"))
            .andDo(log())
    }

    def "POST /search"() {
        given:
        String inputAddress = "서울 성북구 종암동"

        when:
        def resultActions = mockMvc.perform(post("/search")
                .param("address", inputAddress))

        then:
        1 * pharmacyRecommendationService.recommendPharmacyList(argument -> {
            assert argument == inputAddress // mock 객체의 argument 검증
        }) >> outputDtoList // 해당 메소드(pharmacyRecommendationService.recommendPharmacyList())가 호출되면 응답값을 outputDtoList로 해주겠다.

        resultActions
        .andExpect(status().isOk())
        .andExpect(view().name("output"))
        .andExpect(model().attributeExists("outputFormList")) // attributeExists(String name) : name에 해당하는 데이터가 model에 있는지 검증 / model에 outputFormList라는 key가 존재하는지 확인
        .andExpect(model().attribute("outputFormList", outputDtoList)) // attribute(String name, Object value) : name에 해당하는 데이터가 value 객체인지 검증
        .andDo(print())
    }
}
