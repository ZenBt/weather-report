package org.example.domain.entities;

import java.util.ArrayList;

public class ForecastReportEntity {
    public int now;
    public String nowDt;
    public InfoEntity info;
    public FactEntity fact;
    public ArrayList<ForecastEntity> forecasts;

    private String initialJsonString;

    public void setInitialJsonString(String jsonString) {
        initialJsonString = jsonString;
    }

    public String getInitialJsonString() {
        return initialJsonString;
    }

    public float getForecastsAvgTemp() {
        float sumOfAvg = 0;
        for (ForecastEntity forecast : forecasts) {
            float avg = forecast.getAvgTemp();
            sumOfAvg += avg;
        }
        return sumOfAvg / forecasts.size();
    }
}
