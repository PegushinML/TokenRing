package message;

public class TransportMessage<T> {
    private final Long from;
    private final Long to;
    private final T payload;

    private TransportMessage(Long from, Long to, T payload) {
        this.from = from;
        this.to = to;
        this.payload = payload;
    }

    public Long getSource() {
        return this.from;
    }

    public Long getDestination() {
        return this.to;
    }

    public T getPayload() {
        return this.payload;
    }

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public static final class Builder<T> {
        private Long from;
        private Long to;
        private T payload;

        public Builder<T> from(Long id) {
            this.from = id;
            return this;
        }

        public Builder<T> to(Long id) {
            this.to = id;
            return this;
        }

        public Builder<T> with(T payload) {
            this.payload = payload;
            return this;
        }

        public TransportMessage<T> build() {
            return new TransportMessage<>(from, to, payload);
        }
    }

}
