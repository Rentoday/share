package com.project.rentoday.domain.park.service;


import com.project.rentoday.domain.member.entity.Member;
import com.project.rentoday.domain.member.exception.MemberNotFoundException;
import com.project.rentoday.domain.member.repository.MemberRepository;

import com.project.rentoday.domain.park.client.OpenApiClient;
import com.project.rentoday.domain.park.dto.CreateParkRequest;
import com.project.rentoday.domain.park.dto.ParkResponse;
import com.project.rentoday.domain.park.dto.UpdateParkRequest;
import com.project.rentoday.domain.park.entity.Park;
import com.project.rentoday.domain.park.entity.ParkImage;
import com.project.rentoday.domain.park.entity.ParkStatus;
import com.project.rentoday.domain.park.exception.EndTimeBeforeStartTimeException;
import com.project.rentoday.domain.park.exception.ParkIdNotFoundException;
import com.project.rentoday.domain.park.exception.ParkingNumException;
import com.project.rentoday.domain.park.exception.PriceUnderZeroException;
import com.project.rentoday.domain.park.repository.ParkRepository;
import com.project.rentoday.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@Service
@RequiredArgsConstructor
public class ParkService {

    private final ParkRepository parkRepository;
    private final MemberRepository memberRepository;
    private final OpenApiClient openApiClient;

    @Transactional
    public Park register(final CreateParkRequest request) {
        //구획번호 유효성 검사부터
        boolean isValid = openApiClient.validateParkNum(request.getParkNum());
        if (!isValid) {
            throw new ParkingNumException(ErrorCode.INVALID_PARK_NUM);
        }
        //회원 조회
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new MemberNotFoundException(ErrorCode.INVALID_MEMBER));
        //주차 엔티티 생성 및 저장
        Park park = request.toEntity();
        return parkRepository.save(park);
    }

    @Transactional(readOnly = true)
    public List<ParkResponse> getParkByMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(ErrorCode.INVALID_MEMBER));
        List<Park> parks = parkRepository.findByMember(member);

        return IntStream.range(0, parks.size())
                .mapToObj(i -> {
                    Park park = parks.get(i);
                    if (park.getParkStatus() == ParkStatus.CONFIRMED) {
                        return ParkResponse.readPark()
                                .id((long) (i+1))
                                .park(park)
                                .confirmDate(park.getLastModifiedDate())
                                .build();
                    } else {
                        return ParkResponse.checkPark()
                                .id((long) (i+1))
                                .park(park)
                                .build();
                    }
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public Park update(UpdateParkRequest request, Long parkId) {
        Park park = parkRepository.findById(parkId)
                .orElseThrow(() -> new ParkIdNotFoundException(ErrorCode.INVALID_PARK_ID));

        if (request.getEndTime().isBefore(request.getStartTime())) {
            throw new EndTimeBeforeStartTimeException(ErrorCode.INVALID_END_TIME);
        }

        if (request.getPrice() <= 0) {
            throw new PriceUnderZeroException(ErrorCode.INVALID_PRICE);
        }

        //새로운 값으로 주차 상품 업데이트
        park.updateParkInfo(request.getStartTime(), request.getEndTime(), request.getPrice(), request.getContent());
        //주차 이미지 업데이트
        if (request.getImages() != null) {
            park.getParkImages().clear();
            for (ParkImage parkImage : request.getImages()) {
                park.addParkImages(parkImage);
            }
        }

        //주차확인증 업데이트
        if (request.getConfirmation() != null) {
            park.setConfirmation(request.getConfirmation());
        }

        return parkRepository.save(park);
    }

    @Transactional
    public void delete(Long parkId) {
        Park park = parkRepository.findById(parkId)
                .orElseThrow(() -> new ParkIdNotFoundException(ErrorCode.INVALID_PARK_ID));
        parkRepository.delete(park);
    }



}
