package com.project.rentoday.domain.park.dto;

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

    private Long memberId;
    private String carNum;
    private String parkNum;
    private String address;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private double price;
    private String content;

    @Size(max = 3)
    private List<ParkImageRequest> parkImages;

    @Builder
    public CreateParkRequest(Long memberId, String carNum, String parkNum, String address,
                             LocalDateTime startTime, LocalDateTime endTime,
                             double price, String content, List<ParkImageRequest> parkImages) {
        this.memberId = memberId;
        this.carNum = carNum;
        this.parkNum = parkNum;
        this.address = address;
        this.startTime = startTime;
        this.endTime = endTime;
        this.price = price;
        this.content = content;
        this.parkImages = parkImages;
    }

    public Park toEntity() {
        Park park = Park.builder()
                .carNum(carNum)
                .parkingNum(parkNum)
                .address(address)
                .startTime(startTime)
                .endTime(endTime)
                .price(price)
                .content(content)
                .build();

        if (parkImages != null) {
            for (ParkImageRequest parkImage : parkImages) {
                park.addParkImages(parkImage.toEntity());
            }
        }
        return park;
    }
}
