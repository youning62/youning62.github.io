import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class ResumeReader {
    private static final int RESUME_RAW_MAX_CHARS = 3000;

    private ResumeReader() {}

    /** Reads and extracts resume text from raw PDF bytes (e.g. uploaded file). */
    public static String readResumeFromPdfBytes(byte[] bytes) {
        if (bytes == null || bytes.length == 0) return null;
        // #region agent log
        DebugLogger.log(
            "resume-debug",
            "H2",
            "ResumeReader.java:readResumeFromPdfBytes",
            "PDF bytes in upload",
            "{\"bytesLength\":" + bytes.length + "}"
        );
        // #endregion
        return extractTextFromPdfBytes(bytes);
    }

    public static String readResumeFromPdf(String pdfPath) {
        try {
            Path p = Paths.get(pdfPath);
            boolean exists = Files.exists(p);
            boolean isRegularFile = Files.isRegularFile(p);
            // #region agent log
            DebugLogger.log(
                "resume-debug",
                "H1",
                "ResumeReader.java:17",
                "Path basic checks",
                "{\"exists\":" + exists + ",\"isRegularFile\":" + isRegularFile + "}"
            );
            // #endregion
            if (!exists || !isRegularFile) return null;

            byte[] bytes = Files.readAllBytes(Path.of(pdfPath));
            // #region agent log
            DebugLogger.log(
                "resume-debug",
                "H2",
                "ResumeReader.java:32",
                "Read PDF bytes",
                "{\"bytesLength\":" + (bytes == null ? 0 : bytes.length) + "}"
            );
            // #endregion
            if (bytes == null || bytes.length == 0) return null;
            return extractTextFromPdfBytes(bytes);
        } catch (IOException e) {
            // #region agent log
            DebugLogger.log(
                "resume-debug",
                "H2",
                "ResumeReader.java:70",
                "IOException while reading PDF",
                "{\"error\":\"" + e.getClass().getSimpleName().replace("\\", "\\\\").replace("\"", "\\\"") + "\",\"message\":\"" + (e.getMessage() == null ? "" : e.getMessage().replace("\\", "\\\\").replace("\"", "\\\"")) + "\"}"
            );
            // #endregion
            System.err.println("Failed to read PDF: " + e.getMessage());
            return null;
        } catch (Exception e) {
            // #region agent log
            DebugLogger.log(
                "resume-debug",
                "H8",
                "ResumeReader.java:82",
                "Unexpected exception while parsing PDF",
                "{\"error\":\"" + e.getClass().getSimpleName().replace("\\", "\\\\").replace("\"", "\\\"") + "\",\"message\":\"" + (e.getMessage() == null ? "" : e.getMessage().replace("\\", "\\\\").replace("\"", "\\\"")) + "\"}"
            );
            // #endregion
            System.err.println("Failed to parse PDF content.");
            return null;
        }
    }

    private static String extractTextFromPdfBytes(byte[] bytes) {
        try {
            String extracted;
            try (PDDocument document = Loader.loadPDF(bytes)) {
                PDFTextStripper stripper = new PDFTextStripper();
                extracted = stripper.getText(document);
            }

            // #region agent log
            DebugLogger.log(
                "resume-debug",
                "H8",
                "ResumeReader.java:extractTextFromPdfBytes",
                "Extracted text by PDFBox",
                "{\"textNull\":" + (extracted == null) + ",\"textLength\":" + (extracted == null ? 0 : extracted.length()) + ",\"textBlank\":" + (extracted != null && extracted.isBlank()) + "}"
            );
            // #endregion

            if (extracted == null || extracted.isBlank()) return null;
            String normalized = extracted.replace('\r', '\n').replaceAll("\n{3,}", "\n\n").trim();
            if (normalized.length() > RESUME_RAW_MAX_CHARS) {
                // #region agent log
                DebugLogger.log(
                    "resume-debug",
                    "H4",
                    "ResumeReader.java:extractTextFromPdfBytes",
                    "Truncating extracted text",
                    "{\"beforeLength\":" + normalized.length() + ",\"max\":" + RESUME_RAW_MAX_CHARS + "}"
                );
                // #endregion
                normalized = normalized.substring(0, RESUME_RAW_MAX_CHARS);
            }
            return normalized;
        } catch (IOException e) {
            System.err.println("Failed to read PDF: " + e.getMessage());
            return null;
        } catch (Exception e) {
            System.err.println("Failed to parse PDF content.");
            return null;
        }
    }
}
