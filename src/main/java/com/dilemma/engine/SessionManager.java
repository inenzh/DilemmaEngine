package com.dilemma.engine;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class SessionManager {
    private final ConcurrentHashMap<String, Session> sessions = new ConcurrentHashMap<>();

    public Session create() {
        String id = UUID.randomUUID().toString();
        Session s = new Session(id);
        sessions.put(id, s);
        return s;
    }

    public Session get(String id) {
        return sessions.get(id);
    }
}
