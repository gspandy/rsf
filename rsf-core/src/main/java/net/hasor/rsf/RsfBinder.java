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
package net.hasor.rsf;
import net.hasor.core.BindInfo;
import net.hasor.core.Provider;
import net.hasor.rsf.address.InterAddress;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
/**
 * 服务配置器
 * @version : 2014年11月12日
 * @author 赵永春(zyc@hasor.net)
 */
public interface RsfBinder {
    /**
     * 添加全局的RsfFilter。
     * @param filterID filter ID
     * @param instance 过滤器实例
     */
    public void bindFilter(String filterID, RsfFilter instance);

    /**
     * 添加全局的RsfFilter。
     * @param filterID filter ID
     * @param provider provider for RsfFilter
     */
    public void bindFilter(String filterID, Provider<? extends RsfFilter> provider);

    /**
     * 绑定一个类型到RSF环境。
     * @param type 服务类型 
     *
     * @return 返回细粒度绑定操作接口 - {@link LinkedBuilder}
     */
    public <T> LinkedBuilder<T> rsfService(Class<T> type);

    /**
     * 绑定一个类型并且为这个类型指定一个实例。开发者可以通过返回的 Builder 可以对绑定进行后续更加细粒度的绑定。<p>
     * 该方法相当于“<code>rsfBinder.rsfService(type).toInstance(instance);</code>”
     * @param type 服务类型
     * @param instance 为绑定指定的实例对象。
     * @return 返回细粒度绑定操作接口 - {@link ConfigurationBuilder}
     * @see net.hasor.rsf.RsfBinder.ConfigurationBuilder#rsfService(Class)
     */
    public <T> ConfigurationBuilder<T> rsfService(Class<T> type, T instance);

    /**
     * 绑定一个类型并且为这个类型指定一个实现类。开发者可以通过返回的 Builder 可以对绑定进行后续更加细粒度的绑定。<p>
     * 该方法相当于“<code>rsfBinder.rsfService(type).to(implementation);</code>”
     * @param type 服务类型
     * @param implementation 为绑定指定的实现类。
     * @return 返回细粒度绑定操作接口 - {@link ConfigurationBuilder}
     * @see net.hasor.rsf.RsfBinder.ConfigurationBuilder#rsfService(Class)
     */
    public <T> ConfigurationBuilder<T> rsfService(Class<T> type, Class<? extends T> implementation);

    /**
     * 绑定一个类型并且为这个类型指定一个实现类。开发者可以通过返回的 Builder 可以对绑定进行后续更加细粒度的绑定。<p>
     * 该方法相当于“<code>rsfBinder.rsfService(type).toInfo(bindInfo);</code>”
     * @param type 服务类型
     * @param bindInfo 为绑定指定的实现类。
     * @return 返回细粒度绑定操作接口 - {@link ConfigurationBuilder}
     * @see net.hasor.rsf.RsfBinder.ConfigurationBuilder#rsfService(Class)
     */
    public <T> ConfigurationBuilder<T> rsfService(Class<T> type, BindInfo<T> bindInfo);

    /**
     * 绑定一个类型并且为这个类型指定一个Provider。开发者可以通过返回的 Builder 可以对绑定进行后续更加细粒度的绑定。<p>
     * 该方法相当于“<code>rsfBinder.rsfService(type).toProvider(provider);</code>”
     * @param type 服务类型
     * @param provider 为绑定指定的实现类。
     * @return 返回细粒度绑定操作接口 - {@link ConfigurationBuilder}
     * @see net.hasor.rsf.RsfBinder.ConfigurationBuilder#rsfService(Class)
     */
    public <T> ConfigurationBuilder<T> rsfService(Class<T> type, Provider<T> provider);
    //
    //
    //
    /**处理类型和实现的绑定。*/
    public interface LinkedBuilder<T> extends ConfigurationBuilder<T> {
        /**
         * 为绑定设置一个实现类。
         * @param implementation 实现类
         * @return 返回 ConfigurationBuilder。
         */
        public ConfigurationBuilder<T> to(Class<? extends T> implementation);

        /**
         * 为绑定设置一个实例。
         * @param instance 实例对象
         * @return 返回 ConfigurationBuilder。
         */
        public ConfigurationBuilder<T> toInstance(T instance);

        /**
         * 为绑定设置一个 {@link Provider}。
         * @param provider provider
         * @return 返回 ConfigurationBuilder。
         */
        public ConfigurationBuilder<T> toProvider(Provider<T> provider);

        /**
         * 为绑定设置一个 {@link BindInfo}。
         * @param bindInfo BindInfo
         * @return 返回 ConfigurationBuilder。
         */
        public ConfigurationBuilder<T> toInfo(BindInfo<T> bindInfo);
    }
    /**设置服务名。*/
    public interface ConfigurationBuilder<T> extends FilterBindBuilder<T> {
        /**
         * 设置服务分组信息
         * @param group 所属分组
         * @return 返回ConfigurationBuilder
         */
        public ConfigurationBuilder<T> group(String group);

        /**
         * 设置服务名称信息
         * @param name 名称
         * @return 返回ConfigurationBuilder
         */
        public ConfigurationBuilder<T> name(String name);

        /**
         * 设置服务版本信息
         * @param version 版本
         * @return 返回ConfigurationBuilder
         */
        public ConfigurationBuilder<T> version(String version);

        /**
         * 设置超时时间
         * @param clientTimeout 超时时间
         * @return 返回ConfigurationBuilder。
         */
        public ConfigurationBuilder<T> timeout(int clientTimeout);

        /**
         * 设置序列化方式
         * @param serializeType 序列化方式
         * @return 返回ConfigurationBuilder
         */
        public ConfigurationBuilder<T> serialize(String serializeType);
    }
    //
    /**设置过滤器*/
    public interface FilterBindBuilder<T> extends RegisterBuilder<T> {
        /**
         * 为服务添加一个专有的RsfFilter。
         * @param subFilterID filter ID,如果服务专有的filterID和全局RsfFilter出现冲突，那么优先选用该RsfFilter。
         * @param instance Rsffilter实例
         * @return 返回ConfigurationBuilder
         */
        public FilterBindBuilder<T> bindFilter(String subFilterID, RsfFilter instance);

        /**
         * 为服务添加一个专有的RsfFilter。
         * @param subFilterID filter ID,如果服务专有的filterID和全局RsfFilter出现冲突，那么优先选用该RsfFilter。
         * @param provider provider for Rsffilter.
         * @return 返回ConfigurationBuilder
         */
        public FilterBindBuilder<T> bindFilter(String subFilterID, Provider<? extends RsfFilter> provider);

        /**
         * 为服务添加一个专有的RsfFilter。
         * @param subFilterID filter ID,如果服务专有的filterID和全局RsfFilter出现冲突，那么优先选用该RsfFilter。
         * @param rsfFilterType Class for Rsffilter.
         * @return 返回ConfigurationBuilder
         */
        public FilterBindBuilder<T> bindFilter(String subFilterID, Class<? extends RsfFilter> rsfFilterType);
    }
    //
    /**发布地址*/
    public interface RegisterBuilder<T> {
        /**更新服务地址本计算规则（服务级）*/
        public void updateServiceRoute(String scriptBody);

        /**更新本地方法级地址计算脚本。*/
        public void updateMethodRoute(String scriptBody);

        /**更新本地参数级地址计算脚本。*/
        public void updateArgsRoute(String scriptBody);

        /**更新服务路由策略*/
        public void updateFlowControl(String flowControl);

        /**
         * @param rsfHost 远程服务地址
         * @param port 远程服务端口
         */
        public RegisterBuilder<T> bindAddress(String rsfHost, int port) throws URISyntaxException;

        /**
         * 远程地址例:“rsf://127.0.0.1:8000/unit”或“rsf://127.0.0.1:8000/unit/group/name/version”
         * @param rsfURI 远程服务地址
         */
        public RegisterBuilder<T> bindAddress(String rsfURI, String... array) throws URISyntaxException;

        /**
         * 远程地址例:“rsf://127.0.0.1:8000/unit”或“rsf://127.0.0.1:8000/unit/group/name/version”
         * @param rsfURI 远程服务地址
         */
        public RegisterBuilder<T> bindAddress(URI rsfURI, URI... array);

        /**
         * 远程地址例:“rsf://127.0.0.1:8000/unit”或“rsf://127.0.0.1:8000/unit/group/name/version”
         * @param rsfAddress 远程服务地址
         */
        public RegisterBuilder<T> bindAddress(InterAddress rsfAddress, InterAddress... array);

        /** @return 将服务注册到{@link RsfContext}上。*/
        public RegisterReference<T> register() throws IOException;
    }
    //
    /**接口解除*/
    public interface RegisterReference<T> extends RsfBindInfo<T> {
        /**解除注册。*/
        public boolean unRegister();
    }
}