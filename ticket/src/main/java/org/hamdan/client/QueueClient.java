package org.hamdan.client;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(baseUri = "http://localhost:8080")
public interface QueueClient {

    @GET
    @Path("/queue/next")
    void serveNext();

}
