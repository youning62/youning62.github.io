import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DebugLogger {
    private static final String LOG_PATH = "/Users/leo/Desktop/AI Small GAme/.cursor/debug-acb4c8.log";
    private static final String SESSION_ID = "acb4c8";

    private DebugLogger() {}

    public static void log(String runId, String hypothesisId, String location, String message, String dataJson) {
        long ts = System.currentTimeMillis();
        String line = "{"
            + "\"sessionId\":\"" + esc(SESSION_ID) + "\","
            + "\"runId\":\"" + esc(runId) + "\","
            + "\"hypothesisId\":\"" + esc(hypothesisId) + "\","
            + "\"location\":\"" + esc(location) + "\","
            + "\"message\":\"" + esc(message) + "\","
            + "\"data\":" + (dataJson == null ? "{}" : dataJson) + ","
            + "\"timestamp\":" + ts
            + "}";
        try {
            Path parent = Path.of(LOG_PATH).getParent();
            if (parent != null) Files.createDirectories(parent);
        } catch (IOException ignored) {
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(LOG_PATH, true))) {
            bw.write(line);
            bw.newLine();
        } catch (IOException ignored) {
        }
    }

    private static String esc(String s) {
        if (s == null) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"': sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if (c < 0x20) sb.append(String.format("\\u%04x", (int) c));
                    else sb.append(c);
                    break;
            }
        }
        return sb.toString();
    }
}
