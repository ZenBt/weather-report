package org.example.domain.entities;

public class ForecastReportEntity {
    public int now;
    public String now_dt;
    public InfoEntity info;
    public FactEntity fact;
    public ForecastEntity forecast;

    private String initialJsonString;

    public void setInitialJsonString(String jsonString) {
        initialJsonString = jsonString;
    }

    public String getInitialJsonString() {
        return initialJsonString;
    }
}
