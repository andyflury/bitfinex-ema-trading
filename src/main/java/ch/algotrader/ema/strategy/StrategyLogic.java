package ch.algotrader.ema.strategy;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.ZonedDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
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

    @Value("${emaPeriodShort}") private int emaPeriodShort;
    @Value("${emaPeriodLong}") private int emaPeriodLong;

    @Autowired
    private TradingService tradingService;

    private TimeSeries series;
    private DifferenceIndicator emaDifference;

    @Override
    public void afterPropertiesSet() throws Exception {
        this.series = new BaseTimeSeries();
        ClosePriceIndicator closePriceIndicator = new ClosePriceIndicator(this.series);
        EMAIndicator emaShort = new EMAIndicator(closePriceIndicator, this.emaPeriodShort);
        EMAIndicator emaLong = new EMAIndicator(closePriceIndicator, this.emaPeriodLong);
        this.emaDifference = new DifferenceIndicator(emaShort, emaLong);
    }

    @EventListener
    public void handleTradeEvent(TradeEvent event) {

        if (this.series.getEndIndex() >= 0) {
            synchronized (series) {
                series.addTrade(Math.abs(event.getAmount()), event.getPrice());
            }
        }
    }

    @Scheduled(cron = "*/10 * * * * *")
    public void ontime() {
        synchronized (series) {
            createNewBar();
            evaluateLogic();
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

            // print previousBar
            logger.info("open {} high {} low {} close {} vol {} trades {} emaDiff {}",
                    previousBar.getOpenPrice(),
                    previousBar.getMaxPrice(),
                    previousBar.getMinPrice(),
                    previousBar.getClosePrice(),
                    previousBar.getVolume(),
                    previousBar.getTrades(),
                    i > 1 ? (this.emaDifference.getValue(i - 1) + " -> " + this.emaDifference.getValue(i)) : 0);

        }

        series.addBar(newBar);
    }

    private void evaluateLogic() {

        int i = this.series.getEndIndex();
        if (i >= emaPeriodShort) {
            Num currentValue = this.emaDifference.getValue(i);
            Num previousValue = this.emaDifference.getValue(i - 1);

            if (currentValue.doubleValue() > 0 && previousValue.doubleValue() <= 0) {
                logger.info("!!!!!!!! BUY !!!!!!!!! ({} -> {})", previousValue, currentValue);
                tradingService.sendOrder("buy", BigDecimal.valueOf(0.002), "BTCUSD");
            } else if (currentValue.doubleValue() < 0 && previousValue.doubleValue() >= 0) {
                logger.info("!!!!!!!! SELL !!!!!!!!! ({} -> {})", previousValue, currentValue);
                tradingService.sendOrder("sell", BigDecimal.valueOf(0.002), "BTCUSD");
            }
        }
    }

}
