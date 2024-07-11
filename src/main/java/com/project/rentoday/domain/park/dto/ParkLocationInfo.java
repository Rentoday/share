package com.project.rentoday.domain.park.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
@AllArgsConstructor
public class ParkLocationInfo {

    private String address;
    private String latitude;
    private String longitude;
    private String agency;
    private String agNum;

}
