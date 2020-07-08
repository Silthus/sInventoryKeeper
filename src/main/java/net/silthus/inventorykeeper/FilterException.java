package net.silthus.inventorykeeper;

public class FilterException extends Exception {

    public FilterException(String message) {
        super(message);
    }

    public FilterException(String message, Throwable cause) {
        super(message, cause);
    }

    public FilterException(Throwable cause) {
        super(cause);
    }
}
