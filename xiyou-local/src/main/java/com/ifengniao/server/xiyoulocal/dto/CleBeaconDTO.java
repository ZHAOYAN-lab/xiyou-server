package com.ifengniao.server.xiyoulocal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true)
public class CleBeaconDTO {
    private Long calculatedAt;
    private Long updatedAt;
    private Object gps;
    private String mapId;
    private String zoneId;
    private String zoneName;
    private String lastGateway;
    private String nearestGateway;
    private Double rssi;
    private Double x;
    private Double y;
    private Double z;
    private Map<String, Long> userDataTs;
    private Map<String, Object> userData;

    // ✅ 手机端广播解析出来的唯一标识
    private String uniqueId;

    public Map<String, Long> getUserDataTs() {
        if (userDataTs == null) {
            userDataTs = new HashMap<>();
        }
        return userDataTs;
    }

    public Map<String, Object> getUserData() {
        if (userData == null) {
            userData = new HashMap<>();
        }
        return userData;
    }

    /**
     * ✅ 从 manufacturerData 中解析 UniqueId
     * - 如果 >=6 字节：取前 6 个字节作为 UniqueId
     * - 输出两种形式：
     *   1. 带冒号分隔：43:54:4D:41:FC:41
     *   2. 无分隔小写：43544d41fc41 （推荐用于 beaconMac 存库）
     */
    @SuppressWarnings("unchecked")
    private void ensureUniqueId() {
        if (this.uniqueId != null && !this.uniqueId.isEmpty()) {
            return; // 已有值就不解析
        }
        try {
            if (userData != null && userData.containsKey("manufacturerData")) {
                Object raw = userData.get("manufacturerData");
                byte[] bytes;

                if (raw instanceof byte[]) {
                    bytes = (byte[]) raw;
                } else if (raw instanceof String) {
                    String hex = ((String) raw).replaceAll("[^0-9A-Fa-f]", "");
                    int len = hex.length();
                    bytes = new byte[len / 2];
                    for (int i = 0; i < len; i += 2) {
                        bytes[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                                + Character.digit(hex.charAt(i + 1), 16));
                    }
                } else {
                    return;
                }

                if (bytes.length >= 6) {
                    // 生成无分隔符小写形式
                    StringBuilder sbRaw = new StringBuilder();
                    for (int i = 0; i < 6; i++) {
                        sbRaw.append(String.format("%02x", bytes[i]));
                    }
                    this.uniqueId = sbRaw.toString(); // eg. 43544d41fc41
                }
            }
        } catch (Exception e) {
            this.uniqueId = null;
        }
    }

    public String getUniqueId() {
        ensureUniqueId();
        return uniqueId;
    }
}
