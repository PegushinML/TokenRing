package message;

public interface Message<T> {
    Long getSource();

    Long getDestination();

    T getPayload();

    /**
     * finish the lifecycle of message in token ring and returns time in milliseconds;
     * that method exists for benchmarks only;
     * logic of usage id quite incomplete, correct behaviour guaranteed only at first usage;
     * @return amount of milliseconds between sending and receiving
     */
    long finish();
}
