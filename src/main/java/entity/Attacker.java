package entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Attacker {
    private Map<String, String> collectedAuthTokens = new HashMap<>();
    private String targetUE;
    private List<String> observedPLMNs = new ArrayList<>();
    private String selectedPLMN;
    private String reasonCode;

    public Attacker(String targetUE,String reasonCode) {
        this.targetUE = targetUE;
        this.reasonCode = reasonCode;
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
            return fakeAuthResponse();
        }
    }

    /**
     * 监听认证失败消息并分析目标UE是否在攻击范围内
     * @param failureMessage
     */
    public void analyzeFailureMessage(String failureMessage) {
        if (!failureMessage.contains("|")) {
            System.out.println("攻击者: 未识别的认证失败格式");
            return;
        }

        String[] parts = failureMessage.split("\\|");
        String reason = parts[0];
        String supi = parts[1];

        System.out.println("攻击者: 认证失败响应 - 原因: " + reason + ", 来自: " + supi);

        if ("Sync_Failure".equals(reason) && supi.equals(targetUE)) {
            System.out.println("🎯 识别成功：目标UE [" + supi + "] 出现在覆盖范围内！");
        } else if ("Sync_Failure".equals(reason)) {
            System.out.println("攻击者: 非目标UE [" + supi + "] 同步失败，忽略");
        } else if ("MAC_Failure".equals(reason)) {
            System.out.println("攻击者: [" + supi + "] MAC校验失败，不是目标UE");
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

    /**
     * 模拟伪造的注册拒绝消息
     * @param reason 5GMM拒绝原因
     * @return
     */
    public String fakeRegisterRejectMessage(String reason) {
        // 根据攻击需要返回不同的拒绝消息
        return "Register Reject: 5GMM Reason=" + reason;
    }

    /**
     * 模拟监听并收集合法PLMN
     */
    public void scanAndSelectPLMN() {
        observedPLMNs.add("46000"); // 中国移动
        observedPLMNs.add("46001"); // 中国联通
        observedPLMNs.add("46011"); // 中国电信

        selectedPLMN = observedPLMNs.get(0); // 选择一个作为目标
        System.out.println("攻击者: 监听到合法PLMN列表: " + observedPLMNs);
        System.out.println("攻击者: 选择用于欺骗的PLMN: " + selectedPLMN);
    }

    /**
     * 返回模拟注册拒绝消息
     */
    public String fake5GMMRejectMessage() {
        return "Register Reject: 5GMM Reason=" + reasonCode + " from PLMN=" + selectedPLMN;
    }

    public String getSelectedPLMN() {
        return selectedPLMN;
    }

    public String getReasonCode() {
        return reasonCode;
    }

    public String getRandForTarget() {
        return collectedAuthTokens.get(targetUE).split(",")[0];
    }

    public String getAutnForTarget() {
        return collectedAuthTokens.get(targetUE).split(",")[1];
    }
}
