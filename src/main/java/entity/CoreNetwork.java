package entity;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * 核心网类
 */
public class CoreNetwork {
    private String k;  // 核心网主密钥

    public CoreNetwork(String k) {
        this.k = k;
    }

//    /**
//     * 处理认证请求
//     * @param suci
//     * @return
//     */
//    public String processRegistrationRequest(String suci) {
//        System.out.println("核心网:处理认证请求，SUCI=" + suci);
//        //返回认证响应
//        return generateAuthResponse();
//    }

    /**
     * 生成RAND（随机数）和AUTN(认证令牌)，用于认证响应
      * @return
     */
    private String generateAuthResponse() {
        String rand = "RAND_" + Math.random();
        String autn = "AUTN_" + hash(k + rand);
        System.out.println("核心网:生成的RAND=" + rand + ", AUTN=" + autn);
        return rand + "," + autn;
    }

    /**
     * 调用加密算法生成AUTN（认证令牌）
     * @param input
     * @return
     */
    private String hash(String input) {
        try {
            // 使用消息摘要（SHA-256）算法
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(input.getBytes());
            return Base64.getEncoder().encodeToString(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 建立NAS安全模式
     * @param response
     * @return
     */
    public String establishNASSecurityMode(String response) {
        if (response.contains("NAS Security Mode Complete")) {
            System.out.println("核心网:NAS安全模式建立完成。");
            return "Initial Context Request";
        }
        return "AuthFailure";
    }

    public String processRegistrationRequest(String suci) {
        Attacker attacker = new Attacker();
        System.out.println("核心网: 处理认证请求，SUCI=" + suci);
        // 判断是否是伪造数据
        if (suci.contains("Fake")) {
            System.out.println("核心网: 检测到伪造的SUCI!");
        }
        // 返回正常或伪造的认证响应
        return attacker.fakeAuthResponse();
    }

}

