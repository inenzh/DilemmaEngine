package com.dilemma.engine;

import com.dilemma.model.Question;

import java.util.*;

public final class Session {
    public final String id;
    public final List<Integer> order = new ArrayList<>();      // question ids, in the order presented (filled lazily)
    public final Map<Integer, Integer> answers = new LinkedHashMap<>(); // questionId -> chosen option index (0-3)
    public final Set<Integer> askedIds = new LinkedHashSet<>();

    public Session(String id) {
        this.id = id;
    }

    public int answeredCount() {
        return answers.size();
    }

    public boolean isComplete() {
        return answeredCount() >= 30;
    }

    public Question currentQuestion(List<Question> pool) {
        if (order.size() <= answeredCount()) {
            Question next = AdaptiveSelector.pickNext(this, pool);
            order.add(next.id);
            askedIds.add(next.id);
            return next;
        }
        return QuestionBank.byId(order.get(answeredCount()));
    }

    public void recordAnswer(int questionId, int choiceIndex) {
        answers.put(questionId, choiceIndex);
    }
}
