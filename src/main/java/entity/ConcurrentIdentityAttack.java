package entity;

import java.util.*;
import java.util.concurrent.*;

/**
 * 并发身份识别攻击模拟：使用目标UE的认证令牌试探所有UE，并判断响应差异
 */
public class ConcurrentIdentityAttack {

    private CoreNetwork coreNetwork;
    private BaseStations baseStation;
    private Attacker attacker;
    private final String targetSUPI = "MCC123_MNC456_MSIN001";

    public ConcurrentIdentityAttack() {
        String sharedKey = "Shared_Key_123"; // 可以作为参考密钥
        this.coreNetwork = new CoreNetwork(sharedKey);
        this.baseStation = new BaseStations(coreNetwork);
        this.attacker = new Attacker(targetSUPI,null);
    }

    public void startAttack() {
        System.out.println("🚀 启动并发身份识别攻击...");

        // 构造目标UE，获取其真实认证令牌
        UEs targetUE = new UEs(targetSUPI, "Key_1");
        String targetSUCI = targetUE.computeSUCI();
        String targetAuth = coreNetwork.processRegistrationRequest(targetSUCI,false);
        String[] tokens = targetAuth.split(",");
        String rand = tokens[0];
        String autn = tokens[1];
        attacker.collectAuthToken(targetSUPI, rand, autn);

        // 构造其它UE（不同密钥）
        List<UEs> ueList = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            String supi = "MCC123_MNC456_MSIN00" + i;
            String key = "Key_" + i; // 每个 UE 不同主密钥
            ueList.add(new UEs(supi, key));
        }

        // 对所有UE并发发起攻击
        ExecutorService executor = Executors.newFixedThreadPool(4);
        List<Future<?>> futures = new ArrayList<>();

        for (UEs ue : ueList) {
            futures.add(executor.submit(() -> processUE(ue)));
        }

        for (Future<?> f : futures) {
            try {
                f.get();
            } catch (Exception ignored) {}
        }

        executor.shutdown();
        System.out.println("✅ 并发身份识别攻击完成");
    }

    private void processUE(UEs ue) {
        try {
            String rand = attacker.getRandForTarget();
            String autn = attacker.getAutnForTarget();
            String response = ue.processAuthResponse(rand, autn);
            attacker.analyzeFailureMessage(response);
        } catch (Exception e) {
            System.out.println("❌ 线程异常: " + e.getMessage());
        }
    }
}
