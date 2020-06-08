package net.silthus.inventorykeeper.mock;

import be.seeseemelk.mockbukkit.ServerMock;
import com.google.inject.util.Providers;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.messaging.Messenger;

public class CustomServerMock extends ServerMock {

    private final ServicesManager servicesManager;
    private final Messenger messenger;

    public CustomServerMock() {
        this.servicesManager = new ServiceManagerMock();
        this.messenger = new MessengerMock();
    }

    @Override
    public ServicesManager getServicesManager() {
        return servicesManager;
    }

    @Override
    public Messenger getMessenger() {
        return messenger;
    }
}
