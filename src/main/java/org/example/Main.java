package org.example;

import org.example.data.gateways.YandexWeatherForecastGateway;
import org.example.domain.entities.ForecastReportEntity;
import org.example.domain.interactors.WeatherForecastInteractor;
import org.example.domain.interactors.exception.ReportProcessingException;

public class Main {
    public static void main(String[] args) {
        String access_key = System.getenv("ACCESS_KEY");
        String api_base_url = System.getenv("YANDEX_API_BASE_URL");

        if (access_key == null) {
            System.out.println("Не задан ACCESS_KEY в переменных окружения.");
            return;
        }

        WeatherForecastInteractor interactor = new WeatherForecastInteractor(
                new YandexWeatherForecastGateway(api_base_url, access_key)
        );

        ForecastReportEntity report;

        try {
            report = interactor.getForecastReport(55.75, 37.62);
        } catch (ReportProcessingException e) {
            System.out.println("Отчет по прогнозу погоды не может быть получен");
            System.out.println("Причина: " + e.getMessage());
            return;
        }


        System.out.println(report.getInitialJsonString());
    }
}