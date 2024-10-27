package org.example.domain.interactors;

import org.example.domain.entities.CoordsEntity;
import org.example.domain.entities.ForecastReportEntity;
import org.example.domain.gateways.IWeatherForecastGateway;
import org.example.domain.gateways.exceptions.ApiKeyInvalidOrExpiredException;
import org.example.domain.gateways.exceptions.GatewayUnavailableException;
import org.example.domain.gateways.exceptions.UnknownGatewayException;
import org.example.domain.interactors.exception.ReportProcessingException;

public class WeatherForecastInteractor {
    private final IWeatherForecastGateway forecastGateway;

    public WeatherForecastInteractor(IWeatherForecastGateway forecastGateway) {
        this.forecastGateway = forecastGateway;
    }

    public ForecastReportEntity getForecastReport(double latitude, double longitude) throws ReportProcessingException {
        CoordsEntity coords = new CoordsEntity();
        ForecastReportEntity report;
        try {
            report = forecastGateway.getForecastReportByCoords(coords);
        } catch (GatewayUnavailableException e) {
            // TODO: залоггировать e
            throw new ReportProcessingException("Сервис погоды недоступен");
        } catch (ApiKeyInvalidOrExpiredException e) {
            // TODO: залоггировать e
            throw new ReportProcessingException("API ключ истек или неверен");
        } catch (UnknownGatewayException e) {
            // TODO: залоггировать e
            throw new ReportProcessingException("Неизвестная ошибка, статус код: " + e.getMessage());
        } catch (Exception e) {
            throw new ReportProcessingException(e.getMessage());
        }
        return report;
    }
}
