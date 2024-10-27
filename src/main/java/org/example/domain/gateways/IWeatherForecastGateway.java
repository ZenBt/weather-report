package org.example.domain.gateways;

import org.example.domain.entities.CoordsEntity;
import org.example.domain.entities.ForecastReportEntity;
import org.example.domain.gateways.exceptions.ApiKeyInvalidOrExpiredException;
import org.example.domain.gateways.exceptions.GatewayUnavailableException;
import org.example.domain.gateways.exceptions.UnknownGatewayException;

public interface IWeatherForecastGateway {

    ForecastReportEntity getForecastReportByCoords(CoordsEntity coords) throws ApiKeyInvalidOrExpiredException, GatewayUnavailableException, UnknownGatewayException;

}