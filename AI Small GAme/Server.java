import static spark.Spark.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import spark.Route;

public class Server {
    public static void main(String[] args) {
        port(4567);

        Path publicDir = Paths.get(System.getProperty("user.dir"), "public").toAbsolutePath().normalize();
        System.err.println("[Server] working dir: " + System.getProperty("user.dir"));
        System.err.println("[Server] open http://localhost:4567/  (UI from " + publicDir.resolve("index.html") + ")");

        options("/*", (request, response) -> {
            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }

            String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }

            response.header("Access-Control-Allow-Origin", "*");
            response.status(200);
            return "OK";
        });

        before((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
        });

        Route indexHandler = (req, res) -> {
            res.type("text/html; charset=utf-8");
            res.header("Cache-Control", "no-store");
            Path page = publicDir.resolve("index.html");
            try {
                return Files.readString(page, StandardCharsets.UTF_8);
            } catch (IOException e) {
                res.status(500);
                return "<p>Cannot read public/index.html: " + escapeHtml(e.getMessage()) + "</p><p>Working dir: "
                    + escapeHtml(System.getProperty("user.dir")) + "</p>";
            }
        };
        get("/", indexHandler);
        get("/index.html", indexHandler);

        post("/analyze", (req, res) -> {
            res.type("application/json");
            try {
                String body = req.body();
                String job = extractJsonStringField(body, "job");
                String b64 = extractJsonStringField(body, "resumePdfBase64");

                byte[] pdfBytes = new byte[0];
                if (b64 != null && !b64.isBlank()) {
                    try {
                        pdfBytes = Base64.getDecoder().decode(b64);
                    } catch (IllegalArgumentException ignored) {
                        pdfBytes = new byte[0];
                    }
                }

                AnalysisResult result = ResumeAnalysisService.analyze(pdfBytes, job != null ? job : "");
                return analysisResultToJson(result);
            } catch (Exception e) {
                e.printStackTrace();
                res.status(500);
                return errorJson("Server error: " + e.getMessage());
            }
        });
    }

    private static String escapeHtml(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }

    private static String errorJson(String message) {
        return "{\"score\":0,\"summary\":\"\",\"missingSkills\":[],\"suggestions\":["
            + jsonQuote(message == null ? "error" : message)
            + "],\"questions\":[]}";
    }

    private static String extractJsonStringField(String json, String key) {
        if (json == null) {
            return null;
        }
        String needle = "\"" + key + "\"";
        int k = json.indexOf(needle);
        if (k < 0) {
            return null;
        }
        int colon = json.indexOf(':', k + needle.length());
        if (colon < 0) {
            return null;
        }
        int i = colon + 1;
        while (i < json.length() && Character.isWhitespace(json.charAt(i))) {
            i++;
        }
        if (i >= json.length() || json.charAt(i) != '"') {
            return null;
        }
        i++;
        StringBuilder sb = new StringBuilder();
        while (i < json.length()) {
            char c = json.charAt(i++);
            if (c == '\\' && i < json.length()) {
                char n = json.charAt(i++);
                switch (n) {
                    case '"', '\\', '/' -> sb.append(n);
                    case 'n' -> sb.append('\n');
                    case 'r' -> sb.append('\r');
                    case 't' -> sb.append('\t');
                    default -> sb.append(n);
                }
            } else if (c == '"') {
                break;
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private static String analysisResultToJson(AnalysisResult r) {
        List<String> missing = new ArrayList<>(r.getMissingSkills());
        List<String> suggestions = new ArrayList<>(r.getSuggestions());
        List<String> questions = new ArrayList<>(r.getInterviewQuestions());

        if (r.isFallback() && r.getMessage() != null && !r.getMessage().isBlank()) {
            suggestions = new ArrayList<>(List.of(r.getMessage()));
        }

        StringBuilder sb = new StringBuilder();
        sb.append("{\"score\":").append(r.getScore());
        sb.append(",\"summary\":").append(jsonQuote(r.getSummary()));
        sb.append(",\"missingSkills\":").append(stringListToJson(missing));
        sb.append(",\"suggestions\":").append(stringListToJson(suggestions));
        sb.append(",\"questions\":").append(stringListToJson(questions));
        sb.append("}");
        return sb.toString();
    }

    private static String stringListToJson(List<String> items) {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < items.size(); i++) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append(jsonQuote(items.get(i)));
        }
        sb.append(']');
        return sb.toString();
    }

    private static String jsonQuote(String s) {
        if (s == null) {
            return "\"\"";
        }
        StringBuilder sb = new StringBuilder(s.length() + 2);
        sb.append('"');
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"' -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default -> {
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
                }
            }
        }
        sb.append('"');
        return sb.toString();
    }
}
