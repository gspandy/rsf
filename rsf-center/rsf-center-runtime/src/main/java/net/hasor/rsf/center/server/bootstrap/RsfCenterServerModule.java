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
package net.hasor.rsf.center.server.bootstrap;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.LifeModule;
import net.hasor.rsf.RsfBinder;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.RsfModule;
import net.hasor.rsf.RsfPlugin;
import net.hasor.rsf.center.RsfCenterListener;
import net.hasor.rsf.center.RsfCenterRegister;
import net.hasor.rsf.center.server.AuthQuery;
import net.hasor.rsf.center.server.DataAdapter;
import net.hasor.rsf.center.server.domain.RsfCenterSettings;
import net.hasor.rsf.center.server.domain.WorkMode;
import net.hasor.rsf.center.server.register.RsfCenterRegisterProvider;
import net.hasor.rsf.center.server.register.RsfCenterServerVerifyFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 注册中心启动入口。
 *
 * @author 赵永春(zyc@hasor.net)
 * @version : 2015年5月5日
 */
public class RsfCenterServerModule implements LifeModule, RsfPlugin {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    //
    @Override
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        //
        // .注册中心配置信息
        RsfCenterSettings centerSettings = new RsfCenterSettings(apiBinder.getEnvironment());
        apiBinder.bindType(RsfCenterSettings.class).toInstance(centerSettings);
        apiBinder.bindType(WorkMode.class).toInstance(centerSettings.getWorkMode());
        //
        // .发布Center启动配置
        apiBinder.installModule(RsfModule.toModule(this));
        //
        // .adapter
        apiBinder.bindType(AuthQuery.class).to((Class<? extends AuthQuery>) centerSettings.getAuthQueryType());
        apiBinder.bindType(DataAdapter.class).to((Class<? extends DataAdapter>) centerSettings.getDataAdapterType());
        //
    }
    @Override
    public final void loadRsf(RsfContext rsfContext) throws Throwable {
        rsfContext.getAppContext().getInstance(AuthQuery.class);
        rsfContext.getAppContext().getInstance(DataAdapter.class);
        //
        rsfContext.offline();   //切换下线，暂不接收任何Rsf请求
        doStartCenter(rsfContext);
        rsfContext.online();    //切换上线，开始提供服务
        this.logger.info("rsfCenter online.");
    }
    public final void onStart(AppContext appContext) throws Throwable {
        /* 有了 loadRsf 方法,这个方法就可以省略了 */
    }
    /** Center启动 */
    protected void doStartCenter(RsfContext rsfContext) throws java.io.IOException {
        //
        // .判断RSF目前是否配置了启用连接Center,如果是,则不启动 center 服务器。因为RSF将以客户端形式运行
        boolean clientEnableCenter = rsfContext.getEnvironment().getSettings().isEnableCenter();
        if (clientEnableCenter) {
            this.logger.warn("this application has been started form the client mode, so rsfCenter cannot be started.");
            return;
        }
        //
        // .注册 Center提供服务的接口
        RsfCenterSettings centerSettings = rsfContext.getAppContext().getInstance(RsfCenterSettings.class);
        RsfBinder rsfBinder = rsfContext.binder();
        rsfBinder.rsfService(RsfCenterRegister.class).to(RsfCenterRegisterProvider.class)//
                .bindFilter("VerificationFilter", RsfCenterServerVerifyFilter.class)//
                .register();
        rsfBinder.rsfService(RsfCenterListener.class)// 
                .bindFilter("VerificationFilter", RsfCenterServerVerifyFilter.class)//
                .register();
        //
        // .工作模式
        WorkMode workMode = centerSettings.getWorkMode();
        this.logger.info("rsf work mode at : ({}){}", workMode.getCodeType(), workMode.getCodeString());
    }
    /** Center停止 */
    public void onStop(AppContext appContext) throws Throwable {
        /**/
    }
}