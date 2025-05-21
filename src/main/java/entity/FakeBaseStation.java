package entity;

/**
 * 伪基站类
 */
public class FakeBaseStation {

    public String impersonateBaseStation(String ueRequest) {
        // 返回伪造的认证失败消息
        return "AuthFailure_FakeBaseStation";
    }
}