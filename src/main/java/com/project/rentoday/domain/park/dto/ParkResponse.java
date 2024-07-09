package com.project.rentoday.domain.park.dto;

import com.project.rentoday.domain.park.entity.Park;
import com.project.rentoday.domain.park.entity.ParkStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ParkResponse {
    private Long id;
    private String name;
    private String parkNum;
    private String address;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private double price;
    private LocalDateTime regDate;
    private LocalDateTime confirmDate;
    private ParkStatus parkStatus;

    @Builder(builderMethodName = "checkPark")
    public ParkResponse(Long id, Park park) {
        this.id = id;
        this.name = park.getMember().getName();
        this.parkNum = park.getParkingNum();
        this.address = park.getAddress();
        this.price = park.getPrice();
        this.regDate = park.getCreatedDate();
        this.parkStatus = park.getParkStatus();
    }

    @Builder(builderMethodName = "readPark")
    public ParkResponse(Long id, Park park, LocalDateTime confirmDate) {
        this.id = id;
        this.name = park.getMember().getName();
        this.parkNum = park.getParkingNum();
        this.address = park.getAddress();
        this.startTime = park.getStartTime();
        this.endTime = park.getEndTime();
        this.price = park.getPrice();
        this.confirmDate = confirmDate;
    }
}
