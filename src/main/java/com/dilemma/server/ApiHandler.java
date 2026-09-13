package com.dilemma.server;

import com.dilemma.engine.Interpretation;
import com.dilemma.engine.QuestionBank;
import com.dilemma.engine.ScoringEngine;
import com.dilemma.engine.Session;
import com.dilemma.engine.SessionManager;
import com.dilemma.model.Question;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public final class ApiHandler implements HttpHandler {

    private static final int TOTAL_QUESTIONS = 30;
    private final SessionManager sessions = new SessionManager();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        try {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                respond(exchange, 405, Json.obj().put("error", "method not allowed").build());
                return;
            }
            Map<String, String> form = Json.parseForm(exchange.getRequestBody());

            if (path.equals("/api/start")) {
                handleStart(exchange);
            } else if (path.equals("/api/answer")) {
                handleAnswer(exchange, form);
            } else {
                respond(exchange, 404, Json.obj().put("error", "not found").build());
            }
        } catch (Exception e) {
            respond(exchange, 500, Json.obj().put("error", "internal error: " + e.getMessage()).build());
        }
    }

    private void handleStart(HttpExchange exchange) throws IOException {
        Session session = sessions.create();
        Question q = session.currentQuestion(QuestionBank.all());
        respond(exchange, 200, questionPayload(session, q).build());
    }

    private void handleAnswer(HttpExchange exchange, Map<String, String> form) throws IOException {
        String sid = form.get("sid");
        String qidStr = form.get("qid");
        String choiceStr = form.get("choice");

        Session session = sid == null ? null : sessions.get(sid);
        if (session == null) {
            respond(exchange, 400, Json.obj().put("error", "unknown or expired session").build());
            return;
        }

        int qid, choice;
        try {
            qid = Integer.parseInt(qidStr);
            choice = Integer.parseInt(choiceStr);
        } catch (NumberFormatException nfe) {
            respond(exchange, 400, Json.obj().put("error", "bad request").build());
            return;
        }

        Question expected = session.currentQuestion(QuestionBank.all());
        if (expected.id != qid || choice < 0 || choice > 3) {
            respond(exchange, 409, Json.obj().put("error", "question out of sync, refresh").build());
            return;
        }

        session.recordAnswer(qid, choice);

        if (session.isComplete()) {
            respond(exchange, 200, resultsPayload(session).build());
            return;
        }

        Question next = session.currentQuestion(QuestionBank.all());
        respond(exchange, 200, questionPayload(session, next).build());
    }

    private Json questionPayload(Session session, Question q) {
        List<String> optionTexts = q.options.stream().map(o -> o.text).toList();
        Json question = Json.obj()
            .put("id", q.id)
            .put("text", q.text)
            .putRaw("options", Json.arrayOfStrings(optionTexts));
        return Json.obj()
            .put("done", false)
            .put("sessionId", session.id)
            .put("questionNumber", session.answeredCount() + 1)
            .put("total", TOTAL_QUESTIONS)
            .putRaw("question", question.build());
    }

    private Json resultsPayload(Session session) {
        ScoringEngine.Result result = ScoringEngine.score(session.answers);
        Map<String, String> blurbs = Interpretation.attributeBlurbs(result.scores);
        String archetype = Interpretation.archetype(result.scores);
        String consistencyNote = Interpretation.consistencyNote(result.consistency, result.consistencyPairsEvaluated);

        return Json.obj()
            .put("done", true)
            .put("sessionId", session.id)
            .putRaw("scores", Json.objectOfInts(result.scores))
            .putRaw("blurbs", Json.objectOfStrings(blurbs))
            .put("archetype", archetype)
            .put("consistency", result.consistency)
            .put("consistencyNote", consistencyNote);
    }

    private void respond(HttpExchange exchange, int status, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}
