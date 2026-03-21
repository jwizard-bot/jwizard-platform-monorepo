package xyz.jwizard.jwl.common.util.net;

public class NetworkUtil {
    private NetworkUtil() {
    }

    public static HostPort parseHostPort(String address) {
        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException("Address string cannot be null or empty.");
        }
        final String[] parts = address.split(":");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid address format. Expected 'host:port', " +
                "but got: '" + address + "'");
        }
        try {
            final String host = parts[0].trim();
            final int port = Integer.parseInt(parts[1].trim());
            return new HostPort(host, port);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid port number in address definition: '" +
                address + "'", e);
        }
    }
}
