package com.project.rentoday.domain.park.dto;

import lombok.Data;

@Data
public class ParkLocationInfo {

    private String address;
    private String latitude;
    private String longitude;
    private String agency;
    private String agNum;

    public ParkLocationInfo(String address, String latitude, String longitude, String agency, String agNum) {
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.agency = agency;
        this.agNum = agNum;
    }

}
