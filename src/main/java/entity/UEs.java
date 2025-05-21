package entity;

/**
 * UE类
 */
public class UEs {
    private String suci; // 用户唯一标识符
    private String authToken; // 认证令牌

    // 生成认证请求
    public String generateAuthRequest() {
        // 模拟生成随机的 SUCI 和认证令牌
        this.suci = generateSUCI();
        this.authToken = generateAuthToken();
        return String.format("AuthRequest: SUCI=%s, Token=%s", suci, authToken);
    }

    // 接收认证响应
    public boolean receiveAuthResponse(String response) {
        // 简单校验响应消息
        return response.contains("AuthSuccess");
    }

    private String generateSUCI() {
        // 模拟加密的 SUCI 生成
        return "SUCI_" + Math.random();
    }

    private String generateAuthToken() {
        // 生成随机的认证令牌
        return "Token_" + Math.random();
    }
}