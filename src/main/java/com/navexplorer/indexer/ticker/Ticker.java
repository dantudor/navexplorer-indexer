package com.navexplorer.indexer.ticker;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Ticker {
    private String id;
    private String name;
    private String symbol;
    private Integer rank;
    private Double price_usd;
    private Double price_btc;
    private Double daily_volume_usd;
    private Double market_cap_usd;
    private Double available_supply;
    private Double total_supply;
    private Double max_supply;
    private Double percent_change_1h;
    private Double percent_change_24h;
    private Double percent_change_7d;
    private Integer last_updated;

    @JsonProperty("24h_volume_usd")
    public void setDaily_volume_usd(Double daily_volume_usd) {
        this.daily_volume_usd = daily_volume_usd;
    }
}
