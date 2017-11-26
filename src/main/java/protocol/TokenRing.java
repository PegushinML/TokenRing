package protocol;

import message.TransportMessage;
import node.SimpleNode;

import java.util.*;

public class TokenRing<T> {
    private final Map<Long, SimpleNode<T>> nodeMap;
    private final Map<Long, Thread> threadMap;
    private boolean isActive;
    private final List<Long> metricsList;

    public TokenRing(long size) {
        this.nodeMap = SimpleNode.initSimpleNodeMap(size, this);
        this.threadMap = initThreadMap(size);
        this.isActive = true;
        this.metricsList = Collections.synchronizedList(new ArrayList<>());
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

    public String getNodeDistr() {
        StringBuilder result = new StringBuilder();
        for(long key : this.nodeMap.keySet()) {
            result.append(this.nodeMap.get(key).getMessageQueue().size());
            result.append(";");
        }
        return result.toString();
    }

    public void addMetric(long value) {
        this.metricsList.add(value);
    }

    public List<Long> getMetricsList() {
        return this.metricsList;
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
