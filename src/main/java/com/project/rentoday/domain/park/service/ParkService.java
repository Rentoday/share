package com.project.rentoday.domain.park.service;


import com.project.rentoday.domain.district.entity.District;
import com.project.rentoday.domain.district.repository.DistrictRepository;
import com.project.rentoday.domain.member.entity.Member;
import com.project.rentoday.domain.member.exception.MemberErrorCode;
import com.project.rentoday.domain.member.exception.MemberException;
import com.project.rentoday.domain.member.repository.MemberRepository;

import com.project.rentoday.domain.park.client.OpenApiClient;
import com.project.rentoday.domain.park.dto.*;
import com.project.rentoday.domain.park.entity.Park;
import com.project.rentoday.domain.park.entity.ParkImage;
import com.project.rentoday.domain.park.entity.ParkStatus;
import com.project.rentoday.domain.park.exception.*;
import com.project.rentoday.domain.park.repository.ParkImageRepository;
import com.project.rentoday.domain.park.repository.ParkRepository;
import com.project.rentoday.domain.reservation.entity.Reservation;
import com.project.rentoday.domain.reservation.repository.ReservationRepository;
import com.project.rentoday.global.exception.ErrorCode;
import com.project.rentoday.global.file.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ParkService {

    private final ParkRepository parkRepository;
    private final ParkImageRepository parkImageRepository;
    private final MemberRepository memberRepository;
    private final ReservationRepository reservationRepository;
    private final OpenApiClient openApiClient;
    private final DistrictRepository districtRepository;
    private final FileUploadService fileUploadService;

    @Transactional
    public Park register(final CreateParkRequest request) {
        //입력값 유효성 검사부터
        validateRequest(request);

        //회원 조회
        Member member = memberRepository.findByEmail(request.getMember())
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND_ERROR));

        //주차 구획번호 유효성 검사
        validateParkNum(request.getParkNum());

        //주차장 위치 정보 조회 (OpenApi)
        ParkLocationInfo parkLocationInfo = getParkLocationInfo(request.getParkNum());

        //Park 엔티티 생성
        Park park = Park.builder()
                .member(member)
                .carNum(request.getCarNum())
                .parkingNum(request.getParkNum())
                .address(parkLocationInfo.getAddress())
                .latitude(parkLocationInfo.getLatitude())
                .longitude(parkLocationInfo.getLongitude())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .price(request.getPrice())
                .content(request.getContent())
                .build();

        //주차 이미지 처리
        if (request.getParkImages() != null) {
            for (MultipartFile photo : request.getPhoto()) {
                try {
                    String fileName = fileUploadService.profileImageUpload(photo);
                    ParkImage images = ParkImage.builder()
                                    .park(park)
                                    .parkingImageUrl(fileName)
                                    .build();
                    parkImageRepository.save(images);
                } catch (IOException e) {
                    e.getMessage();
                    e.getStackTrace();
                }
            }
        }

        if (request.getPdf() != null) {
            try {
                String fileName = fileUploadService.pdfUpload(request.getPdf());
                park.setConfirmation(fileName);
            } catch (IOException e) {
                e.getMessage();
                e.getStackTrace();
            }
        }

        //주차장 상태 초기 설정
        return parkRepository.save(park);

        //주차장 정보 저장

    }

    @Transactional(readOnly = true)
    public ParkDetailRequest getParkDetail(Long parkId) {
        Park park = parkRepository.findById(parkId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid park id: " + parkId));

        return new ParkDetailRequest(park);
    }
    @Transactional(readOnly = true)
    public List<String> getReservedTimes(Long parkId) {
        LocalTime startTime = LocalTime.of(0, 0); // 오늘 00:00
        LocalTime endTime = LocalTime.of(23, 59); // 오늘 23:59

        return reservationRepository.findByParkIdAndCheckInBetween(
                        parkId,
                        startTime,
                        endTime
                ).stream()
                .map(reservation -> reservation.getCheckIn().format(DateTimeFormatter.ofPattern("HH:mm")))
                .collect(Collectors.toList());

    }

    @Transactional(readOnly = true)
    public Page<ParkResponse> getParkByMember(String email, int page, int size) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND_ERROR));
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Park> parks = parkRepository.findByMember(member, pageable);

        return parks.map(park -> {
            if (park.getParkStatus() == ParkStatus.CONFIRMED) {
                return ParkResponse.readPark()
                        .id(park.getId())
                        .park(park)
                        .confirmDate(park.getLastModifiedDate())
                        .build();
            } else {
                return ParkResponse.checkPark()
                        .id(park.getId())
                        .park(park)
                        .build();
            }
        });
    }

    @Transactional
    public List<ParkResponse> getAllParks() {
        List<Park> allParks = parkRepository.findAll();
        return allParks.stream()
                .map(park -> new ParkResponse(park))
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

    @Transactional(readOnly = true)
    public Page<ParkResponse> getConfirmedParksByMember(String email, int page, int size) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND_ERROR));
        Pageable pageable = PageRequest.of(page, size, Sort.by("lastModifiedDate").descending());
        Page<Park> confirmedParks = parkRepository.findByMemberAndParkStatus(member, ParkStatus.CONFIRMED, pageable);

        return confirmedParks.map(park -> ParkResponse.checkPark()
                .id(park.getId())
                .park(park)
                .build());
    }

    @Transactional
    public Page<ParkResponse> getConfirmedAll(Pageable pageable) {
        Page<Park> confirmedParks = parkRepository.findByParkStatus(ParkStatus.CONFIRMED, pageable);

        return confirmedParks.map(park -> ParkResponse.readPark()
                .id(park.getId())
                .park(park)
                .build());
    }

    @Transactional
    public Page<ParkResponse> getAll(Pageable pageable) {
        Page<Park> allParks = parkRepository.findAll(pageable);
        return allParks.map(ParkResponse::new);
    }

    @Transactional
    public void delete(Long parkId) {
        Park park = parkRepository.findById(parkId)
                .orElseThrow(() -> new ParkIdNotFoundException(ErrorCode.INVALID_PARK_ID));
        parkRepository.delete(park);
    }

    @Transactional(readOnly = true)
    public DistrictResponse getFilteredParks(String address, String time) {
        LocalTime searchTime = LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"));

        District district = districtRepository.findByName(address)
                .orElseThrow(() -> new IllegalArgumentException("해당되는 구가 존재하지 않습니다."));

        List<Park> availableParks = parkRepository.findAvailableParks(address, searchTime);

        System.out.println("Address: " + address);
        System.out.println("Search Time: " + searchTime);
        System.out.println("Available Parks: " + availableParks.size());

        List<DistrictDto> filteredParks = availableParks.stream()
                .map(park -> new DistrictDto(park, district))
                .collect(Collectors.toList());

        return new DistrictResponse(district.getLatitude(), district.getLongitude(), filteredParks);
    }

    @Transactional
    public List<String> getAvailableTimes(Long parkId) {
        Park park = parkRepository.findById(parkId).orElseThrow(() -> new ParkIdNotFoundException(ErrorCode.INVALID_PARK_ID));
        LocalTime startTime = park.getStartTime();
        LocalTime endTime = park.getEndTime();

        List<Reservation> reservations = reservationRepository.findByParkIdAndCheckInBetween(
                parkId,
                startTime,
                endTime
        );

        List<LocalTime> reservedTimes = reservations.stream()
                .flatMap(reservation -> {
                    LocalTime checkIn = reservation.getCheckIn();
                    LocalTime checkOut = reservation.getCheckOut();
                    List<LocalTime> times = new ArrayList<>();
                    for (LocalTime time = checkIn; time.isBefore(checkOut.plusMinutes(1)); time = time.plusHours(1)) {
                        times.add(time);
                    }
                    return times.stream();
                })
                .collect(Collectors.toList());

        List<String> availableTimes = new ArrayList<>();
        for (LocalTime time = startTime; time.isBefore(endTime.plusMinutes(1)); time = time.plusHours(1)) {
            if (!reservedTimes.contains(time)) {
                availableTimes.add(time.format(DateTimeFormatter.ofPattern("HH:mm")));
            }
        }

        return availableTimes;
    }

    private boolean isParkAvailableAtTime(Park park, LocalTime searchTime) {
        LocalTime parkStartTime = park.getStartTime();
        LocalTime parkEndTime = park.getEndTime();
        return !searchTime.isBefore(parkStartTime) && !searchTime.isAfter(parkEndTime);
    }

    private void validateRequest(CreateParkRequest request) {
        if (request.getEndTime().isBefore(request.getStartTime())) {
            throw new EndTimeBeforeStartTimeException(ErrorCode.INVALID_END_TIME);
        }
        if (request.getPrice() <= 0) {
            throw new PriceUnderZeroException(ErrorCode.INVALID_PRICE);
        }
    }

    private void validateParkNum(String parkNum) {
        boolean isValid = openApiClient.validateParkNum(parkNum);
        if (!isValid) {
            throw new ParkingNumException(ErrorCode.INVALID_PARK_NUM);
        }
    }

    private ParkLocationInfo getParkLocationInfo(String parkNum) {
        ParkLocationInfo parkLocationInfo = openApiClient.getParkLocationInfo(parkNum);
        if (parkLocationInfo == null) {
            throw new ParkLocationNotFoundException(ErrorCode.PARK_LOCATION_NOT_FOUND);
        }
        return parkLocationInfo;
    }
}
