package com.project.rentoday.domain.park.controller;

import com.project.rentoday.domain.park.dto.CreateParkRequest;
import com.project.rentoday.domain.park.dto.ParkResponse;
import com.project.rentoday.domain.park.dto.UpdateParkRequest;
import com.project.rentoday.domain.park.entity.Park;
import com.project.rentoday.domain.park.service.ParkService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

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
    public List<ParkResponse> getConfirmedAll() {
        return parkService.getConfirmedAll();
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ParkResponse> getAll() {
        return parkService.getAll();
    }
}
