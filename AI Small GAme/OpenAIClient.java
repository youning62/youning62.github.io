import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class OpenAIClient {
    private OpenAIClient() {}

    public static String callOpenAI(String prompt) {
        try {
            String apiKey = System.getenv("OPENAI_API_KEY");
            if (apiKey == null || apiKey.isBlank()) {
                System.err.println("Missing OPENAI_API_KEY environment variable.");
                return null;
            }
            // #region agent log
            DebugLogger.log(
                "resume-debug",
                "H6",
                "OpenAIClient.java:15",
                "API key state before request",
                "{\"keyNull\":" + (apiKey == null) + ",\"keyBlank\":" + (apiKey != null && apiKey.isBlank()) + ",\"keyLength\":" + (apiKey == null ? 0 : apiKey.length()) + "}"
            );
            // #endregion

            URL url = new URL("https://api.openai.com/v1/chat/completions");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            // #region agent log
            String authHeader = connection.getRequestProperty("Authorization");
            DebugLogger.log(
                "resume-debug",
                "H7",
                "OpenAIClient.java:30",
                "Authorization header after set",
                "{\"authNull\":" + (authHeader == null) + ",\"authLength\":" + (authHeader == null ? 0 : authHeader.length()) + ",\"hasBearerPrefix\":" + (authHeader != null && authHeader.startsWith("Bearer ")) + "}"
            );
            // #endregion

            String escapedPrompt = escapeJson(prompt);
            String jsonInputString = "{"
                + "\"model\": \"gpt-4o-mini\","
                + "\"messages\": ["
                + "{\"role\": \"user\", \"content\": \"" + escapedPrompt + "\"}"
                + "]"
                + "}";

            try (OutputStream os = connection.getOutputStream()) {
                os.write(jsonInputString.getBytes("UTF-8"));
            }

            int code = connection.getResponseCode();
            // #region agent log
            DebugLogger.log(
                "resume-debug",
                "H7",
                "OpenAIClient.java:50",
                "HTTP response received",
                "{\"statusCode\":" + code + "}"
            );
            // #endregion
            InputStream bodyStream = (code >= 200 && code < 300)
                ? connection.getInputStream()
                : connection.getErrorStream();
            if (bodyStream == null) bodyStream = InputStream.nullInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(bodyStream));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            if (code >= 200 && code < 300) {
                return response.toString();
            }

            if (code == 429) {
                System.err.println("I don't have enough money to pay for the API.");
            } else {
                System.err.println("Wrong API" + code + ": " + response.toString());
            }
            return null;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String escapeJson(String s) {
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
                    // Escape all other control chars to keep JSON valid.
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
                    break;
            }
        }
        return sb.toString();
    }
}
