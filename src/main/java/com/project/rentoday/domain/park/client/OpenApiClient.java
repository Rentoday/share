package com.project.rentoday.domain.park.client;

import com.project.rentoday.domain.park.dto.ValidationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class OpenApiClient {

    private final RestTemplate restTemplate;

    public OpenApiClient() {
        this.restTemplate = new RestTemplate();
    }

    private final String endPoint = "http://api.data.go.kr/openapi/tn_pubr_public_residnt_prior_parkng_api";
    private final String encodingKey = "1VzA6081jcO2iM6qu859rtrrZe1Owr9IXbAi0XAvg344mzs8uMDgzW8qKvmlAk8PzqtJirYGw%2FEbkYRD3YB9GA%3D%3D";
    private final String decodingKey = "1VzA6081jcO2iM6qu859rtrrZe1Owr9IXbAi0XAvg344mzs8uMDgzW8qKvmlAk8PzqtJirYGw/EbkYRD3YB9GA==";



    public boolean validateParkNum(String parkNum) {
        String url = String.format("%s?encodingKey=%s&guhakNo=%s", endPoint, encodingKey, parkNum);
        ValidationResponse response = restTemplate.getForObject(url, ValidationResponse.class);
        return response != null && response.isValid();
    }

}
