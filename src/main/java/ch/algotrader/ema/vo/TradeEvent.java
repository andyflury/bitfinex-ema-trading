/***********************************************************************************
 * AlgoTrader Enterprise Trading Framework
 *
 * Copyright (C) 2017 AlgoTrader GmbH - All rights reserved
 *
 * All information contained herein is, and remains the property of AlgoTrader GmbH.
 * The intellectual and technical concepts contained herein are proprietary to
 * AlgoTrader GmbH. Modification, translation, reverse engineering, decompilation,
 * disassembly or reproduction of this material is strictly forbidden unless prior
 * written permission is obtained from AlgoTrader GmbH
 *
 * Fur detailed terms and conditions consult the file LICENSE.txt or contact
 *
 * AlgoTrader GmbH
 * Aeschstrasse 6
 * 8834 Schindellegi
 ***********************************************************************************/
package ch.algotrader.ema.vo;

import com.fasterxml.jackson.databind.JsonNode;

public class TradeEvent {

    private String seq;
    private Long timestamp;
    private Double price;
    private Double amount;

    private TradeEvent(){}

    /*
     [
     "<CHANNEL_ID>",
     "tu",
     "<SEQ>",
     "<ID>",
     "<TIMESTAMP>",
     "<PRICE>",
     "<AMOUNT>"
     ]
     */
    public static TradeEvent fromJson(JsonNode json) {

        TradeEvent tradeEvent = new TradeEvent();
        tradeEvent.seq = json.get(2).asText();
        tradeEvent.timestamp = json.get(4).asLong();
        tradeEvent.price = json.get(5).asDouble();
        tradeEvent.amount = json.get(6).asDouble();
        return tradeEvent;
    }

    public String getSeq() {
        return seq;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public Double getPrice() {
        return price;
    }

    public Double getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return "TradeEvent [seq=" + seq + ", timestamp=" + timestamp + ", price=" + price + ", amount=" + amount + "]";
    }
}
