import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Encapsulates the resume analysis result from the API.
 */
public class AnalysisResult {
    private final int score;
    private final String summary;
    private final String message;  // fallback: "resume is empty" or "resume is garbled"
    private final List<String> missingSkills;
    private final List<String> suggestions;
    private final List<String> interviewQuestions;

    private AnalysisResult(Builder b) {
        this.score = b.score;
        this.summary = b.summary;
        this.message = b.message;
        this.missingSkills = b.missingSkills == null ? List.of() : Collections.unmodifiableList(new ArrayList<>(b.missingSkills));
        this.suggestions = b.suggestions == null ? List.of() : Collections.unmodifiableList(new ArrayList<>(b.suggestions));
        this.interviewQuestions = b.interviewQuestions == null ? List.of() : Collections.unmodifiableList(new ArrayList<>(b.interviewQuestions));
    }

    public int getScore() { return score; }
    public String getSummary() { return summary; }
    public String getMessage() { return message; }
    public List<String> getMissingSkills() { return missingSkills; }
    public List<String> getSuggestions() { return suggestions; }
    public List<String> getInterviewQuestions() { return interviewQuestions; }

    /** True if this is a fallback result (empty or garbled resume). */
    public boolean isFallback() { return message != null && !message.isEmpty(); }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private int score;
        private String summary;
        private String message;
        private List<String> missingSkills;
        private List<String> suggestions;
        private List<String> interviewQuestions;

        public Builder score(int score) { this.score = score; return this; }
        public Builder summary(String summary) { this.summary = summary; return this; }
        public Builder message(String message) { this.message = message; return this; }
        public Builder missingSkills(List<String> list) { this.missingSkills = list; return this; }
        public Builder suggestions(List<String> list) { this.suggestions = list; return this; }
        public Builder interviewQuestions(List<String> list) { this.interviewQuestions = list; return this; }

        public AnalysisResult build() { return new AnalysisResult(this); }
    }
}
