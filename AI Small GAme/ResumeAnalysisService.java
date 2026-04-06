import java.util.List;

/**
 * PDF bytes → prompt → OpenAI → {@link AnalysisResult}.
 */
public final class ResumeAnalysisService {

    private ResumeAnalysisService() {}

    public static AnalysisResult analyze(byte[] pdfBytes, String jobDescription) {
        String resume = ResumeReader.readResumeFromPdfBytes(pdfBytes);
        if (resume == null || resume.isBlank()) {
            return AnalysisResult.builder()
                .score(0)
                .message("resume is empty")
                .build();
        }

        String job = jobDescription == null ? "" : jobDescription.trim();
        String prompt = PromptBuilder.buildPrompt(resume, job);
        String rawResponse = OpenAIClient.callOpenAI(prompt);
        if (rawResponse == null || rawResponse.isBlank()) {
            return AnalysisResult.builder()
                .score(0)
                .suggestions(List.of("OpenAI request failed; check API key and network."))
                .build();
        }

        AnalysisResult parsed = AnalysisFormatter.parseResult(rawResponse);
        if (parsed == null) {
            return AnalysisResult.builder()
                .score(0)
                .suggestions(List.of("Could not parse AI response."))
                .build();
        }
        return parsed;
    }
}
