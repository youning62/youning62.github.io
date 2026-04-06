import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnalysisFormatter {
    private AnalysisFormatter() {}

    /** Parses raw API response into an AnalysisResult. Returns null if parsing fails. */
    public static AnalysisResult parseResult(String rawJson) {
        String json = extractContentFromResponse(rawJson);
        if (json == null || json.isEmpty()) return null;

        // Fallback responses
        String fallbackMessage = extractJsonStringValue(json, "message");
        if (fallbackMessage != null) {
            String normalized = fallbackMessage.trim().toLowerCase();
            if ("resume is empty".equals(normalized) || "resume is garbled".equals(normalized)) {
                return AnalysisResult.builder()
                    .score(0)
                    .message(normalized)
                    .build();
            }
        }

        int score = 0;
        Pattern scorePattern = Pattern.compile("\"score\"\\s*:\\s*(\\d+)");
        Matcher scoreMatcher = scorePattern.matcher(json);
        if (scoreMatcher.find()) {
            score = Integer.parseInt(scoreMatcher.group(1));
        }

        List<String> missing = parseJsonStringArray(json, "missing_skills");
        if (missing.isEmpty()) missing = parseJsonStringArray(json, "missingSkills");
        List<String> suggestions = parseJsonStringArray(json, "suggestions");
        List<String> questions = parseJsonStringArray(json, "interview_questions");
        if (questions.isEmpty()) questions = parseJsonStringArray(json, "interviewQuestions");
        String summary = extractJsonStringValue(json, "summary");

        return AnalysisResult.builder()
            .score(score)
            .summary(summary)
            .missingSkills(missing)
            .suggestions(suggestions)
            .interviewQuestions(questions)
            .build();
    }

    /** Formats an AnalysisResult for display. */
    public static String formatToDisplay(AnalysisResult result) {
        if (result == null) return null;
        StringBuilder out = new StringBuilder();
        out.append("Score ").append(result.getScore()).append(" / 100\n\n");
        if (result.isFallback()) {
            out.append(result.getMessage()).append("\n====================================");
            return out.toString();
        }
        if (result.getSummary() != null && !result.getSummary().isEmpty()) {
            out.append("Summary: ").append(result.getSummary()).append("\n\n");
        }
        if (!result.getMissingSkills().isEmpty()) {
            out.append("Missing Skills:\n");
            for (int i = 0; i < result.getMissingSkills().size(); i++) {
                out.append("  ").append(i + 1).append(". ").append(result.getMissingSkills().get(i)).append("\n");
            }
            out.append("\n");
        }
        if (!result.getSuggestions().isEmpty()) {
            out.append("Suggestions:\n");
            for (int i = 0; i < result.getSuggestions().size(); i++) {
                out.append("  ").append(i + 1).append(". ").append(result.getSuggestions().get(i)).append("\n");
            }
            out.append("\n");
        }
        if (!result.getInterviewQuestions().isEmpty()) {
            out.append("Questions:\n");
            for (int i = 0; i < result.getInterviewQuestions().size(); i++) {
                out.append("  ").append(i + 1).append(". ").append(result.getInterviewQuestions().get(i)).append("\n");
            }
        }
        out.append("\n====================================");
        return out.toString();
    }

    public static String formatAnalysisResult(String rawJson) {
        AnalysisResult result = parseResult(rawJson);
        if (result == null) return rawJson;
        return formatToDisplay(result);
    }

    private static String extractJsonStringValue(String json, String key) {
        if (json == null) return null;
        Pattern pattern = Pattern.compile(
            "\"" + Pattern.quote(key) + "\"\\s*:\\s*\"((?:\\\\.|[^\"\\\\])*)\"",
            Pattern.DOTALL
        );
        Matcher matcher = pattern.matcher(json);
        if (!matcher.find()) return null;
        return unescapeJsonStringValue(matcher.group(1));
    }

    private static String extractContentFromResponse(String apiResponse) {
        if (apiResponse == null) return null;
        int idx = apiResponse.indexOf("\"content\"");
        if (idx < 0) return apiResponse;
        idx = apiResponse.indexOf(':', idx);
        if (idx < 0) return apiResponse;
        idx++;
        while (idx < apiResponse.length() && (apiResponse.charAt(idx) == ' ' || apiResponse.charAt(idx) == '\t')) idx++;
        if (idx >= apiResponse.length() || apiResponse.charAt(idx) != '"') return apiResponse;
        idx++;
        StringBuilder sb = new StringBuilder();
        while (idx < apiResponse.length()) {
            char c = apiResponse.charAt(idx++);
            if (c == '\\' && idx < apiResponse.length()) {
                char next = apiResponse.charAt(idx++);
                switch (next) {
                    case 'n': sb.append('\n'); break;
                    case 'r': sb.append('\r'); break;
                    case 't': sb.append('\t'); break;
                    case '"': sb.append('"'); break;
                    case '\\': sb.append('\\'); break;
                    default: sb.append(next); break;
                }
            } else if (c == '"') {
                break;
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private static List<String> parseJsonStringArray(String json, String key) {
        List<String> list = new ArrayList<>();
        if (json == null) return list;

        Pattern arrPattern = Pattern.compile(
            "\"" + Pattern.quote(key) + "\"\\s*:\\s*\\[(.*?)\\]",
            Pattern.DOTALL
        );
        Matcher arrMatcher = arrPattern.matcher(json);
        if (!arrMatcher.find()) return list;

        String arrBody = arrMatcher.group(1);
        if (arrBody == null || arrBody.trim().isEmpty()) return list;

        Pattern elemPattern = Pattern.compile("\"((?:\\\\.|[^\"\\\\])*)\"");
        Matcher elemMatcher = elemPattern.matcher(arrBody);
        while (elemMatcher.find()) {
            String rawElem = elemMatcher.group(1);
            list.add(unescapeJsonStringValue(rawElem));
        }
        return list;
    }

    private static String unescapeJsonStringValue(String s) {
        if (s == null) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c != '\\') {
                sb.append(c);
                continue;
            }
            if (i + 1 >= s.length()) break;
            char n = s.charAt(++i);
            switch (n) {
                case 'n': sb.append('\n'); break;
                case 'r': sb.append('\r'); break;
                case 't': sb.append('\t'); break;
                case '"': sb.append('"'); break;
                case '\\': sb.append('\\'); break;
                case 'b': sb.append('\b'); break;
                case 'f': sb.append('\f'); break;
                case 'u':
                    if (i + 4 < s.length()) {
                        String hex = s.substring(i + 1, i + 5);
                        try {
                            sb.append((char) Integer.parseInt(hex, 16));
                            i += 4;
                        } catch (Exception ignored) {
                            sb.append('?');
                        }
                    } else {
                        sb.append('?');
                    }
                    break;
                default:
                    sb.append(n);
                    break;
            }
        }
        return sb.toString();
    }
}
