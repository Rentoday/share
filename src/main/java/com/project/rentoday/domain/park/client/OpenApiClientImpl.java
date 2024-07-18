package com.project.rentoday.domain.park.client;

import com.project.rentoday.domain.park.dto.ParkLocationInfo;
import com.project.rentoday.domain.park.dto.ValidationResponse;
import com.project.rentoday.domain.park.exception.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class OpenApiClientImpl implements OpenApiClient {

        private final RestTemplate restTemplate;

        private final String endPoint = "http://api.data.go.kr/openapi/tn_pubr_public_residnt_prior_parkng_api";
        private final String encodingKey = "1VzA6081jcO2iM6qu859rtrrZe1Owr9IXbAi0XAvg344mzs8uMDgzW8qKvmlAk8PzqtJirYGw%2FEbkYRD3YB9GA%3D%3D";
        private final String decodingKey = "1VzA6081jcO2iM6qu859rtrrZe1Owr9IXbAi0XAvg344mzs8uMDgzW8qKvmlAk8PzqtJirYGw/EbkYRD3YB9GA==";



    public boolean validateParkNum(String parkNum) {
        String encodedKey = "1VzA6081jcO2iM6qu859rtrrZe1Owr9IXbAi0XAvg344mzs8uMDgzW8qKvmlAk8PzqtJirYGw%2FEbkYRD3YB9GA%3D%3D";
        String decodedKey = "1VzA6081jcO2iM6qu859rtrrZe1Owr9IXbAi0XAvg344mzs8uMDgzW8qKvmlAk8PzqtJirYGw/EbkYRD3YB9GA==";

        String url = UriComponentsBuilder.fromHttpUrl(endPoint)
                .queryParam("ServiceKey", encodedKey) // 'ServiceKey'로 변경하고 인코딩된 키 사용
                .queryParam("parkplaceNo", parkNum)
                .queryParam("type", "json")
                .build(false) // false를 전달하여 자동 인코딩 방지
                .toUriString();

        log.debug("API Request URL: {}", url);

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                String body = response.getBody();
                log.debug("Full API Response: {}", body);

                if (body != null) {
                    JSONObject jsonObject = new JSONObject(body);
                    JSONObject responseObj = jsonObject.getJSONObject("response");
                    JSONObject headerObj = responseObj.getJSONObject("header");

                    String resultCode = headerObj.getString("resultCode");
                    String resultMsg = headerObj.getString("resultMsg");

                    if ("00".equals(resultCode)) {
                        JSONObject bodyObj = responseObj.getJSONObject("body");
                        int totalCount = bodyObj.getInt("totalCount");
                        return totalCount > 0;
                    } else {
                        log.warn("API returned error code: {}, message: {}", resultCode, resultMsg);
                        return false;
                    }
                }
            } else {
                log.warn("API request failed with status: {}", response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("Error occurred while calling API", e);
        }

        return false;
    }

    @Override
    public ParkLocationInfo getParkLocationInfo(String parkNum) {
        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(endPoint)
                    .queryParam("encodingKey", encodingKey)
                    .queryParam("pageNo", "1")
                    .queryParam("numOfRows", "10")
                    .queryParam("type", "json")
                    .queryParam("parkplaceNo", parkNum);

            ResponseEntity<String> response = restTemplate.getForEntity(builder.toUriString(), String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                String body = response.getBody();
                if (body != null) {
                    JSONObject jsonObject = new JSONObject(body);
                    JSONObject responseObj = jsonObject.getJSONObject("response");
                    JSONObject bodyObj = responseObj.getJSONObject("body");
                    JSONArray itemsArray = bodyObj.getJSONArray("items");

                    if (itemsArray.length() > 0) {
                        JSONObject item = itemsArray.getJSONObject(0);
                        return new ParkLocationInfo(
                                item.getString("rdnmadr"),
                                item.getString("latitude"),
                                item.getString("longitude"),
                                item.getString("institutionNm"),
                                item.getString("phoneNumber")
                        );
                    }
                }
            }
            throw new ApiException("주차 정보를 불러오는데 실패했습니다.");
        } catch (RestClientException | JSONException e) {
            throw new ApiException("API를 불러오는데 실패했습니다.");
        }
    }

}
