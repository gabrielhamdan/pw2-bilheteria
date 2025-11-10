package org.hamdan.models;

import java.util.List;

public record PurchaseDto(List<TicketDto> tickets) {
}
