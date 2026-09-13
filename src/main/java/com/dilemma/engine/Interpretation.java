package com.dilemma.engine;

import com.dilemma.model.Attributes;

import java.util.*;

public final class Interpretation {

    private Interpretation() {}

    private static final Map<String, String[]> BLURBS = new HashMap<>();
    static {
        BLURBS.put(Attributes.MORAL_RIGIDITY, new String[]{
            "You bend principles readily when the situation makes a strict line impractical, favoring context over consistency.",
            "You hold your principles firmly but not absolutely — you'll defend a line, then reconsider it when the cost gets personal.",
            "You hold to your principles even when it is expensive to do so, and you're wary of rules that flex too easily."
        });
        BLURBS.put(Attributes.STRATEGIC_THINKING, new String[]{
            "You tend to resolve dilemmas on their immediate terms rather than mapping out second-order consequences.",
            "You weigh near-term and long-term effects fairly evenly, adjusting your horizon to the stakes in front of you.",
            "You default to tracing consequences several steps out, and you're drawn to solutions that manage trade-offs rather than pick a side outright."
        });
        BLURBS.put(Attributes.RISK_TOLERANCE, new String[]{
            "You gravitate toward the safer, more reversible option even when a bolder move might pay off more.",
            "You take on real risk when the case for it is solid, but you look for ways to hedge it first.",
            "You're comfortable accepting uncertainty and potential loss in exchange for a shot at a better outcome."
        });
        BLURBS.put(Attributes.EMPATHY, new String[]{
            "Other people's circumstances inform your decisions, but they rarely override your read of what's fair or effective.",
            "You factor in how a decision lands on the people affected by it, balancing that against other priorities.",
            "How a decision affects the people involved weighs heavily on you, often more than abstract rules or efficient outcomes."
        });
        BLURBS.put(Attributes.POWER_ORIENTATION, new String[]{
            "You're more comfortable stepping back from authority than seizing it, preferring shared or delegated decisions.",
            "You'll take charge when it's clearly your responsibility, but you don't reach for control by default.",
            "You're comfortable taking decisive, consequential control of a situation and living with the responsibility that comes with it."
        });
    }

    private static String tierBlurb(String attr, int score) {
        String[] tiers = BLURBS.get(attr);
        if (score < 35) return tiers[0];
        if (score <= 65) return tiers[1];
        return tiers[2];
    }

    public static Map<String, String> attributeBlurbs(Map<String, Integer> scores) {
        Map<String, String> out = new LinkedHashMap<>();
        for (String attr : Attributes.ALL) {
            out.put(attr, tierBlurb(attr, scores.get(attr)));
        }
        return out;
    }

    public static String archetype(Map<String, Integer> scores) {
        List<String> ranked = new ArrayList<>(Arrays.asList(Attributes.ALL));
        ranked.sort((a, b) -> scores.get(b) - scores.get(a));
        String top1 = ranked.get(0);
        String top2 = ranked.get(1);

        String key = top1 + "+" + top2;
        Map<String, String> named = new HashMap<>();
        named.put(Attributes.MORAL_RIGIDITY + "+" + Attributes.STRATEGIC_THINKING, "The Principled Strategist");
        named.put(Attributes.STRATEGIC_THINKING + "+" + Attributes.MORAL_RIGIDITY, "The Principled Strategist");
        named.put(Attributes.MORAL_RIGIDITY + "+" + Attributes.EMPATHY, "The Steady Advocate");
        named.put(Attributes.EMPATHY + "+" + Attributes.MORAL_RIGIDITY, "The Steady Advocate");
        named.put(Attributes.RISK_TOLERANCE + "+" + Attributes.POWER_ORIENTATION, "The Decisive Operator");
        named.put(Attributes.POWER_ORIENTATION + "+" + Attributes.RISK_TOLERANCE, "The Decisive Operator");
        named.put(Attributes.STRATEGIC_THINKING + "+" + Attributes.POWER_ORIENTATION, "The Systems Commander");
        named.put(Attributes.POWER_ORIENTATION + "+" + Attributes.STRATEGIC_THINKING, "The Systems Commander");
        named.put(Attributes.EMPATHY + "+" + Attributes.RISK_TOLERANCE, "The Bold Caretaker");
        named.put(Attributes.RISK_TOLERANCE + "+" + Attributes.EMPATHY, "The Bold Caretaker");
        named.put(Attributes.EMPATHY + "+" + Attributes.STRATEGIC_THINKING, "The Thoughtful Mediator");
        named.put(Attributes.STRATEGIC_THINKING + "+" + Attributes.EMPATHY, "The Thoughtful Mediator");
        named.put(Attributes.MORAL_RIGIDITY + "+" + Attributes.POWER_ORIENTATION, "The Uncompromising Authority");
        named.put(Attributes.POWER_ORIENTATION + "+" + Attributes.MORAL_RIGIDITY, "The Uncompromising Authority");
        named.put(Attributes.MORAL_RIGIDITY + "+" + Attributes.RISK_TOLERANCE, "The Principled Risk-Taker");
        named.put(Attributes.RISK_TOLERANCE + "+" + Attributes.MORAL_RIGIDITY, "The Principled Risk-Taker");
        named.put(Attributes.EMPATHY + "+" + Attributes.POWER_ORIENTATION, "The Protective Leader");
        named.put(Attributes.POWER_ORIENTATION + "+" + Attributes.EMPATHY, "The Protective Leader");
        named.put(Attributes.RISK_TOLERANCE + "+" + Attributes.STRATEGIC_THINKING, "The Calculated Gambler");
        named.put(Attributes.STRATEGIC_THINKING + "+" + Attributes.RISK_TOLERANCE, "The Calculated Gambler");

        return named.getOrDefault(key, "The Adaptive Decision-Maker");
    }

    public static String consistencyNote(int consistency, int evaluated) {
        if (evaluated == 0) {
            return "Not enough directly comparable dilemmas landed on tagged choices to measure consistency with confidence; treat the profile above as a first approximation.";
        }
        if (consistency >= 85) {
            return "Your choices held to the same underlying values across very different situations. This profile is a high-confidence read of how you actually decide, not just how you'd like to think you decide.";
        }
        if (consistency >= 60) {
            return "Your choices mostly held together across similar dilemmas, with a little give when the framing or stakes shifted. Treat this profile as reliable but not absolute.";
        }
        return "Your results contain significant situational variation: on more than one occasion, a scenario that tested the same underlying value pulled a different answer out of you depending on how it was framed. That's common and not a flaw, but it means these scores describe a range of behavior more than a fixed rule.";
    }
}
