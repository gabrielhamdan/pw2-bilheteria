package org.hamdan;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Path("/jwt")
public class TokenResource {

    @Inject
    TokenService service;

    @ConfigProperty(name = "auth.hmac.secret")
    String secret;

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public TokenDto generateJwt(@PathParam("id") String id, @HeaderParam("X-Signature") String signature) {
        String assinatura = HmacUtil.hmacSha256(id, secret);

        if (!assinatura.equals(signature))
            throw new WebApplicationException("Forbidden", 403);

        return new TokenDto(service.generateToken(id));
    }

}
