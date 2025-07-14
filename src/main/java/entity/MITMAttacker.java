package entity;

public class MITMAttacker {
    private CoreNetwork realCore;

    public MITMAttacker(CoreNetwork realCore) {
        this.realCore = realCore;
    }

    public String interceptAndForwardRegistration(String suci) {
        System.out.println("MITM攻击者: 拦截注册请求, SUCI=" + suci);
        // 可以进行日志记录或替换 suci
        String modifiedSUCI = suci.replace("Encrypted", "ModifiedEncrypted");
        System.out.println("MITM攻击者: 修改后转发给真实核心网: " + modifiedSUCI);
        return realCore.processRegistrationRequest(modifiedSUCI,true);
    }

    public String interceptAndModifyAuth(String rand, String autn) {
        System.out.println("MITM攻击者: 拦截认证响应, RAND=" + rand + ", AUTN=" + autn);
        // 可模拟篡改或分析行为
        return rand + "," + autn + "_MITM";
    }

    public void observeUEResponse(String response) {
        System.out.println("MITM攻击者: 观察UE响应: " + response);
        if (response.contains("Failure")) {
            System.out.println("攻击者: 可能认证失败，可记录分析");
        }
    }

    public String interceptAndSpoofNASSecurityMode(String nasCommand) {
        System.out.println("MITM攻击者: 拦截 NAS Security Mode Command");
        String spoofedCommand = nasCommand.replace("IntegrityAlgo=128-5", "IntegrityAlgo=000-0");
        System.out.println("MITM攻击者: 注入弱加密算法（IntegrityAlgo=000-0）");
        return spoofedCommand;
    }

}
