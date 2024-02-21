package com.example.project.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class KakaoApiResponseDto {

    @JsonProperty("meta")
    private MetaDto metaDto;

    /* List로 받는 이유는 문자열 기반 주소를 입력을 해서 요청을 할 때 애매하게 서울대학교 이런식으로 요청을 하게되면
       인근 지역이 다 검색이 된다. 그래서 리스트로 넘겨주는 것이다.
     */
    @JsonProperty("documents")
    private List<DocumentDto> documentList;
}
