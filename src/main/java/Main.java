import entity.BaseStations;
import entity.CoreNetwork;
import entity.FakeBaseStation;
import entity.UEs;

public class Main {

    public static void main(String[] args) {
        CoreNetwork coreNetwork = new CoreNetwork();
        BaseStations gNB = new BaseStations(coreNetwork);
        FakeBaseStation fbs = new FakeBaseStation();
        UEs ue = new UEs();

        // 1. 正常接入流程
        String request = ue.generateAuthRequest();
        System.out.println("UE Request: " + request);
        String response = gNB.processAuthRequest(request);
        System.out.println("Core Network Response: " + response);

        // 2. 攻击模拟
        String fakeResponse = fbs.impersonateBaseStation(request);
        System.out.println("Fake Base Station Response: " + fakeResponse);

        // 验证认证结果
        boolean authResult = ue.receiveAuthResponse(fakeResponse);
        System.out.println("Authentication Result: " + authResult);
    }
}
