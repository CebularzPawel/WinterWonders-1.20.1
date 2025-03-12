package net.cebularz.winterwonders.client.data;

import java.util.HashMap;
import java.util.Map;

public class FrozenStatusCache {
    private static final Map<Integer, Boolean> frozenStatuses = new HashMap<>();

    public static void setStatus(int entityId, boolean frozen) {
        frozenStatuses.put(entityId, frozen);
    }

    public static boolean isFrozen(int entityId) {
        return frozenStatuses.getOrDefault(entityId, false);
    }
}

