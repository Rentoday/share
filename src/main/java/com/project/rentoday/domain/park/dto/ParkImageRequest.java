package com.project.rentoday.domain.park.dto;

import com.project.rentoday.domain.park.entity.ParkImage;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ParkImageRequest {
    private String imageUrl;

    @Builder
    public ParkImageRequest(String imageUrl, String imageFileKey) {
        this.imageUrl = imageUrl;
    }

    public ParkImage toEntity() {
        return ParkImage.builder()
                .parkingImageUrl(imageUrl)
                .build();
    }
}
