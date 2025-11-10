package org.hamdan;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.hamdan.models.Ticket;

@ApplicationScoped
public class TicketRepository implements PanacheRepository<Ticket> {
}
