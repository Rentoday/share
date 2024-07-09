package com.project.rentoday.domain.park.dto;

import com.project.rentoday.domain.park.entity.ParkImage;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class UpdateParkRequest {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private double price;
    private String content;
    private List<ParkImage> images;
    private String confirmation;
}
