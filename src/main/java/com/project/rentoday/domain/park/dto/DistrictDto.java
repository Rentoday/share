package com.project.rentoday.domain.park.dto;

import com.project.rentoday.domain.district.entity.District;
import com.project.rentoday.domain.park.entity.Park;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DistrictDto {
    private Long id;  // 추가된 필드
    private double price;
    private String parkLatitude;
    private String parkLongitude;

    public DistrictDto(Park park, District district) {
        this.id = park.getId();  // Park 엔티티에 getId() 메서드가 있다고 가정
        this.price = park.getPrice();
        this.parkLatitude = park.getLatitude();
        this.parkLongitude = park.getLongitude();
    }
}
