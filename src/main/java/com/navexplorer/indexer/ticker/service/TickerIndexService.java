package com.navexplorer.indexer.ticker.service;

import com.navexplorer.indexer.ticker.entity.Ticker;
import com.navexplorer.library.ticker.repository.TickerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Calendar;
import java.util.List;

@Service
public class TickerIndexService {
    private static final Logger logger = LoggerFactory.getLogger(TickerIndexService.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    TickerRepository tickerRepository;

    private String url = "https://api.coinmarketcap.com/v1/ticker/nav-coin/";

//    @Scheduled(fixedRate = 60000L)
    public void getNewTicker() {
        ResponseEntity<List<Ticker>> rateResponse = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Ticker>>() {}
        );

        List<Ticker> tickers = rateResponse.getBody();

        logger.info("Get new ticker data");

        if (tickers.size() != 1) {
            return;
        }
        Ticker cmcTicker = tickers.get(0);

        com.navexplorer.library.ticker.entity.Ticker lastTicker = tickerRepository.findFirstByOrderByIdDesc();

        Calendar now = Calendar.getInstance();
        now.set(Calendar.SECOND, 0);
        now.set(Calendar.MILLISECOND, 0);

        if (lastTicker == null || !cmcTicker.getPrice_btc().equals(lastTicker.getPriceBtc()) || !cmcTicker.getPrice_usd().equals(lastTicker.getPriceUsd())) {
            com.navexplorer.library.ticker.entity.Ticker ticker = new com.navexplorer.library.ticker.entity.Ticker();
            ticker.setPriceBtc(cmcTicker.getPrice_btc());
            ticker.setPriceUsd(cmcTicker.getPrice_usd());
            ticker.setOpenDate(now.getTime());
            ticker.setCloseDate(now.getTime());

            tickerRepository.save(ticker);
        } else {
            lastTicker.setCloseDate(now.getTime());
            tickerRepository.save(lastTicker);
        }
    }
}
