package com.project.rentoday.domain.park.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "park_image")
public class ParkImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "img_id")
    private Long id;

    @ManyToOne(targetEntity = Park.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "pa_id")
    private Park park;

    @NotNull
    @Column
    private String parkingImageUrl;


    @Builder
    public ParkImage(Park park, String parkingImageUrl) {
        this.park = park;
        this.parkingImageUrl = parkingImageUrl;
    }

    public void updateImage(String parkingImageUrl, String parkingImageFileKey) {
        if (parkingImageUrl != null) this.parkingImageUrl = parkingImageUrl;

    }

    public void setPark(Park park) {
        this.park = park;
    }
}
