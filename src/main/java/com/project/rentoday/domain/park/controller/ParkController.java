package com.project.rentoday.domain.park.controller;

import com.project.rentoday.domain.park.dto.*;
import com.project.rentoday.domain.park.entity.Park;
import com.project.rentoday.domain.park.service.ParkService;
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

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/parks")
@Api(tags = "Park")
public class ParkController {

    private final ParkService parkService;

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

    @GetMapping("/{parkId}/available-times")
    public ResponseEntity<List<String>> getAvailableTimes(@PathVariable Long parkId) {
        List<String> availableTimes = parkService.getAvailableTimes(parkId);
        System.out.println("availableTimes = " + availableTimes);
        return ResponseEntity.ok(availableTimes);
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
