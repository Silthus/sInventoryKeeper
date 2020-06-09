package net.silthus.inventorykeeper.api;

/**
 * Thrown if something went wrong when registering an {@link InventoryFilter}.
 */
public class FilterRegistrationException extends Exception {

    public FilterRegistrationException() {
    }

    public FilterRegistrationException(String message) {
        super(message);
    }

    public FilterRegistrationException(String message, Throwable cause) {
        super(message, cause);
    }

    public FilterRegistrationException(Throwable cause) {
        super(cause);
    }

    public FilterRegistrationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
