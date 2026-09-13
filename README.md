# Dilemma Engine

A 30-question behavioral assessment. Every answer secretly moves five hidden
attributes — Moral Rigidity, Strategic Thinking, Risk Tolerance, Empathy, and
Power Orientation — which are revealed as a normalized 0–100 profile at the end,
alongside a Decision Consistency score.

Pure Java (JDK 17+, no external dependencies — uses `com.sun.net.httpserver`)
serving a small HTML/CSS/JS frontend.

## Run it

```bash
cd dilemma-engine
javac -d out $(find src -name "*.java")
java -cp out com.dilemma.Main
```

Then open **http://localhost:8080**.

The port defaults to `8080`; override with `PORT=3000 java -cp out com.dilemma.Main`.

## How it works

- **`model/Question.java`** — a dilemma: 4 options, each with hidden per-attribute
  deltas and an optional `axis` tag used only for consistency checking.
- **`engine/QuestionBank.java`** — the fixed content: all 30 dilemmas, tiered
  BROAD (1–8) / MID (9–20) / HARD (21–30), with 7 thematically linked pairs
  (e.g. a loyalty-vs-justice dilemma early, and a harder version of the same
  underlying tension later) used to detect contradictions.
- **`engine/AdaptiveSelector.java`** — the first 8 questions are shown in a
  fixed broad-tendency order; from question 9 on, the next question is chosen
  from the remaining pool in the current tier, prioritizing whichever
  attributes have been least informed so far. All 30 questions are always
  shown — only the order adapts, and the person never sees the mechanism.
- **`engine/ScoringEngine.java`** — sums the deltas of the options a person
  chose, then normalizes each attribute to 0–100 against the actual min/max
  achievable across the whole 30-question bank. Consistency is computed
  separately from the 7 paired dilemmas: a mismatched `axis` between a pair's
  chosen options counts as a contradiction; scoring itself is never fully
  determined by consistency, only reported alongside it.
- **`engine/Interpretation.java`** — turns the five scores into short
  human-readable blurbs, a two-attribute "archetype" label, and a note about
  how much to trust the profile given the consistency score.
- **`server/`** — a minimal HTTP layer (`ApiHandler` for `/api/start` and
  `/api/answer`, `StaticFileHandler` for the frontend, `Json` for hand-rolled
  JSON encoding/decoding so the project needs zero external libraries).
- **`web/`** — the frontend: a dossier-style single page, three screens
  (start / question / results), talking to the two API endpoints.

Nothing about which attribute a question measures, the scoring weights, or the
adaptive logic is ever sent to the client — only question text, four option
strings, and (at the very end) the final scores and blurbs.
