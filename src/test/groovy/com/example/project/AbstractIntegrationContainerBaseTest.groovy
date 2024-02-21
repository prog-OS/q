package com.example.project

import org.springframework.boot.test.context.SpringBootTest
import org.testcontainers.containers.GenericContainer
import spock.lang.Specification

// 이 AbstractIntegrationContainerBaseTest 를 상속 받으면 통합 테스트 환경에서 테스트를 진행할 수 있도록 구성
// @SpringBootTest 사용하여 스프링 컨테이너를 같이 띄워서 여러 모듈 간의 연동까지 검증할 수 있는 통합 테스트 환경을 구성
@SpringBootTest
abstract class AbstractIntegrationContainerBaseTest extends Specification {
    // mariadb의 경우 application.yml를 사용했지만 redis의 경우 아래와 같이 따로 추가해줘야 한다.
    // static을 사용해 테스트가 실행될 때 한번만 실행되도록 함
    static final GenericContainer MY_REDIS_CONTAINER

    static { // 인스턴스 화
        MY_REDIS_CONTAINER = new GenericContainer<>("redis:6") // docker image name 지정
            .withExposedPorts(6379) // docker에서 Expose한 포트이고, 호스트에서 포트는 테스트 컨테이너가 충돌되지 않는 포트를 이미 생성해서 .withExposedPorts(6379) 와 랩핑을 해준다.

        MY_REDIS_CONTAINER.start(); // redis를 시작해줌과 동시에 랜덤한 포트 맵핑

        // 스프링 부트 입장에서는 레디스와 통신하기 위해서 포트를 알아야 한다.
        // 즉 스프링 부트에게 맵핑된 포트를 알려줘야 스프링 부트가 레디스와 통신할 수 있다.
        System.setProperty("spring.redis.host", MY_REDIS_CONTAINER.getHost()) // 프로퍼티로 스프링부트에게 6379와 맵핑된 호스트와 포트를 알려준다. spring.redis.host 프로퍼티로 전달할 것이다.
        System.setProperty("spring.redis.port", MY_REDIS_CONTAINER.getMappedPort(6379).toString())
    }
}
