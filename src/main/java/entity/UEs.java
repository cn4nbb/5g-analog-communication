package entity;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class UEs {
    private String supi;  // 用户永久标识符 (MCC + MNC + MSIN)
    private String suci;  // 用户加密标识符
    private String k;     // 主密钥
    private String rand;  // 随机数
    private String autn;  // 认证令牌
    private boolean isRegistered;

    public UEs(String supi, String k) {
        this.supi = supi;
        this.k = k;
        this.isRegistered = false;
    }

    /**
     * 计算SUCI (模拟加密)
     * @return 加密后的标识符
     */
    public String computeSUCI() {
        String publicKey = "ECIES_Public_Key";  // 模拟基站的公钥
        this.suci = "Encrypted(" + supi + ")_With_" + publicKey;
        return suci;
    }

    /**
     * 发送注册请求 (携带SUCI)
     * @return 注册请求内容
     */
    public String sendRegistrationRequest() {
        computeSUCI();
        return "Registration Request: SUCI=" + suci;
    }

    /**
     * 处理认证响应
     * @param rand 随机数
     * @param autn 认证令牌
     * @return NAS命令或认证失败原因
     */
    public String processAuthResponse(String rand, String autn) {
        this.rand = rand;
        this.autn = autn;
        if (!rand.startsWith("Fake") && verifyAUTN(rand, autn)) {
            System.out.println("UE: 认证成功。");
            return "NAS Security Mode Command";
        } else {
            String failureReason = rand.startsWith("Fake") ? "MAC_Failure" : "Sync_Failure,AUTS";
            System.out.println("UE: 认证失败，原因: " + failureReason);
            return failureReason;
        }
    }

    /**
     * 验证认证令牌 (AUTN)
     * @param rand 随机数
     * @param autn 认证令牌
     * @return 验证结果
     */
    private boolean verifyAUTN(String rand, String autn) {
        String expectedAutn = generateAUTN(rand);
        return expectedAutn.equals(autn);
    }

    /**
     * 生成认证令牌 (AUTN)
     * @param rand 随机数
     * @return 生成的认证令牌
     */
    private String generateAUTN(String rand) {
        return "AUTN_" + hash(k + rand);
    }

    /**
     * 完成NAS安全模式建立
     * @param command NAS命令
     * @return 完成状态或失败原因
     */
    public String completeNASSecurityMode(String command) {
        if (command.contains("NAS Security Mode Command")) {
            System.out.println("UE: NAS安全模式建立完成。");
            this.isRegistered = true;
            return "NAS Security Mode Complete";
        }
        return "NAS Security Mode Failure";
    }

    /**
     * 接收上下文响应
     * @param response 核心网的上下文响应
     */
    public void receiveContextResponse(String response) {
        if (response.contains("Initial Context Response")) {
            System.out.println("UE: 注册处理完成。");
            this.isRegistered = true;
        }
    }

    /**
     * Hash算法 (用于认证令牌生成)
     * @param input 输入数据
     * @return 哈希值
     */
    private String hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(input.getBytes());
            return Base64.getEncoder().encodeToString(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取SUPI
     * @return SUPI
     */
    public String getSUPI() {
        return supi;
    }

    /**
     * 是否已注册
     * @return 注册状态
     */
    public boolean isRegistered() {
        return isRegistered;
    }
}
