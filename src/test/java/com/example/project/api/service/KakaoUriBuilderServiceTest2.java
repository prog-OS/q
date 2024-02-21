package com.example.project.api.service;

import org.junit.jupiter.api.*;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class KakaoUriBuilderServiceTest2 {

    private KakaoUriBuilderService kakaoUriBuilderService;

    @BeforeEach
    void setting() {
        kakaoUriBuilderService = new KakaoUriBuilderService();
    }

    @DisplayName("buildUriByAddressSearch - 한글 파라미터의 경우 정상적으로 인코딩")
    @Test
    void UriByAddress() {
        // given
        String address = "서울 성북구";
        String result = "https://dapi.kakao.com/v2/local/search/address.json?query=서울 성북구";

        // when
        URI uri = kakaoUriBuilderService.buildUriByAddressSearch(address);
        String decodedResult = URLDecoder.decode(uri.toString(), StandardCharsets.UTF_8);

        // then
        assertThat(decodedResult).isEqualTo(result);
    }

}
