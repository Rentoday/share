package com.project.rentoday.domain.park.controller;

import com.project.rentoday.domain.park.dto.*;
import com.project.rentoday.domain.park.entity.Park;
import com.project.rentoday.domain.park.service.ParkService;
import com.project.rentoday.domain.reservation.entity.Reservation;
import com.project.rentoday.domain.reservation.repository.ReservationRepository;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/parks")
@Api(tags = "Park")
public class ParkController {

    private final ParkService parkService;
    private final ReservationRepository reservationRepository;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public void registerPark(@RequestPart(value = "key") CreateParkRequest createRequest,
                             @RequestPart(value = "photo", required = false) MultipartFile[] photos,
                             @RequestPart(value = "pdf", required = false) MultipartFile pdf,
                             @AuthenticationPrincipal UserDetails principal) {
        String email = principal.getUsername();
        createRequest.setMember(email);
        createRequest.setPdf(pdf);
        createRequest.setPhoto(photos);
        parkService.register(createRequest);
    }



    //멤버가 등록한 주차
    @GetMapping("/member")
    @ResponseStatus(HttpStatus.OK)
    public Page<ParkResponse> getParkByMember(
            @AuthenticationPrincipal UserDetails principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        String email = principal.getUsername();
        return parkService.getParkByMember(email, page, size);
    }

    @PatchMapping("/{parkId}")
    @ResponseStatus(HttpStatus.OK)
    public Park updatePark(@PathVariable Long parkId, @RequestBody UpdateParkRequest request) {
        Park park = parkService.update(request, parkId);
        return park;
    }

    @DeleteMapping("/{parkId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePark(@PathVariable Long parkId) {
        parkService.delete(parkId);
    }

    //멤버가 소유한 승인된 주차
    @GetMapping("/confirmed/member")
    @ResponseStatus(HttpStatus.OK)
    public Page<ParkResponse> getConfirmedParksByMember(
            @AuthenticationPrincipal UserDetails principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        String email = principal.getUsername();
        return parkService.getConfirmedParksByMember(email, page, size);
    }

    @GetMapping("/confirmed")
    @ResponseStatus(HttpStatus.OK)
    public Page<ParkResponse> getConfirmedAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return parkService.getConfirmedAll(pageable);
    }

    @GetMapping("/page")
    @ResponseStatus(HttpStatus.OK)
    public Page<ParkResponse> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return parkService.getAll(pageable);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<ParkResponse> getAllParks() {
        return parkService.getAllParks();
    }

    @GetMapping("/{parkId}")
    public ResponseEntity<ParkDetailRequest> getParkDetail(@PathVariable("parkId") Long parkId) {
        ParkDetailRequest parkDetail = parkService.getParkDetail(parkId);
        return ResponseEntity.ok(parkDetail);
    }

    //판매 가능 시간을 시간 단위로 추출하는 요청 (timeSlot)
    @GetMapping("/{parkId}/available-times")
    public ResponseEntity<Map<String, Object>> getAvailableTimes(@PathVariable Long parkId) {
        List<String> availableTimes = parkService.getAvailableTimes(parkId);
        List<Reservation> reservations = reservationRepository.findByParkId(parkId);

        List<String> reservedTimes = reservations.stream()
                        .map(reservation -> reservation.getCheckIn().toString())
                        .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("availableTimes", availableTimes);
        response.put("reservedTimes", reservedTimes);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{parkId}/details")
    public ResponseEntity<ParkDetailsDto> getParkDetails(@PathVariable Long parkId, @RequestParam String reservationUid) {
        try {
            ParkDetailsDto details = parkService.getParkDetailsWithReservation(parkId, reservationUid);
            return ResponseEntity.ok(details);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/filter")
    public ResponseEntity<DistrictResponse> getFilteredParks(
            @RequestParam("address") String address,
            @RequestParam("time") String time) {
        DistrictResponse response = parkService.getFilteredParks(address, time);
        return ResponseEntity.ok(response);
    }
}
