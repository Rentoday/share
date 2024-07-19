package com.project.rentoday.domain.park.client;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.project.rentoday.domain.park.dto.OpenApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.*;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OpenApiClientService {

    public OpenApiResponse getTotalPages(String parkNum, String parkAdd) throws IOException {

        StringBuilder urlBuilder = new StringBuilder("http://api.data.go.kr/openapi/tn_pubr_public_residnt_prior_parkng_api");
        urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=1VzA6081jcO2iM6qu859rtrrZe1Owr9IXbAi0XAvg344mzs8uMDgzW8qKvmlAk8PzqtJirYGw%2FEbkYRD3YB9GA%3D%3D");
        urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지 번호*/
        urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("100", "UTF-8")); /*한 페이지 결과 수*/
        urlBuilder.append("&" + URLEncoder.encode("type", "UTF-8") + "=" + URLEncoder.encode("json", "UTF-8")); /*XML/JSON 여부*/
        urlBuilder.append("&" + URLEncoder.encode("prkcmprtNo", "UTF-8") + "=" + URLEncoder.encode(parkNum, "UTF-8")); /*XML/JSON 여부*/
        URL url = new URL(urlBuilder.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");

        System.out.println("Response code: " + conn.getResponseCode());

        BufferedReader rd;
        if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }

        StringBuilder sb = new StringBuilder();

        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();
        conn.disconnect();

        System.out.println(sb.toString());

        String jsonString = sb.toString();
        System.out.println("Received JSON: " + jsonString);
        Gson gson = new Gson();

        // JSON 객체에서 실제 데이터 배열을 추출 (API 응답 구조에 따라 다를 수 있음)
        JsonObject responseObject = gson.fromJson(jsonString, JsonObject.class);
        JsonObject bodyObject = responseObject.getAsJsonObject("response").getAsJsonObject("body");
        JsonArray itemsArray = bodyObject.getAsJsonArray("items");

        // JsonArray를 List<ParkingInfo>로 변환
        Type listType = new TypeToken<List<OpenApiResponse>>(){}.getType();
        List<OpenApiResponse> parkingInfoList = gson.fromJson(itemsArray, listType);


        // 이제 parkingInfoList를 사용하여 데이터를 처리할 수 있습니다
        for (OpenApiResponse info : parkingInfoList) {
            String address = info.getRdnmadr().replaceAll("\\s+", "");
            if (info.getPrkcmprtNo().equals(parkNum) && address.equals(parkAdd)) {
                System.out.println(info.getRdnmadr());
                return info;
            }
        }

        return null;
    }

}
