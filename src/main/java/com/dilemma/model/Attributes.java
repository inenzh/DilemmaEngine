package com.dilemma.model;

import java.util.LinkedHashMap;
import java.util.Map;

public final class Attributes {
    private Attributes() {}

    public static final String MORAL_RIGIDITY = "MR";
    public static final String STRATEGIC_THINKING = "ST";
    public static final String RISK_TOLERANCE = "RT";
    public static final String EMPATHY = "EM";
    public static final String POWER_ORIENTATION = "PO";

    public static final String[] ALL = { MORAL_RIGIDITY, STRATEGIC_THINKING, RISK_TOLERANCE, EMPATHY, POWER_ORIENTATION };

    public static Map<String, String> displayNames() {
        Map<String, String> m = new LinkedHashMap<>();
        m.put(MORAL_RIGIDITY, "Moral Rigidity");
        m.put(STRATEGIC_THINKING, "Strategic Thinking");
        m.put(RISK_TOLERANCE, "Risk Tolerance");
        m.put(EMPATHY, "Empathy");
        m.put(POWER_ORIENTATION, "Power Orientation");
        return m;
    }
}
