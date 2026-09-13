package com.dilemma.engine;

import com.dilemma.model.Question;
import com.dilemma.model.Question.Tier;

import java.util.*;

/**
 * Determines presentation order across the fixed 30-question bank.
 *
 * The first 8 (BROAD tier) establish general tendencies and are shown in a
 * fixed order. From question 9 onward, the next question is chosen from the
 * appropriate tier's remaining pool, prioritizing whichever attributes have
 * been least informed by answers so far. All 30 questions are always shown;
 * only the order adapts.
 */
final class AdaptiveSelector {

    private AdaptiveSelector() {}

    static Question pickNext(Session session, List<Question> pool) {
        int answered = session.answeredCount();

        Tier targetTier;
        if (answered < 8) {
            targetTier = Tier.BROAD;
        } else if (answered < 20) {
            targetTier = Tier.MID;
        } else {
            targetTier = Tier.HARD;
        }

        List<Question> candidates = new ArrayList<>();
        for (Question q : pool) {
            if (!session.askedIds.contains(q.id) && q.tier == targetTier) {
                candidates.add(q);
            }
        }
        // Fallback in the unlikely event a tier pool runs dry.
        if (candidates.isEmpty()) {
            for (Question q : pool) {
                if (!session.askedIds.contains(q.id)) candidates.add(q);
            }
        }

        if (targetTier == Tier.BROAD) {
            candidates.sort(Comparator.comparingInt(q -> q.id));
            return candidates.get(0);
        }

        Map<String, Integer> touchCounts = new HashMap<>();
        for (Integer askedId : session.askedIds) {
            Question asked = QuestionBank.byId(askedId);
            for (String attr : asked.attributesTouched) {
                touchCounts.merge(attr, 1, Integer::sum);
            }
        }

        Question best = null;
        double bestScore = Double.NEGATIVE_INFINITY;
        for (Question c : candidates) {
            double score = 0;
            for (String attr : c.attributesTouched) {
                int touched = touchCounts.getOrDefault(attr, 0);
                score += 1.0 / (1 + touched);
            }
            // Small deterministic tie-breaker so ordering isn't strictly ascending-id.
            score += (Objects.hash(session.id, c.id) % 100) / 100000.0;
            if (score > bestScore) {
                bestScore = score;
                best = c;
            }
        }
        return best;
    }
}
