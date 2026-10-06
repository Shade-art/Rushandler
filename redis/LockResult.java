package org.rushandler.rushandler.redis;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LockResult {

    private final boolean acquired;
    private final String token;

    public static LockResult success(String token) {
        return new LockResult(true, token);
    }

    public static LockResult failure() {
        return new LockResult(false, null);
    }
}
