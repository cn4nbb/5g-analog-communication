import entity.Attacker;
import entity.BaseStations;
import entity.CoreNetwork;
import entity.UEs;

import java.util.ArrayList;
import java.util.List;

public class Main {

    /**
     * 用户注册流程（包含攻击模拟）
     */
    private static void RegistrationWithAttack() {
        // 初始化网络组件
        String sharedKey = "Shared_Key_123";
        CoreNetwork coreNetwork = new CoreNetwork(sharedKey);
        BaseStations baseStation = new BaseStations(coreNetwork);

        // 攻击者设置
        Attacker attacker = new Attacker("MCC123_MNC456_MSIN001");

        // 初始化多个UE
        List<UEs> ueList = new ArrayList<>();
        ueList.add(new UEs("MCC123_MNC456_MSIN001", sharedKey)); // 目标UE
        ueList.add(new UEs("MCC123_MNC456_MSIN002", sharedKey));
        ueList.add(new UEs("MCC123_MNC456_MSIN003", sharedKey));

        // 准备阶段: 收集目标UE的认证令牌
        for (UEs ue : ueList) {
            String suci = ue.computeSUCI();
            String authResponse = coreNetwork.processRegistrationRequest(suci);
            String[] tokens = authResponse.split(",");
            String rand = tokens[0];
            String autn = tokens[1];
            if (ue.getSUPI().equals("MCC123_MNC456_MSIN001")) {
                attacker.collectAuthToken(ue.getSUPI(), rand, autn);
            }
        }

        // 攻击阶段: 模拟FBS，劫持认证流程
        for (UEs ue : ueList) {
            System.out.println("\n=== 处理 UE: " + ue.getSUPI() + " ===");
            String suci = ue.computeSUCI();
            String authToken = attacker.returnAuthToken(ue.getSUPI());
            String[] tokens = authToken.split(",");
            String rand = tokens[0];
            String autn = tokens[1];
            String response = ue.processAuthResponse(rand, autn);
            System.out.println("认证结果: " + response);
            if (response.contains("Failure")) {
                attacker.analyzeFailureMessage(response);
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("=== 正常注册流程 ===");
        Registration(); // 正常用户注册流程
        System.out.println("\n=== 攻击模拟注册流程 ===");
        RegistrationWithAttack(); // 含攻击模拟的注册流程
    }

    /**
     * 用户注册流程 (不包含攻击)
     */
    private static void Registration() {
        // 初始化网络组件
        String sharedKey = "Shared_Key_123";  // 统一的预共享密钥
        CoreNetwork coreNetwork = new CoreNetwork(sharedKey);
        BaseStations baseStation = new BaseStations(coreNetwork);
        UEs userEquipment = new UEs("MCC123_MNC456_MSIN789", sharedKey);

        // Step 1: RRC连接建立
        String rrcRequest = "RRC Connection Request from UE";
        System.out.println(rrcRequest);
        String rrcResponse = baseStation.processRRCConnectionRequest(rrcRequest);
        System.out.println(rrcResponse);

        // Step 2: 注册请求
        String registrationRequest = userEquipment.sendRegistrationRequest();
        System.out.println(registrationRequest);
        String authResponse = baseStation.forwardRegistrationRequest(userEquipment.computeSUCI());
        System.out.println("Auth Response: " + authResponse);

        // Step 3: 处理认证响应
        String[] authFields = authResponse.split(",");
        String rand = authFields[0];
        String autn = authFields[1];
        String nasCommand = userEquipment.processAuthResponse(rand, autn);
        System.out.println("NAS Command: " + nasCommand);

        // Step 4: 调用 BaseStations 处理 NAS 安全模式建立
        String nasResponse = baseStation.processNASSecurityMode(nasCommand);
        System.out.println("NAS Security Mode Response from BaseStation: " + nasResponse);

        // Step 5: NAS安全模式建立完成
        String finalResponse = userEquipment.completeNASSecurityMode(nasResponse);
        System.out.println("Final NAS Security Mode Response: " + finalResponse);
    }
}
