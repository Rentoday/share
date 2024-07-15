package com.project.rentoday.domain.park.dto;

import com.project.rentoday.domain.park.entity.Park;
import com.project.rentoday.domain.park.entity.ParkImage;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ParkDetailRequest {
    private Long id;
    private String parkingNum;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private double price;
    private String agency;
    private String agNum;
    private String address;
    private String content;
    private List<ParkImageDto> parkImages;

    public ParkDetailRequest(Park park) {
        this.id = park.getId();
        this.parkingNum = park.getParkingNum();
        this.startTime = park.getStartTime();
        this.endTime = park.getEndTime();
        this.price = park.getPrice();
        this.agency = park.getAgency();
        this.agNum = park.getAgNum();
        this.address = park.getAddress();
        this.content = park.getContent();
        this.parkImages = park.getParkImages().stream()
                .map(ParkImageDto::new)
                .collect(Collectors.toList());
    }

    @Getter
    public static class ParkImageDto {
        private Long id;
        private String imageUrl;

        public ParkImageDto(ParkImage parkImage) {
            this.id = parkImage.getId();
            this.imageUrl = parkImage.getParkingImageUrl();
        }
    }
}
