package com.WeatherAPI;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class WeatherData {
    private static final HttpClient client = HttpClient.newHttpClient();
    public static WeatherEntity getWeather(String city) {
        String url = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/";
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url + city + "?key=" + System.getenv("WEATHER_SECRET")))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new RuntimeException(response.body());
            }
            return Json.fromJson(Json.parse(response.body()), WeatherEntity.class);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
