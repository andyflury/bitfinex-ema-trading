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

import com.fasterxml.jackson.annotation.JsonProperty;

public class OrderResponse {

    private long id;
    private String symbol;
    private String exchange;
    @JsonProperty("avg_execution_price")
    private double avgExecutionPrice;
    private String side;
    private String type;
    private String timestamp;
    @JsonProperty("is_live")
    private boolean isLive;
    @JsonProperty("is_cancelled")
    private boolean isCancelled;
    @JsonProperty("is_hidden")
    private boolean isHidden;
    @JsonProperty("was_forced")
    private boolean isWasForced;
    @JsonProperty("original_amount")
    private double originalAmount;
    @JsonProperty("remaining_amount")
    private double remainingAmount;
    @JsonProperty("executed_amount")
    private double executedAmount;
    @JsonProperty("order_id")
    private long orderId;

    private String message;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public double getAvgExecutionPrice() {
        return avgExecutionPrice;
    }

    public void setAvgExecutionPrice(double avgExecutionPrice) {
        this.avgExecutionPrice = avgExecutionPrice;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isLive() {
        return isLive;
    }

    public void setLive(boolean live) {
        isLive = live;
    }

    public boolean isCancelled() {
        return isCancelled;
    }

    public void setCancelled(boolean cancelled) {
        isCancelled = cancelled;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public void setHidden(boolean hidden) {
        isHidden = hidden;
    }

    public boolean isWasForced() {
        return isWasForced;
    }

    public void setWasForced(boolean wasForced) {
        isWasForced = wasForced;
    }

    public double getOriginalAmount() {
        return originalAmount;
    }

    public void setOriginalAmount(double originalAmount) {
        this.originalAmount = originalAmount;
    }

    public double getRemainingAmount() {
        return remainingAmount;
    }

    public void setRemainingAmount(double remainingAmount) {
        this.remainingAmount = remainingAmount;
    }

    public double getExecutedAmount() {
        return executedAmount;
    }

    public void setExecutedAmount(double executedAmount) {
        this.executedAmount = executedAmount;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "BFXOrderResponse [id=" + id + ", symbol=" + symbol + ", exchange=" + exchange + ", side=" + side + ", originalAmount=" + originalAmount + ", orderId=" + orderId + ", message="
                + message + "]";
    }
}
