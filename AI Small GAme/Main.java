import java.util.Scanner;
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("Enter resume PDF path:");
        String resumePdfPath = scanner.nextLine();
        // #region agent log
        DebugLogger.log(
            "resume-debug",
            "H1",
            "Main.java:8",
            "User provided resume path",
            "{\"pathLength\":" + (resumePdfPath == null ? 0 : resumePdfPath.length()) + ",\"path\":\"" + (resumePdfPath == null ? "" : resumePdfPath.replace("\\", "\\\\").replace("\"", "\\\"")) + "\"}"
        );
        // #endregion
        String resume = ResumeReader.readResumeFromPdf(resumePdfPath);
        // #region agent log
        DebugLogger.log(
            "resume-debug",
            "H5",
            "Main.java:20",
            "Resume read result in Main",
            "{\"isNull\":" + (resume == null) + ",\"isBlank\":" + (resume != null && resume.isBlank()) + ",\"resumeLength\":" + (resume == null ? 0 : resume.length()) + "}"
        );
        // #endregion
        if (resume == null || resume.isBlank()) {
            System.out.println("Resume cant find");
            scanner.close();
            return;
        }

        System.out.println("Enter job description:");
        String job = scanner.nextLine();

        System.out.println("Analyzing...");

        String prompt = PromptBuilder.buildPrompt(resume, job);
        String rawResult = OpenAIClient.callOpenAI(prompt);
        AnalysisResult result = (rawResult != null) ? AnalysisFormatter.parseResult(rawResult) : null;
        if (result != null) {
            System.out.println(AnalysisFormatter.formatToDisplay(result));
        } else {
            System.out.println("Meet some problems, please try again.");
        }

        scanner.close();
    }
}