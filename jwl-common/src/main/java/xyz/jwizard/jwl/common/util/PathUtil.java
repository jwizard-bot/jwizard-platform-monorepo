package xyz.jwizard.jwl.common.util;

public class PathUtil {
    private PathUtil() {
    }

    public static String combinePaths(String prefix, String path) {
        if (prefix == null || prefix.isBlank() || prefix.equals("/")) {
            return ensureLeadingSlash(path);
        }
        final String cleanPrefix = prefix.replaceAll("/+$", ""); // remove slash from the end
        final String cleanPath = path.replaceAll("^/+", ""); // remove slash from the beginning
        return ensureLeadingSlash(cleanPrefix + "/" + cleanPath);
    }

    public static String ensureLeadingSlash(String path) {
        if (path == null || path.isEmpty() || path.equals("/")) {
            return "/";
        }
        return path.startsWith("/") ? path : "/" + path;
    }
}
