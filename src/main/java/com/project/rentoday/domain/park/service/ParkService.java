package com.project.rentoday.domain.park.service;


import com.project.rentoday.domain.district.entity.District;
import com.project.rentoday.domain.district.repository.DistrictRepository;
import com.project.rentoday.domain.member.entity.Member;
import com.project.rentoday.domain.member.exception.MemberErrorCode;
import com.project.rentoday.domain.member.exception.MemberException;
import com.project.rentoday.domain.member.repository.MemberRepository;

import com.project.rentoday.domain.park.client.OpenApiClient;
import com.project.rentoday.domain.park.client.OpenApiClientService;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.configurationprocessor.json.JSONObject;
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
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class ParkService {

    private final ParkRepository parkRepository;
    private final ParkImageRepository parkImageRepository;
    private final MemberRepository memberRepository;
    private final ReservationRepository reservationRepository;
    private final OpenApiClientService openApiClientService;
    private final DistrictRepository districtRepository;
    private final FileUploadService fileUploadService;

    @Transactional
    public void register(final CreateParkRequest request) {

        //회원 조회
        Member member = memberRepository.findByEmail(request.getMember())
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND_ERROR));

        String replaceAdd = request.getParkAdd().replaceAll("\\s+", "");
        try {
            OpenApiResponse apiResponse = openApiClientService.getTotalPages(request.getParkNum(), replaceAdd);
            request.setParkNum(apiResponse.getPrkcmprtNo());
            request.setLatitude(apiResponse.getLatitude());
            request.setLongitude(apiResponse.getLongitude());
            request.setAddress(apiResponse.getRdnmadr());
            request.setInstitutionNm(apiResponse.getInstitutionNm());
            request.setPhone(apiResponse.getPhoneNumber());
        }catch (IOException e) {
            e.getMessage();
            e.getStackTrace();
        }
        System.out.println(request.getPdf());

        String fileName = null;
        if (request.getPdf() != null) {
            try {
                fileName = fileUploadService.pdfUpload(request.getPdf());
            } catch (IOException e) {
                e.getMessage();
                e.getStackTrace();
            }
        }

        //Park 엔티티 생성
        Park park = Park.builder()
                .member(member)
                .carNum(request.getCarNum())
                .parkingNum(request.getParkNum())
                .address(request.getAddress())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .price(request.getPrice())
                .content(request.getContent())
                .agency(request.getInstitutionNm())
                .agNum(request.getPhone())
                .confirmation(fileName)
                .build();

        //주차 이미지 처리
        saveImage(park, request.getPhoto());

        //주차장 상태 초기 설정
        parkRepository.save(park);

        //주차장 정보 저장

    }

    private void saveImage(Park park, MultipartFile[] images) {
        if (images != null) {
            for (MultipartFile photo : images) {
                try {
                    String fileName = fileUploadService.profileImageUpload(photo);
                    ParkImage image = ParkImage.builder()
                            .park(park)
                            .parkingImageUrl(fileName)
                            .build();
                    parkImageRepository.save(image);
                } catch (IOException e) {
                    e.getMessage();
                    e.getStackTrace();
                }
            }
        }
    }


    @Transactional(readOnly = true)
    public ParkDetailsDto getParkDetailsWithReservation(Long parkId, String reservationUid) {
        Park park = parkRepository.findById(parkId)
                .orElseThrow(() -> new IllegalArgumentException("해당 주차장을 찾을 수 없습니다. ID: " + parkId));

        Reservation reservation = reservationRepository.findByReservationUid(reservationUid)
                .orElseThrow(() -> new IllegalArgumentException("해당 예약을 찾을 수 없습니다. UID: " + reservationUid));

        Member member = reservation.getMember();

        return ParkDetailsDto.builder()
                .parkId(park.getId())
                .parkingNum(park.getParkingNum())
                .parkStartTime(reservation.getCheckIn())
                .parkEndTime(reservation.getCheckOut())
                .price(reservation.getAmount())
                .agency(park.getAgency())
                .agencyPhone(park.getAgNum())
                .description(park.getContent())
                .reservationUid(reservation.getReservationUid())
                .reservationStartTime(reservation.getCheckIn())
                .reservationEndTime(reservation.getCheckOut())
                .buyerEmail(member.getEmail())
                .buyerName(member.getName())
                .build();
    }

    @Transactional(readOnly = true)
    public ParkDetailRequest getParkDetail(Long parkId) {
        Park park = parkRepository.findById(parkId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid park id: " + parkId));

        return new ParkDetailRequest(park);
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

        List<DistrictDto> filteredParks = availableParks.stream()
                .map(park -> new DistrictDto(park, district))
                .collect(Collectors.toList());

        return new DistrictResponse(district.getLatitude(), district.getLongitude(), filteredParks);
    }

    @Transactional(readOnly = true)
    public List<String> getAvailableTimes(Long parkId) {
        Park park = parkRepository.findById(parkId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid park Id:" + parkId));

        LocalTime startTime = park.getStartTime();
        LocalTime endTime = park.getEndTime();

        List<String> allTimes = generateTimeSlots(startTime, endTime);
        List<String> reservedTimes = getReservedTimes(parkId);

        allTimes.removeAll(reservedTimes);

        return allTimes;
    }



    @Transactional(readOnly = true)
    public List<String> getReservedTimes(Long parkId) {
        LocalTime startTime = LocalTime.MIN;
        LocalTime endTime = LocalTime.MAX;

        return reservationRepository.findByParkIdAndCheckInBetween(
                        parkId,
                        startTime,
                        endTime
                ).stream()
                .map(reservation -> reservation.getCheckIn().format(DateTimeFormatter.ofPattern("HH:mm")))
                .collect(Collectors.toList());
    }

    private List<String> generateTimeSlots(LocalTime startTime, LocalTime endTime) {
        List<String> timeSlots = new ArrayList<>();
        LocalTime currentTime = startTime;

        while (currentTime.isBefore(endTime)) {
            timeSlots.add(currentTime.format(DateTimeFormatter.ofPattern("HH:mm")));
            currentTime = currentTime.plusHours(1);
        }

        return timeSlots;
    }
}
