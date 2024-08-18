package bulls.server.enums;

import bulls.staticData.TempConf;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public enum ServerId {
    AP01("AP1", SelectServerLocation.PUSAN, ServerPurpose.LP, false),
    AP02("AP2", SelectServerLocation.SEOUL, ServerPurpose.LP, true),
    AP03("AP3", SelectServerLocation.SEOUL, ServerPurpose.MAIN_HEDGE, true),
    AP04("AP4", SelectServerLocation.SEOUL, ServerPurpose.SUB_HEDGE, true),
    AP05("AP5", SelectServerLocation.SEOUL, ServerPurpose.LP, true),
    AP25("AP25", SelectServerLocation.PUSAN, ServerPurpose.LP, false),
    AP28("PUSAN_AP1", SelectServerLocation.PUSAN, ServerPurpose.LP, true),
    AP29("PUSAN_AP2", SelectServerLocation.PUSAN, ServerPurpose.LP, true),
    CLEARING("SRV98", SelectServerLocation.SEOUL, ServerPurpose.EXPIRY_CLEARING, true),
    UNKNOWN("", SelectServerLocation.NOTHING, ServerPurpose.UNKNOWN, false);

    public final String aliasServerId;
    public final SelectServerLocation location;
    public final ServerPurpose purpose;
    public final boolean isOAServer;

    private static ServerId currentServerId = null;

    private static final Map<String, ServerId> aliasMap = new HashMap<>();
    private static final Map<SelectServerLocation, Set<ServerId>> locationSetMap = new HashMap<>();
    private static final Map<ServerPurpose, Set<ServerId>> purposeSetMap = new HashMap<>();
    private static final Set<ServerId> oaServerIdSet = new HashSet<>();

    ServerId(String aliasServerId, SelectServerLocation location, ServerPurpose purpose, boolean isOAServer) {
        this.aliasServerId = aliasServerId;
        this.location = location;
        this.purpose = purpose;
        this.isOAServer = isOAServer;
    }

    static {
        for (var id : values()) {
            if (id == UNKNOWN)
                continue;

            aliasMap.put(id.aliasServerId, id);
            locationSetMap.computeIfAbsent(id.location, k -> new HashSet<>()).add(id);
            purposeSetMap.computeIfAbsent(id.purpose, k -> new HashSet<>()).add(id);
            if (id.isOAServer)
                oaServerIdSet.add(id);
        }
    }

    @NotNull
    public static Set<ServerId> getServerIdSetByLocation(SelectServerLocation location) {
        switch (location) {
            case SEOUL, PUSAN -> {
                return locationSetMap.get(location);
            }
            case ALL -> {
                return Set.of(ServerId.values());
            }
        }

        return Set.of();
    }

    @NotNull
    public static Set<ServerId> getServerIdSetByPurpose(ServerPurpose purpose) {
        if (purposeSetMap.containsKey(purpose))
            return purposeSetMap.get(purpose);

        return Set.of();
    }

    @NotNull
    public static Set<ServerId> getOAServerIdSet() {
        return oaServerIdSet;
    }

    @NotNull
    public static ServerId getServerId(String serverIdString) {
        if (serverIdString == null)
            return UNKNOWN;

        if (aliasMap.containsKey(serverIdString))
            return aliasMap.get(serverIdString);

        try {
            return valueOf(serverIdString);
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }

    @NotNull
    public static ServerId getCurrentServerId() {
        if (currentServerId != null)
            return currentServerId;

        currentServerId = getServerId(TempConf.SERVER_ID);
        return currentServerId;
    }
}
