package org.hamdan;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.hamdan.client.QueueClient;
import org.hamdan.models.PurchaseDto;
import org.hamdan.models.PurchaseResponse;
import org.hamdan.models.Ticket;
import org.hamdan.models.TicketDto;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class TicketService {

    private static final int MAX_TICKETS = 5;

    @Inject
    TicketRepository ticketRepository;

    @Inject
    @RestClient
    QueueClient queueClient;

    @Inject
    JwtBlacklist blacklist;

    @Transactional
    public void init(@Observes StartupEvent ev) {
        final Double price = 180.0;
        for (char c = 'A'; c <= 'Z'; c++)
            for (int i = 0; i < 10; i++) {
                Ticket t = new Ticket();
                t.setPrice(price);
                t.setSeat(String.format("%c%d", c, i+1));
                t.setTaken(false);
                ticketRepository.persist(t);
            }
    }

    public List<Ticket> listTickets(boolean available) {
        List<Ticket> tickets = ticketRepository.listAll();

        if (available)
            return tickets.stream().filter(t -> !t.getTaken()).toList();

        return tickets;
    }

    @Transactional
    public PurchaseResponse purchaseTickets(String token, PurchaseDto purchaseDto) {
        if (purchaseDto.tickets().size() > MAX_TICKETS)
            return new PurchaseResponse(String.format("Número máximo (%d) de ingressos por cliente excedido.", MAX_TICKETS));

        List<Ticket> tickets = new ArrayList<>();

        for (TicketDto ticketDto : purchaseDto.tickets()) {
            Ticket ticket = ticketRepository.findById(ticketDto.id());
            ticket.setTaken(true);
            ticketRepository.persist(ticket);
            tickets.add(ticket);
        }

        blacklist.revoke(token);

        queueClient.serveNext();

        return new PurchaseResponse("Compra efetuada com sucesso.", tickets);
    }

}
