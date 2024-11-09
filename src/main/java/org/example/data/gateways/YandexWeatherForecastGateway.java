package org.example.data.gateways;

import org.example.domain.entities.*;
import org.example.domain.gateways.IWeatherForecastGateway;
import org.example.domain.gateways.exceptions.ApiKeyInvalidOrExpiredException;
import org.example.domain.gateways.exceptions.GatewayUnavailableException;
import org.example.domain.gateways.exceptions.UnknownGatewayException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.util.ArrayList;
import java.util.Iterator;
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

    public ForecastReportEntity getForecastReportByCoords(CoordsEntity coords, int limit) {
        String result = null;
        HttpResponse<String> response;
        HttpRequest request;
        String url = apiBaseUrl + "/forecast?lat=" + coords.lat + "&lon=" + coords.lon + "&limit=" + limit;
        try {
            request = HttpRequest.newBuilder(new URI(url)).header("X-Yandex-Weather-Key", apiKey).GET().build();
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

        ForecastReportEntity report = getEntityFromJson(jsonObject);
        report.setInitialJsonString(result);
        return report;

    }

    private ForecastReportEntity getEntityFromJson(JSONObject jsonObject) {
        ForecastReportEntity report = new ForecastReportEntity();

        InfoEntity info = new InfoEntity();
        JSONObject tzinfo = jsonObject.getJSONObject("info").getJSONObject("tzinfo");
        info.lat = jsonObject.getJSONObject("info").getFloat("lat");
        info.lon = jsonObject.getJSONObject("info").getFloat("lon");
        info.tzinfo = new InfoEntity.TzInfo(
                tzinfo.getInt("offset"),
                tzinfo.getString("name"),
                tzinfo.getString("abbr"),
                tzinfo.getBoolean("dst")
        );
        info.defPressureMm = jsonObject.getJSONObject("info").getInt("def_pressure_mm");
        info.defPressurePa = jsonObject.getJSONObject("info").getInt("def_pressure_pa");
        info.url = jsonObject.getJSONObject("info").getString("url");

        FactEntity fact = new FactEntity();
        fact.temp = jsonObject.getJSONObject("fact").getFloat("temp");
        fact.feelsLike = jsonObject.getJSONObject("fact").getFloat("feels_like");
        fact.tempWater = jsonObject.getJSONObject("fact").getFloat("temp_water");
        fact.icon = jsonObject.getJSONObject("fact").getString("icon");
        fact.condition = jsonObject.getJSONObject("fact").getString("condition");
        fact.windSpeed = jsonObject.getJSONObject("fact").getFloat("wind_speed");
        fact.windGust = jsonObject.getJSONObject("fact").getFloat("wind_gust");
        fact.windDir = jsonObject.getJSONObject("fact").getString("wind_dir");
        fact.pressureMm = jsonObject.getJSONObject("fact").getFloat("pressure_mm");
        fact.pressurePa = jsonObject.getJSONObject("fact").getFloat("pressure_pa");
        fact.humidity = jsonObject.getJSONObject("fact").getFloat("humidity");
        fact.daytime = jsonObject.getJSONObject("fact").getString("daytime");
        fact.polar = jsonObject.getJSONObject("fact").getBoolean("polar");
        fact.season = jsonObject.getJSONObject("fact").getString("season");
        fact.obsTime = jsonObject.getJSONObject("fact").getInt("obs_time");
        fact.isThunder = jsonObject.getJSONObject("fact").getBoolean("is_thunder");
        fact.cloudness = jsonObject.getJSONObject("fact").getInt("cloudness");


        report.now = jsonObject.getInt("now");
        report.nowDt = jsonObject.getString("now_dt");
        report.info = info;
        report.fact = fact;
        report.forecasts = new ArrayList<ForecastEntity>();

        JSONArray forecasts = jsonObject.getJSONArray("forecasts");

        for (int i = 0, size = forecasts.length(); i < size; i++) {
            JSONObject jsonForecast = forecasts.getJSONObject(i);

            ForecastEntity forecast = new ForecastEntity();
            forecast.date = jsonForecast.getString("date");
            forecast.dateTs = jsonForecast.getInt("date_ts");
            forecast.week = jsonForecast.getInt("week");
            forecast.riseBegin = jsonForecast.getString("rise_begin");
            forecast.sunrise = jsonForecast.getString("sunrise");
            forecast.sunset = jsonForecast.getString("sunset");
            forecast.setEnd = jsonForecast.getString("set_end");
            forecast.moonCode = jsonForecast.getInt("moon_code");
            forecast.moonText = jsonForecast.getString("moon_text");

            ArrayList<ForecastEntity.PartsEntity> parts = new ArrayList<ForecastEntity.PartsEntity>();


            JSONObject part = jsonForecast.getJSONObject("parts").getJSONObject("morning");
            parts.addLast(new ForecastEntity.PartsEntity(part.getFloat("temp_avg"), part.getFloat("temp_max"), part.getFloat("temp_min")));
            part = jsonForecast.getJSONObject("parts").getJSONObject("day");
            parts.addLast(new ForecastEntity.PartsEntity(part.getFloat("temp_avg"), part.getFloat("temp_max"), part.getFloat("temp_min")));
            part = jsonForecast.getJSONObject("parts").getJSONObject("evening");
            parts.addLast(new ForecastEntity.PartsEntity(part.getFloat("temp_avg"), part.getFloat("temp_max"), part.getFloat("temp_min")));


            forecast.parts = parts;
            report.forecasts.addLast(forecast);
        }

        return report;
    }
}

