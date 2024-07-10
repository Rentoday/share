package com.project.rentoday.domain.park.client;

import com.project.rentoday.domain.park.dto.ParkLocationInfo;

/**
 * 의존성 역전 원칙. 고수준 모듈이 저수준 모듈에 직접 의존하지 않고, 추상화에 의존하게 된다. 이로 인해 코드의 유연성과 재사용성이 향상.
 * 인터페이스 사용 시 -> 실제 API를 호출하지 않는 Mock 객체를 쉽게 만들 수 있어 단위 테스트가 용이해진다.
 * 구현 교체 용이성 향상
 */
public interface OpenApiClient {
    boolean validateParkNum(String parkNum);

    ParkLocationInfo getParkLocationInfo(String parkNum);
}
