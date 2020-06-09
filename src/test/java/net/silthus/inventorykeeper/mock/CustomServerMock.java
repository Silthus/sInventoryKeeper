package net.silthus.inventorykeeper.mock;

import be.seeseemelk.mockbukkit.MockCommandMap;
import be.seeseemelk.mockbukkit.ServerMock;
import org.bukkit.help.HelpMap;
import org.bukkit.help.HelpTopic;
import org.bukkit.help.HelpTopicFactory;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.messaging.Messenger;

import java.util.Collection;
import java.util.List;

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

    @Override
    public MockCommandMap getCommandMap() {

        return super.getCommandMap();
    }

    @Override
    public HelpMap getHelpMap() {
        return new HelpMap() {
            @Override
            public HelpTopic getHelpTopic(String topicName) {
                return null;
            }

            @Override
            public Collection<HelpTopic> getHelpTopics() {
                return null;
            }

            @Override
            public void addTopic(HelpTopic topic) {

            }

            @Override
            public void clear() {

            }

            @Override
            public void registerHelpTopicFactory(Class<?> commandClass, HelpTopicFactory<?> factory) {

            }

            @Override
            public List<String> getIgnoredPlugins() {
                return null;
            }
        };
    }
}
