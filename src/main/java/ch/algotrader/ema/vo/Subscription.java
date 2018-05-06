package ch.algotrader.ema.vo;


public class Subscription {

    private String event;
    private String channel;
    private String pair;

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getPair() {
        return pair;
    }

    public void setPair(String pair) {
        this.pair = pair;
    }

    public static Subscription trades(String pair) {
        final Subscription subscription = new Subscription();
        subscription.event = "subscribe";
        subscription.channel = "trades";
        subscription.pair = pair;
        return subscription;
    }
}
