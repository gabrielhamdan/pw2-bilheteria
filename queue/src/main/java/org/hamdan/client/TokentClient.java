package org.hamdan.client;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(baseUri = "http://localhost:8081")
public interface TokentClient {

    @GET
    @Path("/jwt/{id}")
    TokenDto generateToken(@PathParam("id") String id);

}
