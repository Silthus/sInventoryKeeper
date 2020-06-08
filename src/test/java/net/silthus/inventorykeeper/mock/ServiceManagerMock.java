package net.silthus.inventorykeeper.mock;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;

import java.util.Collection;
import java.util.List;

public class ServiceManagerMock implements ServicesManager {
    @Override
    public <T> void register(Class<T> service, T provider, Plugin plugin, ServicePriority priority) {
        throw new UnimplementedOperationException();
    }

    @Override
    public void unregisterAll(Plugin plugin) {
        throw new UnimplementedOperationException();
    }

    @Override
    public void unregister(Class<?> service, Object provider) {
        throw new UnimplementedOperationException();
    }

    @Override
    public void unregister(Object provider) {
        throw new UnimplementedOperationException();
    }

    @Override
    public <T> T load(Class<T> service) {
        throw new UnimplementedOperationException();
    }

    @Override
    public <T> RegisteredServiceProvider<T> getRegistration(Class<T> service) {
        throw new UnimplementedOperationException();
    }

    @Override
    public List<RegisteredServiceProvider<?>> getRegistrations(Plugin plugin) {
        throw new UnimplementedOperationException();
    }

    @Override
    public <T> Collection<RegisteredServiceProvider<T>> getRegistrations(Class<T> service) {
        throw new UnimplementedOperationException();
    }

    @Override
    public Collection<Class<?>> getKnownServices() {
        throw new UnimplementedOperationException();
    }

    @Override
    public <T> boolean isProvidedFor(Class<T> service) {
        throw new UnimplementedOperationException();
    }
}
