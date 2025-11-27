package org.hamdan;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.sse.SseEventSink;

@Path("/queue")
public class QueueResource {

    @Inject
    QueueService service;

    @GET
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void enterQueue(@Context SseEventSink eventSink) {
        service.addToQueue(eventSink);
    }

    @GET
    @Path("/next")
    public void serveNext() {
        service.dismissCurrentCustomer();
    }

}
