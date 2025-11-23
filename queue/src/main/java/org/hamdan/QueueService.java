package org.hamdan;

import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.sse.OutboundSseEvent;
import jakarta.ws.rs.sse.Sse;
import jakarta.ws.rs.sse.SseEventSink;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.hamdan.client.TokentClient;

import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.*;

@ApplicationScoped
public class QueueService {

    private final Queue<Customer> queue = new LinkedList<>();
    private final int TOKEN_DURATION = 15;

    private Customer currentCustomer;
    private ScheduledFuture<?> currentTask;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @Inject
    @RestClient
    TokentClient tokentClient;

    @Inject
    Sse sse;

    @ConfigProperty(name = "auth.hmac.secret")
    String secret;

    @Scheduled(cron = "0 10 21 * * ?")
    public void serveNext() {
        Customer customer = queue.poll();

        if (customer == null)
            return;

        String assinatura = HmacUtil.hmacSha256(customer.getId(), secret);

        OutboundSseEvent event = sse.newEventBuilder()
                .name(EQueueStatus.PROCESSING.name())
                .data(new QueueResponseDto(
                        "Em atendimento.",
                        customer.getId(),
                        EQueueStatus.PROCESSING,
                        tokentClient.generateToken(customer.getId(), assinatura).token()
                ).toJson())
                .build();

        var executor = Executors.newSingleThreadExecutor();

        Future<?> future = executor.submit(() -> {
            customer.getEventSink().send(event);
            customer.getEventSink().close();
        });

        try {
            future.get(60, TimeUnit.SECONDS); // ok
            setCurrentCustomer(customer);
        } catch (TimeoutException e) {
            future.cancel(true); // timeout
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace(); // falha
        } finally {
            executor.shutdownNow();
        }
    }

    public void addToQueue(SseEventSink eventSink) {
        Customer customer = new Customer(UUID.randomUUID().toString(), eventSink);

        if (!queue.offer(customer)) {
            OutboundSseEvent waitingMessage = sse.newEventBuilder()
                    .name(EQueueStatus.FAILED.name())
                    .data(new QueueResponseDto("Não foi possível entrar na fila.", null, EQueueStatus.FAILED).toJson())
                    .build();
            eventSink.send(waitingMessage);
            eventSink.close();

            return;
        }

        OutboundSseEvent waitingMessage = sse.newEventBuilder()
                .name(EQueueStatus.AWAITING.name())
                .data(new QueueResponseDto(String.format("Aguarde. Posição na fila: %d.", queue.size()), customer.getId(), EQueueStatus.AWAITING).toJson())
                .build();
        eventSink.send(waitingMessage);

        if (queue.peek().equals(customer) && currentCustomer == null)
            serveNext();
    }

    private synchronized void setCurrentCustomer(Customer customer) {
        this.currentCustomer = customer;

        if (currentTask != null) {
            currentTask.cancel(false);
        }

        if (customer != null) {
            currentTask = scheduler.schedule(() -> {
                onCustomerTimeExpired(customer);
            }, TOKEN_DURATION, TimeUnit.MINUTES);
        }
    }

    private void onCustomerTimeExpired(Customer customer) {
        serveNext();
    }

}
