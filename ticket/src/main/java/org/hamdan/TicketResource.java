package org.hamdan;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.hamdan.models.PurchaseDto;
import org.hamdan.models.PurchaseResponse;
import org.hamdan.models.Ticket;

import java.util.List;

@Path("/tickets")
@RolesAllowed("Customer")
public class TicketResource {

    @Inject
    TicketService ticketService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Ticket> listTickets(@QueryParam("available") boolean available) {
        return ticketService.listTickets(available);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public PurchaseResponse purchaseTickets(@HeaderParam("Authorization") String token, PurchaseDto purchaseDto) {
        return ticketService.purchaseTickets(token, purchaseDto);
    }

}
