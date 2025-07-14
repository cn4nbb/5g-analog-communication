package entity;
import java.util.*;

public class BaseStations {

    private CoreNetwork coreNetwork;
    private Attacker attacker;

    public BaseStations(CoreNetwork coreNetwork) {
        this.coreNetwork = coreNetwork;
        this.attacker = null; // 攻击目标UE
    }

    public BaseStations(CoreNetwork coreNetwork,Attacker attacker) {
        this.coreNetwork = coreNetwork;
        this.attacker = attacker; // 攻击目标UE
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



    /**
     * 处理注册请求，并执行相应的攻击
     */
    public String forwardRegistrationRequest(String suci, boolean is5GMMAttack) {
        // 攻击者拦截SUCI并返回伪造认证令牌
        String interceptedSUCI = attacker.interceptMessage(suci);
        return is5GMMAttack ? coreNetwork.process5GMMDeceptionRequest(interceptedSUCI) : coreNetwork.processRegistrationRequest(interceptedSUCI,true);
    }


    public String processNASSecurityMode(String response) {
        if (response.contains("NAS Security Mode Complete")) {
            System.out.println("gNB: NAS安全模式建立完成。");
            return "Initial Context Request";
        } else {
            // 如果是认证失败消息，调用攻击者分析
            attacker.analyzeFailureMessage(response);
            return "NASSecurityModeFailure";
        }
    }

    /**
     * === 新增方法：完整模拟 5GMM 欺骗攻击 ===
     */
    public void trigger5GMMDeceptionAttack(List<UEs> ueList, String reasonCode) {
        // 初始化攻击者
        this.attacker = new Attacker(null,reasonCode);
        attacker.scanAndSelectPLMN();

        // 对所有UE执行欺骗流程
        for (UEs ue : ueList) {
            System.out.println("\n=== 伪基站吸引 UE: " + ue.getSUPI() + " ===");

            String rrc = processRRCConnectionRequest("RRC Connection Request");
            System.out.println("RRC Response: " + rrc);

            String suci = ue.computeSUCI();
            System.out.println("UE发起注册区域更新请求: " + suci);

            String rejectMessage = attacker.fake5GMMRejectMessage();
            System.out.println("伪基站返回5GMM拒绝消息: " + rejectMessage);

            ue.process5GMMRejectMessage(rejectMessage);
        }
    }
}
