package com.dilemma.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Tiny hand-rolled JSON object builder — sufficient for this app's fixed response shapes. */
public final class Json {

    private final StringBuilder sb = new StringBuilder();
    private boolean first = true;

    public static Json obj() {
        Json j = new Json();
        j.sb.append('{');
        return j;
    }

    private void comma() {
        if (!first) sb.append(',');
        first = false;
    }

    public Json put(String key, String value) {
        comma();
        sb.append(quote(key)).append(':').append(value == null ? "null" : quote(value));
        return this;
    }

    public Json put(String key, int value) {
        comma();
        sb.append(quote(key)).append(':').append(value);
        return this;
    }

    public Json put(String key, boolean value) {
        comma();
        sb.append(quote(key)).append(':').append(value);
        return this;
    }

    public Json putRaw(String key, String rawJson) {
        comma();
        sb.append(quote(key)).append(':').append(rawJson);
        return this;
    }

    public static String arrayOfStrings(List<String> items) {
        StringBuilder a = new StringBuilder("[");
        for (int i = 0; i < items.size(); i++) {
            if (i > 0) a.append(',');
            a.append(quote(items.get(i)));
        }
        return a.append(']').toString();
    }

    public static String objectOfInts(Map<String, Integer> map) {
        StringBuilder o = new StringBuilder("{");
        boolean f = true;
        for (Map.Entry<String, Integer> e : map.entrySet()) {
            if (!f) o.append(',');
            f = false;
            o.append(quote(e.getKey())).append(':').append(e.getValue());
        }
        return o.append('}').toString();
    }

    public static String objectOfStrings(Map<String, String> map) {
        StringBuilder o = new StringBuilder("{");
        boolean f = true;
        for (Map.Entry<String, String> e : map.entrySet()) {
            if (!f) o.append(',');
            f = false;
            o.append(quote(e.getKey())).append(':').append(quote(e.getValue()));
        }
        return o.append('}').toString();
    }

    public String build() {
        sb.append('}');
        return sb.toString();
    }

    public static String quote(String s) {
        StringBuilder out = new StringBuilder("\"");
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"': out.append("\\\""); break;
                case '\\': out.append("\\\\"); break;
                case '\n': out.append("\\n"); break;
                case '\r': out.append("\\r"); break;
                case '\t': out.append("\\t"); break;
                default:
                    if (c < 0x20) out.append(String.format("\\u%04x", (int) c));
                    else out.append(c);
            }
        }
        return out.append('"').toString();
    }

    // ---- request body parsing (application/x-www-form-urlencoded) ----

    public static Map<String, String> parseForm(InputStream body) throws IOException {
        String raw = readAll(body);
        Map<String, String> out = new LinkedHashMap<>();
        if (raw.isEmpty()) return out;
        for (String pair : raw.split("&")) {
            int eq = pair.indexOf('=');
            if (eq < 0) continue;
            String k = URLDecoder.decode(pair.substring(0, eq), StandardCharsets.UTF_8);
            String v = URLDecoder.decode(pair.substring(eq + 1), StandardCharsets.UTF_8);
            out.put(k, v);
        }
        return out;
    }

    public static String readAll(InputStream in) throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        byte[] tmp = new byte[4096];
        int n;
        while ((n = in.read(tmp)) != -1) buf.write(tmp, 0, n);
        return buf.toString(StandardCharsets.UTF_8);
    }
}
