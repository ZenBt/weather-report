package org.example.data.gateways;

import org.example.domain.entities.CoordsEntity;
import org.example.domain.entities.ForecastReportEntity;
import org.example.domain.gateways.IWeatherForecastGateway;
import org.example.domain.gateways.exceptions.ApiKeyInvalidOrExpiredException;
import org.example.domain.gateways.exceptions.GatewayUnavailableException;
import org.example.domain.gateways.exceptions.UnknownGatewayException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.util.Objects;
import java.util.ResourceBundle;

public class YandexWeatherForecastGateway implements IWeatherForecastGateway {
    private final String apiBaseUrl;
    private final String apiKey;

    private final HttpClient client = HttpClient.newHttpClient();

    public YandexWeatherForecastGateway(String apiBaseUrl, String apiKey) {
        this.apiBaseUrl = apiBaseUrl;
        this.apiKey = apiKey;
    }

    public ForecastReportEntity getForecastReportByCoords(CoordsEntity coords) {
        String result = null;
        HttpResponse<String> response;
        HttpRequest request;
        String url = apiBaseUrl + "/forecast?lat=" + coords.lat + "&lon=" + coords.lon;
        try {
            request = HttpRequest.newBuilder(new URI(url))
                    .header("X-Yandex-Weather-Key", apiKey)
                    .GET()
                    .build();
        } catch (URISyntaxException e) {
            throw new UnknownGatewayException("Неверный формат URL");
        }

        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException | IOException e) {
            throw new UnknownGatewayException(e.getMessage());
        }

        int statusCode = response.statusCode();
        result = switch (statusCode) {
            case 200 -> response.body();
            case 401, 403 -> throw new ApiKeyInvalidOrExpiredException(String.valueOf(statusCode));
            case 500, 502, 503 -> throw new GatewayUnavailableException(String.valueOf(statusCode));
            default -> throw new UnknownGatewayException(String.valueOf(statusCode));
        };
        JSONObject jsonObject = new JSONObject(result);

        ForecastReportEntity report = new ForecastReportEntity();
        report.setInitialJsonString(result);
        report.now = jsonObject.getInt("now");
        report.now_dt = jsonObject.getString("now_dt");
        return report;
    }
}
