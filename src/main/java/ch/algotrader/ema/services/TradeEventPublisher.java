package ch.algotrader.ema.services;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.algotrader.ema.vo.TradeEvent;

@Component
public class TradeEventPublisher {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private ApplicationEventPublisher publisher;

    void publish(String msg) {
        try {
            final JsonNode json = objectMapper.readTree(msg);
            if (json.isArray()) {
                final JsonNode typeNode = json.get(1);
                if ("tu".equals(typeNode.asText())) {
                    final TradeEvent fromJson = TradeEvent.fromJson(json);
                    publisher.publishEvent(fromJson);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
