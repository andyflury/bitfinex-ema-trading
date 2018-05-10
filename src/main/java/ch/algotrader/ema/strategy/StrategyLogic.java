package ch.algotrader.ema.strategy;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.ZonedDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.ta4j.core.Bar;
import org.ta4j.core.BaseBar;
import org.ta4j.core.BaseTimeSeries;
import org.ta4j.core.TimeSeries;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.DifferenceIndicator;
import org.ta4j.core.num.Num;

import ch.algotrader.ema.services.TradingService;
import ch.algotrader.ema.vo.TradeEvent;

@Component
public class StrategyLogic implements InitializingBean {
    
    private static final Logger logger = LoggerFactory.getLogger(StrategyLogic.class);

    @Value("${symbol}") private String symbol;
    @Value("${quantity}") private BigDecimal quantity;

    @Value("${emaPeriodShort}") private int emaPeriodShort;
    @Value("${emaPeriodLong}") private int emaPeriodLong;

    private final TradingService tradingService;
    private final TimeSeries series;

    private DifferenceIndicator emaDifference;

    @Autowired
    public StrategyLogic(TradingService tradingService) {
        this.tradingService = tradingService;
        this.series = new BaseTimeSeries();
    }

    @Override
    public void afterPropertiesSet() {
        ClosePriceIndicator closePriceIndicator = new ClosePriceIndicator(this.series);
        EMAIndicator emaShort = new EMAIndicator(closePriceIndicator, this.emaPeriodShort);
        EMAIndicator emaLong = new EMAIndicator(closePriceIndicator, this.emaPeriodLong);
        this.emaDifference = new DifferenceIndicator(emaShort, emaLong);
    }

    public void handleTradeEvent(TradeEvent event) {

        if (this.series.getEndIndex() >= 0) {
            synchronized (series) {
                series.addTrade(Math.abs(event.getAmount()), event.getPrice());
            }
        }
    }

    @Scheduled(cron = "*/10 * * * * *")
    public void onTime() {
        synchronized (series) {
            try {
                logBar();
                evaluateLogic();
                createNewBar();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void logBar() {
        
        int i = this.series.getEndIndex();
        if (i > 0 && i < emaPeriodLong) {
            Bar bar = this.series.getBar(i);
            logger.info("open {} high {} low {} close {} vol {} trades {}",
                    bar.getOpenPrice(),
                    bar.getMaxPrice(),
                    bar.getMinPrice(),
                    bar.getClosePrice(),
                    bar.getVolume(),
                    bar.getTrades());
            
        } else if (i >= emaPeriodLong) {

            Bar bar = this.series.getBar(i);
            Num emaDiff = this.emaDifference.getValue(i);
            Num emaDiffPrev = this.emaDifference.getValue(i - 1);

            logger.info("open {} high {} low {} close {} vol {} trades {} emaDiffPrev {} emaDiff {}",
                    bar.getOpenPrice(),
                    bar.getMaxPrice(),
                    bar.getMinPrice(),
                    bar.getClosePrice(),
                    bar.getVolume(),
                    bar.getTrades(),
                    emaDiffPrev,
                    emaDiff);
        }
    }

    private void evaluateLogic() {

        int i = this.series.getEndIndex();
        if (i >= emaPeriodLong) {

            Num emaDiff = this.emaDifference.getValue(i);
            Num emaDiffPrev = this.emaDifference.getValue(i - 1);

            if (emaDiff.doubleValue() > 0 && emaDiffPrev.doubleValue() <= 0) {

                logger.info("!!!!!!!! BUY !!!!!!!!!)");
                tradingService.sendOrder("buy", quantity, symbol);

            } else if (emaDiff.doubleValue() < 0 && emaDiffPrev.doubleValue() >= 0) {

                logger.info("!!!!!!!! SELL !!!!!!!!!");
                tradingService.sendOrder("sell", quantity, symbol);
            }
        }
    }

    private void createNewBar() {
    
        // create new bar
        ZonedDateTime now = ZonedDateTime.now();
        Duration duration = Duration.ofSeconds(10);
        Bar newBar = new BaseBar(duration, now, this.series.function());
    
        // set price to closing price of previous bar
        int i = this.series.getEndIndex();
        if (i >= 0) {
            Bar previousBar = series.getBar(i);
            newBar.addPrice(previousBar.getClosePrice());
        }
    
        series.addBar(newBar);
    }

}
