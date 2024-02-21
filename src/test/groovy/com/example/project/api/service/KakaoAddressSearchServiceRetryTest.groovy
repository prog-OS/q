package com.example.project.api.service

import com.example.project.AbstractIntegrationContainerBaseTest
import com.example.project.api.dto.DocumentDto
import com.example.project.api.dto.KakaoApiResponseDto
import com.example.project.api.dto.MetaDto
import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType

class KakaoAddressSearchServiceRetryTest extends AbstractIntegrationContainerBaseTest {

    @Autowired
    private KakaoAddressSearchService kakaoAddressSearchService

//    @MockBean // 스프링컨테이너 내에 있는 빈을 모킹한다.
    /**
     * 스프링 컨테이너 내에 있는 빈을 모킹하는 이유는
     * KakaoAddressSearchService 안에 requestAddressSearch() 안에 있는
     * URI uri = kakaoUriBuilderService.buildUriByAddressSearch(address);
     * 에서 uri는 실제 카카오 api를 호출하기 위한 uri이다. 하지만 지금은 로컬호스트에 떠있는 모킹 서버를 호출할 것이다.
     * 그렇게 하기 위해 KakaoUriBuilderService를 모킹해서 URI 자체를 로컬호스트에 떠있는 목 서버를 응답해 주도록 모킹함
     */
    @SpringBean
    private KakaoUriBuilderService kakaoUriBuilderService = Mock()

    private MockWebServer mockWebServer // 서버를 목킹할 수 있는 목웹 서버

    private ObjectMapper mapper = new ObjectMapper()

    private String inputAddress = "서울 성북구 종암로 10길"

    def setup() {
        mockWebServer = new MockWebServer()
        mockWebServer.start()
        println mockWebServer.port // 동적 할당
        println mockWebServer.url("/") // localhost에서 실행
    }

    def cleanup() {
        mockWebServer.shutdown()
    }

    def "requestAddressSearch retry success"() {
        given:
        def metaDto = new MetaDto(1)
        def documentDto = DocumentDto.builder()
                .addressName(inputAddress)
                .build()
        def expectedResponse = new KakaoApiResponseDto(metaDto, Arrays.asList(documentDto))
        def uri = mockWebServer.url("/").uri() // 목 웹 서버에서 사용하고 있는 uri를 모킹

        when:
        mockWebServer.enqueue(new MockResponse().setResponseCode(504)) // 첫 번째 호출시 504 에러 응답
        mockWebServer.enqueue(new MockResponse().setResponseCode(200)
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE) // 카카오 api를 호출하고 응답 받은 것처럼 헤더와 바디를 지정해줌
                .setBody(mapper.writeValueAsString(expectedResponse)))

        def kakaoApiResult = kakaoAddressSearchService.requestAddressSearch(inputAddress)

        then:
        // buildUriByAddressSearch() 가 실제로 2번 호출된 것이 맞는지 확인
        2 * kakaoUriBuilderService.buildUriByAddressSearch(inputAddress) >> uri // 목 웹 서버를 호출하기 위한 uri를 리턴하도록 지정
        kakaoApiResult.getDocumentList().size() == 1
        kakaoApiResult.getMetaDto().totalCount == 1
        kakaoApiResult.getDocumentList().get(0). getAddressName() == inputAddress
    }

    def "requestAddressSearch retry fail "() {
        given:
        def uri = mockWebServer.url("/").uri()

        when:
        mockWebServer.enqueue(new MockResponse().setResponseCode(504))
        mockWebServer.enqueue(new MockResponse().setResponseCode(504))

        def result = kakaoAddressSearchService.requestAddressSearch(inputAddress)

        then:
        2 * kakaoUriBuilderService.buildUriByAddressSearch(inputAddress) >> uri
        result == null
    }
}
