package com.project.rentoday.domain.park.dto;

import com.project.rentoday.domain.member.entity.Member;
import com.project.rentoday.domain.park.entity.Park;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CreateParkRequest {

    private String member;
    private String carNum;
    private String parkNum;
    private LocalTime startTime;
    private LocalTime endTime;
    private double price;
    private String content;
    private String address;
    private String latitude;
    private String longitude;
    private String confirmation;
    private MultipartFile[] photo;
    private MultipartFile pdf;

    @Size(max = 3)
    private List<ParkImageRequest> parkImages;

    @Builder
    public CreateParkRequest(String member, String carNum, String parkNum, String address,
                             LocalTime startTime, LocalTime endTime, String latitude, String longitude,
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

}
