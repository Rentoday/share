package com.project.rentoday.domain.park.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
@Data
@AllArgsConstructor
public class DistrictResponse {
    private double districtLatitude;
    private double districtLongitude;
    private List<DistrictDto> parks;
}
