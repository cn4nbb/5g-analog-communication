package entity;

/**
 * 核心网类
 */
public class CoreNetwork {
    private static final String SECRET_KEY = "5GCoreSecret";

    public String authenticate(String request) {
        // 简单认证模拟
        if (request.contains("SUCI")) {
            return "AuthSuccess";
        }
        return "AuthFailure";
    }
}

