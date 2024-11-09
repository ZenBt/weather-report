package org.example;

import org.example.data.gateways.YandexWeatherForecastGateway;
import org.example.domain.entities.ForecastReportEntity;
import org.example.domain.interactors.WeatherForecastInteractor;
import org.example.domain.interactors.exception.ReportProcessingException;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        String access_key = System.getenv("ACCESS_KEY");
        String api_base_url = System.getenv("YANDEX_API_BASE_URL");

        if (access_key == null) {
            System.out.println("Не задан ACCESS_KEY в переменных окружения.");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите координаты точки lat: ");
        float lat, lon;
        int limit;

        try {
            lat = scanner.nextFloat();
        } catch (InputMismatchException e) {
            System.out.println("Ошибка ввода числа, недопустимый формат");
            return;
        }

        System.out.println("Введите координаты точки lon: ");
        try {
            lon = scanner.nextFloat();
        } catch (InputMismatchException e) {
            System.out.println("Ошибка ввода числа, недопустимый формат");
            return;
        }
        System.out.println("Введите количество дней в пределах [1, 11]: ");
        try {
            limit = scanner.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("Ошибка ввода числа, недопустимый формат");
            return;
        }

        if (limit < 1 || limit > 11) {
            System.out.println("Недопустимое значение");
            return;
        }


        WeatherForecastInteractor interactor = new WeatherForecastInteractor(
                new YandexWeatherForecastGateway(api_base_url, access_key)
        );

        ForecastReportEntity report;

        try {
            report = interactor.getForecastReport(lat, lon, limit);
        } catch (ReportProcessingException e) {
            System.out.println("Отчет по прогнозу погоды не может быть получен");
            System.out.println("Причина: " + e.getMessage());
            return;
        }


        System.out.println(report.getInitialJsonString());
        System.out.println("Текущая температура: " + String.valueOf(report.fact.temp));
        System.out.println("Средняя температура: " + report.getForecastsAvgTemp());
    }
}