package com.project.rentoday.domain.park.controller;

import com.project.rentoday.domain.park.dto.CreateParkRequest;
import com.project.rentoday.domain.park.dto.ParkResponse;
import com.project.rentoday.domain.park.dto.UpdateParkRequest;
import com.project.rentoday.domain.park.entity.Park;
import com.project.rentoday.domain.park.service.ParkService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    @GetMapping("/{memberId}")
    @ResponseStatus(HttpStatus.OK)
    public List<ParkResponse> getParkByMember(@PathVariable Long memberId) {
        List<ParkResponse> parkResponses = parkService.getParkByMember(memberId);
        return parkResponses;
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

    @GetMapping("/confirmed")
    @ResponseStatus(HttpStatus.OK)
    public List<ParkResponse> getConfirmedParks() {
        return parkService.getConfirmedParks();
    }
}
