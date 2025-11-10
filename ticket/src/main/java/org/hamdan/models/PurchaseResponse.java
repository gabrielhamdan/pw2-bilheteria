package org.hamdan.models;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PurchaseResponse(String message, List<Ticket> tickets) {

    public PurchaseResponse(String message) {
        this(message, null);
    }

}
