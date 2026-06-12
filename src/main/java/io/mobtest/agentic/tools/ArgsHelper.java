package io.mobtest.agentic.tools;

import java.util.Map;

final class ArgsHelper {

    private ArgsHelper() {}

    static String str(Map<String, Object> args, String key) {
        Object value = args.get(key);
        return value == null ? "" : value.toString();
    }
}
