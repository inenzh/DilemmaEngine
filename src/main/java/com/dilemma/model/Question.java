package com.dilemma.model;

import java.util.List;
import java.util.Map;

/**
 * A single dilemma: a scenario with exactly four options. Each option carries
 * hidden deltas across the five attributes, and optionally an "axis" label
 * used only for cross-question consistency detection (never shown to the user).
 */
public final class Question {

    public enum Tier { BROAD, MID, HARD }

    public final int id;
    public final Tier tier;
    public final String text;
    public final List<Option> options; // exactly 4, in fixed A/B/C/D order
    public final List<String> attributesTouched; // primary attributes this question informs
    public final Integer pairsWith; // id of an earlier question for consistency checking, or null

    public Question(int id, Tier tier, String text, List<Option> options,
                     List<String> attributesTouched, Integer pairsWith) {
        if (options.size() != 4) {
            throw new IllegalArgumentException("Question " + id + " must have exactly 4 options");
        }
        this.id = id;
        this.tier = tier;
        this.text = text;
        this.options = options;
        this.attributesTouched = attributesTouched;
        this.pairsWith = pairsWith;
    }

    public static final class Option {
        public final String text;
        public final Map<String, Integer> deltas; // attribute key -> delta
        public final String axis; // "A" or "B" — only meaningful for paired questions; else null

        public Option(String text, Map<String, Integer> deltas, String axis) {
            this.text = text;
            this.deltas = deltas;
            this.axis = axis;
        }
    }
}
