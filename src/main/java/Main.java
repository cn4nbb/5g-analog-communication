import entity.*;

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
        BaseStations baseStation = new BaseStations(coreNetwork,new Attacker("MCC123_MNC456_MSIN001",null));

        // 攻击者设置
        Attacker attacker = new Attacker("MCC123_MNC456_MSIN001", "11");

        // 初始化多个UE
        List<UEs> ueList = new ArrayList<>();
        ueList.add(new UEs("MCC123_MNC456_MSIN001", sharedKey)); // 目标UE
        ueList.add(new UEs("MCC123_MNC456_MSIN002", sharedKey));
        ueList.add(new UEs("MCC123_MNC456_MSIN003", sharedKey));

        // 准备阶段: 收集目标UE的认证令牌
        for (UEs ue : ueList) {
            String suci = ue.computeSUCI();
            String authResponse = coreNetwork.processRegistrationRequest(suci,true);
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
        System.out.println("\n=== 5GMM 欺骗攻击 ===");
        perform5GMMDeceptionAttack(); // 5GMM 欺骗攻击
        System.out.println("\n=== 中间人攻击（MITM） ===");
        performMITMAttack();
        System.out.println("\n=== 并发身份识别攻击 ===");
        ConcurrentIdentityAttack concurrentAttack = new ConcurrentIdentityAttack();
        concurrentAttack.startAttack();
    }

    /**
     * 用户注册流程 (不包含攻击)
     */
    private static void Registration() {
        // 初始化网络组件
        String sharedKey = "Shared_Key_123";  // 统一的预共享密钥
        CoreNetwork coreNetwork = new CoreNetwork(sharedKey);
        BaseStations baseStation = new BaseStations(coreNetwork,new Attacker("MCC123_MNC456_MSIN001",null));
        UEs userEquipment = new UEs("MCC123_MNC456_MSIN789", sharedKey);

        // Step 1: RRC连接建立
        String rrcRequest = "RRC Connection Request from UE";
        System.out.println(rrcRequest);
        String rrcResponse = baseStation.processRRCConnectionRequest(rrcRequest);
        System.out.println(rrcResponse);

        // Step 2: 注册请求
        String registrationRequest = userEquipment.sendRegistrationRequest();
        System.out.println(registrationRequest);
        String authResponse = baseStation.forwardRegistrationRequest(userEquipment.computeSUCI(), false);
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

    /**
     * 5GMM 欺骗攻击（由 BaseStations 内部执行）
     */
    private static void perform5GMMDeceptionAttack() {
        String sharedKey = "Shared_Key_123";
        CoreNetwork coreNetwork = new CoreNetwork(sharedKey);
        BaseStations baseStation = new BaseStations(coreNetwork);

        // 构造多个UE
        List<UEs> ueList = new ArrayList<>();
        ueList.add(new UEs("MCC123_MNC456_MSIN001", sharedKey));
        ueList.add(new UEs("MCC123_MNC456_MSIN002", sharedKey));
        ueList.add(new UEs("MCC123_MNC456_MSIN003", sharedKey));

        // 执行攻击（设置为理由码 7 或 11）
        baseStation.trigger5GMMDeceptionAttack(ueList, "11");
    }

    /**
     * MITM中间人攻击（含安全上下文协商劫持）
     */
    private static void performMITMAttack() {
        System.out.println("=== 中间人攻击（MITM + 安全上下文协商劫持） ===");

        String sharedKey = "Shared_Key_123";
        CoreNetwork realCore = new CoreNetwork(sharedKey);
        MITMAttacker mitm = new MITMAttacker(realCore);
        UEs victimUE = new UEs("MCC123_MNC456_MSIN999", sharedKey);

        // Step 1: 伪基站响应 RRC 请求
        System.out.println("UE 发起 RRC 请求...");
        System.out.println("伪基站响应: RRC Connection Setup Complete");

        // Step 2: 注册请求 → 被MITM拦截并转发
        String suci = victimUE.computeSUCI();
        String authResponse = mitm.interceptAndForwardRegistration(suci);
        String[] authTokens = authResponse.split(",");
        String rand = authTokens[0];
        String autn = authTokens[1];

        // Step 3: MITM拦截并修改认证消息（可选）
        String modifiedAuth = mitm.interceptAndModifyAuth(rand, autn);
        String[] parts = modifiedAuth.split(",");
        String randModified = parts[0];
        String autnModified = parts[1];

        // Step 4: UE处理认证消息
        String ueAuthResult = victimUE.processAuthResponse(randModified, autnModified);
        mitm.observeUEResponse(ueAuthResult);

        // Step 5: 安全上下文协商拦截
        String realNasCommand = "NAS Security Mode Command: IntegrityAlgo=128-5";
        String spoofedCommand = mitm.interceptAndSpoofNASSecurityMode(realNasCommand);

        // Step 6: UE完成NAS协商
        String finalResult = victimUE.completeNASSecurityMode(spoofedCommand);
        System.out.println("最终协商结果: " + finalResult);
    }

    /**
     * 并发身份识别攻击（改进之前对应的攻击）
     */
    public static void performConcurrentIdentityAttack() {
        System.out.println("\n=== 并发身份识别攻击 ===");
        ConcurrentIdentityAttack concurrentAttack = new ConcurrentIdentityAttack();
        concurrentAttack.startAttack();
    }
}
