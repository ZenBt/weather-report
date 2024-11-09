package org.example.domain.entities;

import java.util.ArrayList;

public class ForecastEntity {
    public static class PartsEntity {
        public float tempAvg;
        public float tempMax;
        public float tempMin;

        public PartsEntity(float tempAvg,
                           float tempMax,
                           float tempMin) {
            this.tempAvg = tempAvg;
            this.tempMax = tempMax;
            this.tempMin = tempMin;
        }
    }

    public String date;
    public int dateTs;
    public int week;
    public String riseBegin;
    public String sunrise;
    public String sunset;
    public String setEnd;
    public int moonCode;
    public String moonText;
    public ArrayList<PartsEntity> parts;

    public float getAvgTemp() {
        float sum = 0;
        for (PartsEntity part : parts) {
            sum += part.tempAvg;
        }
        return sum / parts.size();
    }
}
