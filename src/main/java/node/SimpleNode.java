package node;

import message.TransportMessage;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SimpleNode<T> {
    private final Long id;
    private final Long nextId;
    private final Queue<TransportMessage<T>> messageQueue;

    private SimpleNode(Long id, Long nextId) {
        this.id = id;
        this.nextId = nextId;
        this.messageQueue = new ConcurrentLinkedQueue<>();
    }

    public void receiveMessage(TransportMessage<T> message) {
        if(this.id.equals(message.getDestination())) {
            System.out.println("Node-" + this.id + " got message with " + message.getPayload().toString());
        } else if(!this.messageQueue.offer(message)) {
            throw new RuntimeException();
        }
    }

    public Queue<TransportMessage<T>> getMessageQueue() {
        return this.messageQueue;
    }

    public Long getId() {
        return this.id;
    }

    public Long getNextId() {
        return this.nextId;
    }

    public static <T> Map<Long, SimpleNode<T>> initSimpleNodeMap(long size) {
        Map<Long, SimpleNode<T>> resultMap = new HashMap<>();
        for(long i = 0; i < size - 1; i++) {
            SimpleNode<T> currentNode = new SimpleNode<>(i, i+1);
            resultMap.put(i, currentNode);
        }
        resultMap.put(size - 1, new SimpleNode<>(size, 0L));
        return Collections.unmodifiableMap(resultMap);
    }
}
