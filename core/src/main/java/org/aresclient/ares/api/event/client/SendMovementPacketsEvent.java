package org.aresclient.ares.api.event.client;

import org.aresclient.ares.api.event.AresEvent;

public class SendMovementPacketsEvent extends AresEvent {
    public SendMovementPacketsEvent(Era era) {
        super("SendMovementPacketsEvent", era);
    }
}
