package com.example.project.direction.service

import com.example.project.api.dto.DocumentDto
import com.example.project.api.service.KakaoCategorySearchService
import com.example.project.direction.repository.DirectionRepository
import com.example.project.pharmacy.dto.PharmacyDto
import com.example.project.pharmacy.service.PharmacySearchService
import spock.lang.Specification

class DirectionServiceTest extends Specification {

    private PharmacySearchService pharmacySearchService = Mock()
    private DirectionRepository directionRepository = Mock()
    private KakaoCategorySearchService kakaoCategorySearchService = Mock()
    private Base62Service base62Service = Mock()
    // PharmacySearchService는 의존성 주입을 받아서 진행하고 있다. 그래서 단위 테스트를 하기 위해 PharmacySearchService를 모킹해주어야 한다.
    private DirectionService directionService = new DirectionService(
            pharmacySearchService, directionRepository, kakaoCategorySearchService, base62Service)

    private List<PharmacyDto> pharmacyList

    def setup() {
        pharmacyList = new ArrayList<>()
        pharmacyList.addAll(
                PharmacyDto.builder()
                        .id(1L)
                        .pharmacyName("돌곶이온누리약국")
                        .pharmacyAddress("주소1")
                        .latitude(37.61040424)
                        .longitude(127.0569046)
                        .build(),
                PharmacyDto.builder()
                        .id(2L)
                        .pharmacyName("호수온누리약국")
                        .pharmacyAddress("주소2")
                        .latitude(37.60894036)
                        .longitude(127.029052)
                        .build()
        )
    }

    def "buildDirectionList - 결과 값이 거리 순으로 정렬이 되는지 확인"() {
        given:
        def addressName = "서울 성북구 종암로10길"
        double inputLatitude = 37.5960650456809
        double inputLongitude = 127.037033003036

        def documentDto = DocumentDto.builder()
                                        .addressName(addressName)
                                        .latitude(inputLatitude)
                                        .longitude(inputLongitude)
                                        .build()

        when:
        /**
         * DirectionService 안에 있는 PharmacySearchService는 mock 객체로 만드어서 실제 행위를 하는 것처럼 지정해줘야 한다.
         * 여기서 필요한 것은 DirectionService 안에 있는 searchPharmacyDtoList() 즉, 전체 약구 DTO를 가져오는 행위를 해줘야 한다.
         * 그러기 위해 위쪽 setup() 부분에 DTO 리스트를 만들어 두었다.
         * 그래서 이 PharmacySearchService인 Mock 객체가 dto 리스트를 조회해서 가져오는 것처럼 행위를 지정해주었다.
         * 이것을 스톡?이라 한다.
         *
         * pharmacySearchService.searchPharmacyDtoList()이 불릴 때 약국 리스트를 리턴해라 라는 행위를 지정
         * 실제로 아래 메소드가 호출될 때 스톱핑한 dto 리스트가 리턴된다.
         */
        pharmacySearchService.searchPharmacyDtoList() >> pharmacyList

        def result = directionService.buildDirectionList(documentDto)

        then:
        result.size() == 2
        result.get(0).targetPharmacyName == "호수온누리약국"
        result.get(1).targetPharmacyName == "돌곶이온누리약국"
    }

    def "buildDirectionList - 정해진 반경 10 km 내에 검색이 되는지 확인"() {
        given:
        pharmacyList.add(
                PharmacyDto.builder()
                        .id(3L)
                        .pharmacyName("경기약국")
                        .pharmacyAddress("주소3")
                        .latitude(37.3825107393401)
                        .longitude(127.236707811313)
                        .build())

        def addressName = "서울 성북구 종암로10길"
        double inputLatitude = 37.5960650456809
        double inputLongitude = 127.037033003036

        def documentDto = DocumentDto.builder()
                .addressName(addressName)
                .latitude(inputLatitude)
                .longitude(inputLongitude)
                .build()

        when:
        pharmacySearchService.searchPharmacyDtoList() >> pharmacyList

        def results = directionService.buildDirectionList(documentDto)

        then:
        results.size() == 2
        results.get(0).targetPharmacyName == "호수온누리약국"
        results.get(1).targetPharmacyName == "돌곶이온누리약국"
    }

}
