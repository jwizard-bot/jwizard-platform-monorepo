package xyz.jwizard.jwl.queue.exchange;

public enum DefaultExchangeType implements ExchangeType {
    DIRECT("direct"),
    TOPIC("topic"),
    FANOUT("fanout"),
    ;

    private final String type;

    DefaultExchangeType(String type) {
        this.type = type;
    }

    @Override
    public String getType() {
        return type;
    }
}
