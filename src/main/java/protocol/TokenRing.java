package protocol;

import message.TransportMessage;
import node.SimpleNode;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TokenRing<T> {
    private final Map<Long, SimpleNode<T>> nodeMap;
    private final Map<Long, Thread> threadMap;
    private boolean isActive;

    public TokenRing(long size) {
        this.nodeMap = SimpleNode.initSimpleNodeMap(size);
        this.threadMap = initThreadMap(size);
        this.isActive = true;
    }

    private Map<Long,Thread> initThreadMap(long size) {
        Map<Long, Thread> threadMap = new HashMap<>();
        for(long i = 0; i < size; i++) {
            final long currentId = i;
            threadMap.put(i, new Thread(() -> {
                SimpleNode<T> currentNode = nodeMap.get(currentId);
                SimpleNode<T> nextNode = nodeMap.get(currentNode.getNextId());
                while(!Thread.interrupted()) {
                    if(!this.isActive) {
                        Thread.currentThread().interrupt();
                    }
                    if(this.isActive) {
                        if(!currentNode.getMessageQueue().isEmpty()) {
                            nextNode.receiveMessage(currentNode.getMessageQueue().poll());
                        }
                    }
                }
            }));
        }
        return Collections.unmodifiableMap(threadMap);
    }

    public void printMessagesInfo() {
        int total = 0;
        for(long key : this.nodeMap.keySet()) {
            System.out.println("Node-" + key + " contains " + this.nodeMap.get(key).getMessageQueue().size() + " messages");
            total += this.nodeMap.get(key).getMessageQueue().size();
        }
        System.out.println("Total amount of messages is " + total);
    }

    public void start() {
        for(long key : threadMap.keySet()) {
            threadMap.get(key).start();
            while(!threadMap.get(key).isAlive()) {
                try {
                    System.out.println("Waiting for Thread " + key + " to awaken.");
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        this.isActive = true;
    }

    public void close() {
        this.isActive = false;
    }

    public void requestFromRandomNodeWithPayload(T payload) {
        Double value = Math.random()*(this.threadMap.size() - 1);
        this.messageRequest().from(value.longValue()).with(payload).execute();
    }

    public MessageRequest messageRequest() {
        return new MessageRequest();
    }

    public class MessageRequest {
        private Long from;
        private Long to;
        private T payload;

        public MessageRequest from(Long id) {
            this.from = id;
            return this;
        }

        public MessageRequest to(Long id) {
            this.to = id;
            return this;
        }

        public MessageRequest with(T payload) {
            this.payload = payload;
            return this;
        }

        public void execute() {
            nodeMap.get(from).receiveMessage(TransportMessage.<T>builder().from(from).to(to).with(payload).build());
        }
    }
}
