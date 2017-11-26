package node;

import message.Message;
import protocol.TokenRing;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SimpleNode<T> {
    private final Long id;
    private final Long nextId;
    private final Queue<Message<T>> messageQueue;
    private final TokenRing<T> controller;

    private SimpleNode(Long id, Long nextId, TokenRing<T> protocol) {
        this.id = id;
        this.nextId = nextId;
        this.messageQueue = new ConcurrentLinkedQueue<>();
        this.controller = protocol;
    }

    public void receiveMessage(Message<T> message) {
        if(this.id.equals(message.getDestination())) {
            //System.out.println("Node-" + this.id + " got message with " + message.getPayload().toString() + " for time: " + message.finish());
            this.controller.addMetric(message.finish());
        } else if(!this.messageQueue.offer(message)) {
            throw new RuntimeException();
        }
    }

    public Queue<Message<T>> getMessageQueue() {
        return this.messageQueue;
    }

    public Long getId() {
        return this.id;
    }

    public Long getNextId() {
        return this.nextId;
    }

    public static <T> Map<Long, SimpleNode<T>> initSimpleNodeMap(long size, TokenRing<T> controller) {
        Map<Long, SimpleNode<T>> resultMap = new HashMap<>();
        for(long i = 0; i < size - 1; i++) {
            SimpleNode<T> currentNode = new SimpleNode<>(i, i+1, controller);
            resultMap.put(i, currentNode);
        }
        resultMap.put(size - 1, new SimpleNode<>(size, 0L, controller));
        return Collections.unmodifiableMap(resultMap);
    }
}
