package ch.algotrader.ema.services;

import java.io.IOException;
import java.net.URI;

import javax.validation.constraints.NotNull;
import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.algotrader.ema.strategy.StrategyLogic;
import ch.algotrader.ema.vo.Subscription;
import ch.algotrader.ema.vo.TradeEvent;

@Service
@ClientEndpoint
public class MarketDataService implements DisposableBean, InitializingBean {

    private static final Logger LOGGER = LogManager.getLogger(MarketDataService.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final StrategyLogic strategyLogic;

    @Value("${ws-uri}")
    private String wsUrl;

    private Session session;

    @Autowired
    public MarketDataService(StrategyLogic strategyLogic) {
        this.strategyLogic = strategyLogic;
    }

    public void subscribeTrades(@NotNull String topic) {

        try {
            if (this.session == null || ! this.session.isOpen()) {
                this.session = initSession();
            }

            final String ser = new ObjectMapper().writeValueAsString(Subscription.trades(topic));
            LOGGER.info("sending " + ser);
            this.session.getBasicRemote().sendText(ser);
        } catch (IOException e) {
            LOGGER.error(e);
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        LOGGER.info("Connected: " + session.getNegotiatedSubprotocol());
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        LOGGER.info("Closed, reason: {}", closeReason);
    }

    @OnMessage
    public void onMessage(Session session, String msg) {

        try {
            final JsonNode json = objectMapper.readTree(msg);
            if (json.isArray()) {
                final JsonNode typeNode = json.get(1);
                if ("tu".equals(typeNode.asText())) {
                    final TradeEvent tradeEvent = TradeEvent.fromJson(json);
                    strategyLogic.handleTradeEvent(tradeEvent);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void destroy() throws Exception {
        LOGGER.info("Shutting down web socket session");
        this.session.close();
    }

    @Override
    public void afterPropertiesSet() {
        this.session = initSession();
    }

    private Session initSession() {
        Session ssn = null;
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        try {
            ssn = container.connectToServer(this, URI.create(wsUrl));

            LOGGER.info("session open: " + ssn.isOpen());

        } catch (DeploymentException | IOException e) {
            LOGGER.error(e);
        }
        return ssn;
    }

}
