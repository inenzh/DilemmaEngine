package com.dilemma.engine;

import com.dilemma.model.Attributes;
import com.dilemma.model.Question;

import java.util.*;

public final class ScoringEngine {

    private static final Map<String, Integer> MIN_POSSIBLE = new HashMap<>();
    private static final Map<String, Integer> MAX_POSSIBLE = new HashMap<>();

    static {
        for (String attr : Attributes.ALL) {
            int min = 0, max = 0;
            for (Question q : QuestionBank.all()) {
                int optMin = Integer.MAX_VALUE, optMax = Integer.MIN_VALUE;
                for (Question.Option o : q.options) {
                    int v = o.deltas.getOrDefault(attr, 0);
                    optMin = Math.min(optMin, v);
                    optMax = Math.max(optMax, v);
                }
                min += optMin;
                max += optMax;
            }
            MIN_POSSIBLE.put(attr, min);
            MAX_POSSIBLE.put(attr, max);
        }
    }

    public static final class Result {
        public final Map<String, Integer> scores = new LinkedHashMap<>(); // attr -> 0-100
        public int consistency; // 0-100
        public int contradictions;
        public int consistencyPairsEvaluated;
    }

    public static Result score(Map<Integer, Integer> answers) {
        Result result = new Result();

        Map<String, Integer> raw = new HashMap<>();
        for (String attr : Attributes.ALL) raw.put(attr, 0);

        for (Map.Entry<Integer, Integer> e : answers.entrySet()) {
            Question q = QuestionBank.byId(e.getKey());
            Question.Option chosen = q.options.get(e.getValue());
            for (Map.Entry<String, Integer> d : chosen.deltas.entrySet()) {
                raw.merge(d.getKey(), d.getValue(), Integer::sum);
            }
        }

        for (String attr : Attributes.ALL) {
            int min = MIN_POSSIBLE.get(attr);
            int max = MAX_POSSIBLE.get(attr);
            int r = raw.get(attr);
            double pct;
            if (max == min) {
                pct = 50;
            } else {
                pct = (r - min) * 100.0 / (max - min);
            }
            int clamped = (int) Math.round(Math.max(0, Math.min(100, pct)));
            result.scores.put(attr, clamped);
        }

        // Consistency: for each unique pair (id < pairsWith) where both were answered
        // with an axis-tagged option, a contradiction is a mismatched axis.
        int contradictions = 0;
        int evaluated = 0;
        Set<Integer> seenPairRoot = new HashSet<>();
        for (Question q : QuestionBank.all()) {
            if (q.pairsWith == null) continue;
            int a = Math.min(q.id, q.pairsWith);
            int b = Math.max(q.id, q.pairsWith);
            int key = a * 100 + b;
            if (!seenPairRoot.add(key)) continue;

            Integer choiceA = answers.get(a);
            Integer choiceB = answers.get(b);
            if (choiceA == null || choiceB == null) continue;

            String axisA = QuestionBank.byId(a).options.get(choiceA).axis;
            String axisB = QuestionBank.byId(b).options.get(choiceB).axis;
            if (axisA == null || axisB == null) continue; // a neutral option carries no consistency signal

            evaluated++;
            if (!axisA.equals(axisB)) contradictions++;
        }

        result.contradictions = contradictions;
        result.consistencyPairsEvaluated = evaluated;
        int consistency = 100 - (contradictions * 15);
        result.consistency = Math.max(25, Math.min(100, consistency));

        return result;
    }
}
