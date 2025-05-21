package entity;

/**
 * 基站类
 */
public class BaseStations {
    private CoreNetwork coreNetwork;

    public BaseStations(CoreNetwork coreNetwork) {
        this.coreNetwork = coreNetwork;
    }

    // 接收并处理UE的接入请求
    public String processAuthRequest(String request) {
        // 转发到核心网处理
        return coreNetwork.authenticate(request);
    }
}
