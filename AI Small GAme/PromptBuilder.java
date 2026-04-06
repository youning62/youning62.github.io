public class PromptBuilder {
    private PromptBuilder() {}

    public static String buildPrompt(String resume, String job) {
        String promptTemplate = "You are a professional career coach.\n\n"
            + "Analyze the resume based on the job description.\n\n"
            + "Return ONLY valid JSON in this format:\n"
            + "{\n"
            + "  \"score\": number,\n"
            + "  \"summary\": \"short explanation\",\n"
            + "  \"missing_skills\": [string, string, string],\n"
            + "  \"suggestions\": [string, string, string],\n"
            + "  \"interview_questions\": [string, string, string, string, string]\n"
            + "}\n\n"
            + "Special fallback rules:\n"
            + "- If resume is empty, return only: {\"score\":0,\"message\":\"resume is empty\"}\n"
            + "- If resume content is garbled/unreadable, return only: {\"score\":0,\"message\":\"resume is garbled\"}\n"
            + "- In fallback cases, do not include summary, missing_skills, suggestions, or interview_questions\n\n"
            + "Resume:\n{resume}\n\nJob Description:\n{job}";

        return promptTemplate.replace("{job}", job).replace("{resume}", resume);
    }
}
