package org.example.domain.entities;

public class InfoEntity {

    public static class TzInfo {
        public int offset;
        public String name;
        public String abbr;
        public boolean dst;

        public TzInfo(int offset,
                      String name,
                      String abbr,
                      boolean dst) {
            this.offset = offset;
            this.name = name;
            this.abbr = abbr;
            this.dst = dst;
        }
    }

    public float lat;
    public float lon;

    public TzInfo tzinfo;

    public int defPressureMm;
    public int defPressurePa;
    public String url;

}
