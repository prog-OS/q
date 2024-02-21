package com.example.project.api.service

import spock.lang.Specification
import java.nio.charset.StandardCharsets

class KakaoUriBuilderServiceTest extends Specification {

    private KakaoUriBuilderService kakaoUriBuilderService

    def setup() { // run before every feature method
        kakaoUriBuilderService = new KakaoUriBuilderService()
    }

    def "buildUriByAddressSearch - 한글 파라미터의 경우 정상적으로 인코딩"() {
        given:
        String address = "서울 성북구"
        String charset = StandardCharsets.UTF_8

        when:
        def uri = kakaoUriBuilderService.buildUriByAddressSearch(address)
        def decodedResult = URLDecoder.decode(uri.toString(), charset)

        then:
        print uri
        decodedResult == "https://dapi.kakao.com/v2/local/search/address.json?query=서울 성북구"



    }
}
