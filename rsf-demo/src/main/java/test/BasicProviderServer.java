/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package test;
import net.hasor.core.Hasor;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.RsfModule;
import test.services.EchoService;
import test.services.EchoServiceImpl;
import test.services.MessageService;
import test.services.MessageServiceImpl;
/**
 * 启动服务端
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class BasicProviderServer {
    public static void main(String[] args) throws Throwable {
        //Server
        Hasor.createAppContext("provider-config-basic.xml", new RsfModule() {
            @Override
            public void loadRsf(RsfContext rsfContext) throws Throwable {
                rsfContext.binder().rsfService(EchoService.class).toInstance(new EchoServiceImpl()).register();
                rsfContext.binder().rsfService(MessageService.class).toInstance(new MessageServiceImpl()).register();
            }
        });
        //
        System.out.println("server start.");
        while (true) {
            Thread.sleep(100);
        }
    }
}