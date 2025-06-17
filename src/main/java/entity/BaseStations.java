package entity;
import java.util.*;

public class BaseStations {

    private CoreNetwork coreNetwork;
    private Attacker attacker;

    public BaseStations(CoreNetwork coreNetwork) {
        this.coreNetwork = coreNetwork;
    }

    /**
     * 处理RRC连接请求
     * @param request
     * @return
     */
    public String processRRCConnectionRequest(String request) {
        System.out.println("gNB: 处理请求:" + request);
        return "RRC Connection Setup Complete";
    }

//    /**
//     * 前向传播注册请求给核心网
//     * @param suci
//     * @return
//     */
//    public String forwardRegistrationRequest(String suci) {
//        return coreNetwork.processRegistrationRequest(suci);
//    }

//    /**
//     * 处理NAS安全模式
//     * @param response
//     * @return
//     */
//    public String processNASSecurityMode(String response) {
//        if (response.contains("NAS Security Mode Complete")) {
//            System.out.println("gNB: NAS安全模式建立完成。");
//            return "Initial Context Request";
//        }
//        return "NASSecurityModeFailure";
//    }



    public String forwardRegistrationRequest(String suci) {
        attacker = new Attacker();
        // 攻击者拦截SUCI
        String interceptedSUCI = attacker.interceptMessage(suci);
        return coreNetwork.processRegistrationRequest(interceptedSUCI);
    }


    public String processNASSecurityMode(String response) {
        attacker = new Attacker();
        if (response.contains("NAS Security Mode Complete")) {
            System.out.println("gNB: NAS安全模式建立完成。");
            return "Initial Context Request";
        } else {
            // 如果是认证失败消息，调用攻击者分析
            attacker.analyzeFailureMessage(response);
            return "NASSecurityModeFailure";
        }
    }


}
