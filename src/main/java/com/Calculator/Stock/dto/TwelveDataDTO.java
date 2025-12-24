package com.Calculator.Stock.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TwelveDataDTO {
    private Meta meta;
    private List<Values> values;
    private String status;

    @Data
    public static class Meta {
        private String symbol;
        private String interval;
        private String currency;
        private String exchange_timezone;
        private String exchange;
        private String mic_code;
        private String type;
    }

    @Data
    public static class Values {
        private String datetime;
        private String open;
        private String high;
        private String low;
        private String close;
        private String volume;
    }


}
