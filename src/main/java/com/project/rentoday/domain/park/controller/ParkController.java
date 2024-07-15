package com.project.rentoday.domain.park.controller;

import com.project.rentoday.domain.park.dto.CreateParkRequest;
import com.project.rentoday.domain.park.dto.ParkDetailRequest;
import com.project.rentoday.domain.park.dto.ParkResponse;
import com.project.rentoday.domain.park.dto.UpdateParkRequest;
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

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/parks")
@Api(tags = "Park")
public class ParkController {

    private final ParkService parkService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public Park registerPark(@RequestBody final CreateParkRequest request) {
        Park registeredPark = parkService.register(request);
        return registeredPark;
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

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<ParkResponse> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return parkService.getAll(pageable);
    }


    @GetMapping("/{parkId}")
    public ResponseEntity<ParkDetailRequest> getParkDetail(@PathVariable Long parkId) {
        ParkDetailRequest parkDetail = parkService.getParkDetail(parkId);
        return ResponseEntity.ok(parkDetail);
    }

    @GetMapping("/{parkId}/reserved-times")
    public ResponseEntity<List<LocalDateTime>> getReservedTimes(@PathVariable Long parkId) {
        List<LocalDateTime> reservedTimes = parkService.getReservedTimes(parkId);
        return ResponseEntity.ok(reservedTimes);
    }
}
