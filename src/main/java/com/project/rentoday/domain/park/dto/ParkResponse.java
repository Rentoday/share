package com.project.rentoday.domain.park.dto;

import com.project.rentoday.domain.park.entity.Park;
import com.project.rentoday.domain.park.entity.ParkStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@NoArgsConstructor
public class ParkResponse {
    private Long id;
    private String name;
    private String parkNum;
    private String address;
    private LocalTime startTime;
    private LocalTime endTime;
    private double price;
    private LocalDateTime regDate;
    private LocalDateTime confirmDate;
    private ParkStatus parkStatus;
    private String longitude;
    private String latitude;



    public ParkResponse(Park park) {
        this.id = park.getId();
        this.name = park.getMember().getName();
        this.parkNum = park.getParkingNum();
        this.address = park.getAddress();
        this.startTime = park.getStartTime();
        this.endTime = park.getEndTime();
        this.price = park.getPrice();
        this.regDate = park.getCreatedDate();
        this.parkStatus = park.getParkStatus();
        this.longitude = park.getLongitude();
        this.latitude = park.getLatitude();
    }

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
        this.confirmDate = park.getLastModifiedDate();
    }
}
