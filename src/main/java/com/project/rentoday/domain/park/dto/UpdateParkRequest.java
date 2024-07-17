package com.project.rentoday.domain.park.dto;

import com.project.rentoday.domain.park.entity.ParkImage;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class UpdateParkRequest {
    private LocalTime startTime;
    private LocalTime endTime;
    private double price;
    private String content;
    private List<ParkImage> images;
    private String confirmation;
}
