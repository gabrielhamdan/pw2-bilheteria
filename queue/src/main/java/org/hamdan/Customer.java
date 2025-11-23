package org.hamdan;

import jakarta.ws.rs.sse.SseEventSink;

public class Customer {

    private final String id;
    private final SseEventSink eventSink;
    private String token;

    public Customer(String id, SseEventSink eventSink) {
        this.id = id;
        this.eventSink = eventSink;
    }

    public String getId() {
        return id;
    }

    public SseEventSink getEventSink() {
        return eventSink;
    }

}
