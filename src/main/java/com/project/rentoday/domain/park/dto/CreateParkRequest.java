package com.project.rentoday.domain.park.dto;

import com.project.rentoday.domain.member.entity.Member;
import com.project.rentoday.domain.park.entity.Park;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class CreateParkRequest {

    private Member member;
    private String carNum;
    private String parkNum;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private double price;
    private String content;
    private String address;
    private String latitude;
    private String longitude;
    private String confirmation;

    @Size(max = 3)
    private List<ParkImageRequest> parkImages;

    @Builder
    public CreateParkRequest(Member member, String carNum, String parkNum, String address,
                             LocalDateTime startTime, LocalDateTime endTime, String latitude, String longitude,
                             double price, String content, String confirmation, List<ParkImageRequest> parkImages) {
        this.member = member;
        this.carNum = carNum;
        this.parkNum = parkNum;
        this.address = address;
        this.startTime = startTime;
        this.endTime = endTime;
        this.latitude = latitude;
        this.longitude = longitude;
        this.price = price;
        this.content = content;
        this.confirmation = confirmation;
        this.parkImages = parkImages;
    }

    public Park toEntity() {
        Park park = Park.builder()
                .member(member)
                .carNum(carNum)
                .parkingNum(parkNum)
                .address(address)
                .latitude(latitude)
                .longitude(longitude)
                .startTime(startTime)
                .endTime(endTime)
                .price(price)
                .content(content)
                .confirmation(confirmation)
                .build();

        if (parkImages != null) {
            for (ParkImageRequest parkImage : parkImages) {
                park.addParkImages(parkImage.toEntity());
            }
        }
        return park;
    }
}
