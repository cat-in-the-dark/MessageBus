package org.catinthedark.network.example;

import java.util.UUID;

public class DisconnectedMessage {
    private UUID clientID;

    public UUID getClientID() {
        return clientID;
    }

    public void setClientID(UUID clientID) {
        this.clientID = clientID;
    }
}
