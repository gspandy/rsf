package test;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.rsf.RsfClient;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.RsfModule;
import net.hasor.rsf.RsfResult;
import test.services.EchoService;
import test.services.MessageService;
/**
 *
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class CenterCustomerClient {
    public static void main(String[] args) throws Throwable {
        //Client
        AppContext clientContext = Hasor.createAppContext("customer-config-center.xml", new RsfModule() {
            public void loadRsf(RsfContext rsfContext) throws Throwable {
                rsfContext.binder().rsfService(EchoService.class).register();
                rsfContext.binder().rsfService(MessageService.class).register();
            }
        });
        System.out.println("server start.");
        //
        //Client -> Server
        RsfClient client = clientContext.getInstance(RsfClient.class);
        EchoService echoService = client.wrapper(EchoService.class);
        for (int i = 0; i < 208; i++) {
            try {
                String res = echoService.sayHello("Hello Word");
                System.out.println(res);
            } catch (Exception e) {
            }
        }
        //
        MessageService messageService = client.wrapper(MessageService.class);
        for (int i = 0; i < 208; i++) {
            try {
                RsfResult res = messageService.sayHello("Hello Word");//客户端会瞬间返回,服务端执行一个消息需要 500毫秒。
                System.out.println(res);
            } catch (Exception e) {
            }
        }
    }
}