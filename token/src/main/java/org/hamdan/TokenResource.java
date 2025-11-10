package org.hamdan;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/jwt")
public class TokenResource {

    @Inject
    TokenService service;

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public TokenDto generateJwt(@PathParam("id") String id) {
        return new TokenDto(service.generateToken(id));
    }

}
