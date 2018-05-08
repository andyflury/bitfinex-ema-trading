package ch.algotrader.ema;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import ch.algotrader.ema.services.MarketDataService;

@SpringBootApplication
@EnableScheduling
public class Application implements CommandLineRunner {

    private final MarketDataService marketDataService;

    @Autowired
    public Application(MarketDataService marketDataService) {
        this.marketDataService = marketDataService;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) {
        marketDataService.subscribeTrades("BTCUSD");
    }
}
