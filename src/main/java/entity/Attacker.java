package entity;

import java.util.HashMap;
import java.util.Map;

public class Attacker {
    private Map<String, String> collectedAuthTokens = new HashMap<>();
    private String targetUE;

    public Attacker(String targetUE) {
        this.targetUE = targetUE;
    }

    public Attacker(){
        this.targetUE = null;
    }
    /**
     * 收集目标UE的认证令牌
     * @param rand
     * @param autn
     */
    public void collectAuthToken(String supi, String rand, String autn) {
        System.out.println("攻击者: 收集认证令牌 - RAND: " + rand + ", AUTN: " + autn + " for SUPI: " + supi);
        collectedAuthTokens.put(supi, rand + "," + autn);
    }

    /**
     * 模拟返回目标UE的认证令牌
     * @return
     */
    public String returnAuthToken(String supi) {
        if (collectedAuthTokens.containsKey(supi)) {
            System.out.println("攻击者: 返回目标UE认证令牌 - SUPI: " + supi);
            return collectedAuthTokens.get(supi);
        } else {
            System.out.println("攻击者: 未收集到目标UE的认证令牌！");
            return "Fake_RAND,Fake_AUTN";
        }
    }

    /**
     * 监听认证失败消息并分析目标UE是否在攻击范围内
     * @param failureMessage
     */
    public void analyzeFailureMessage(String failureMessage) {
        System.out.println("攻击者: 接收到认证失败消息 - " + failureMessage);
        if (failureMessage.contains("MAC_Failure")) {
            System.out.println("攻击者: 目标UE不在覆盖范围内。");
        } else if (failureMessage.contains("Sync_Failure")) {
            System.out.println("攻击者: 目标UE在覆盖范围内，成功识别目标设备！");
        }
    }

    /**
     * 拦截并打印消息内容
     * @param message 消息内容
     * @return 修改后的消息内容
     */
    public String interceptMessage(String message) {
        System.out.println("攻击者: 拦截消息 - " + message);
        // 示例：篡改SUCI或认证令牌
        if (message.contains("SUCI")) {
            String alteredSUCI = "Fake_SUCI_Encrypted_Data";
            System.out.println("攻击者: 修改SUCI为 - " + alteredSUCI);
            return "Registration Request: SUCI=" + alteredSUCI;
        } else if (message.contains("RAND")) {
            String alteredRAND = "Fake_RAND_Value";
            String alteredAUTN = "Fake_AUTN_Value";
            System.out.println("攻击者: 修改RAND和AUTN为 - " + alteredRAND + ", " + alteredAUTN);
            return alteredRAND + "," + alteredAUTN;
        }
        return message;
    }

    /**
     * 模拟返回伪造的认证响应
     * @return 伪造的认证响应
     */
    public String fakeAuthResponse() {
        String fakeRand = "Fake_RAND_Value";
        String fakeAutn = "Fake_AUTN_Value";
        System.out.println("攻击者: 发送伪造认证响应 - RAND: " + fakeRand + ", AUTN: " + fakeAutn);
        return fakeRand + "," + fakeAutn;
    }
}
