# AI Resume Analyzer

Small web app: upload a **PDF resume** and paste a **job description**, get a score, summary, missing skills, suggestions, and sample interview questions (via OpenAI).

## Requirements

- **Java 17**
- **Maven**
- An **OpenAI API key** (set as an environment variable—never commit keys to git)

## Run locally

1. Set your API key:

   ```bash
   export OPENAI_API_KEY="your-key-here"
   ```

2. From this project directory:

   ```bash
   mvn compile exec:java
   ```

3. Open **http://localhost:4567/** in your browser (do not open `index.html` via `file://` or the app cannot reach the server).

4. Choose a PDF, paste the job text, click **Analyze**.

## Static hosting (optional)

The UI in `public/index.html` can be hosted on static sites (e.g. GitHub Pages). The **Analyze** button needs a running backend: deploy this Java server somewhere, then set the root URL in the page’s `<meta name="api-base" content="YOUR_BACKEND_URL" />` (no trailing slash).

## License

Use and modify as you like for personal or learning projects.
