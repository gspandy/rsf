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
package net.hasor.rsf.container;
import net.hasor.core.AppContext;
import net.hasor.core.BindInfo;
import net.hasor.core.Hasor;
import net.hasor.core.Provider;
import net.hasor.core.binder.InstanceProvider;
import net.hasor.rsf.*;
import net.hasor.rsf.address.InterAddress;
import net.hasor.rsf.address.InterServiceAddress;
import net.hasor.rsf.domain.RsfServiceType;
import net.hasor.rsf.domain.ServiceDomain;
import org.more.FormatException;
import org.more.util.StringUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;
/**
 * 服务注册器
 * @version : 2014年11月12日
 * @author 赵永春(zyc@hasor.net)
 */
abstract class RsfBindBuilder implements RsfBinder {
    protected abstract RsfBeanContainer getContainer();

    protected abstract RsfContext getRsfContext();
    //
    public void bindFilter(String filterID, RsfFilter instance) {
        this.getContainer().addFilter(filterID, instance);
    }
    public void bindFilter(String filterID, Provider<? extends RsfFilter> provider) {
        this.getContainer().addFilter(filterID, provider);
    }
    public <T> LinkedBuilder<T> rsfService(Class<T> type) {
        return new LinkedBuilderImpl<T>(type);
    }
    public <T> ConfigurationBuilder<T> rsfService(Class<T> type, T instance) {
        return this.rsfService(type).toInstance(instance);
    }
    public <T> ConfigurationBuilder<T> rsfService(Class<T> type, Class<? extends T> implementation) {
        return this.rsfService(type).to(implementation);
    }
    public <T> ConfigurationBuilder<T> rsfService(Class<T> type, Provider<T> provider) {
        return this.rsfService(type).toProvider(provider);
    }
    public <T> ConfigurationBuilder<T> rsfService(Class<T> type, BindInfo<T> bindInfo) {
        return this.rsfService(type).toInfo(bindInfo);
    }
    //
    private class LinkedBuilderImpl<T> implements LinkedBuilder<T> {
        private final ServiceInfo<T>    serviceDefine;
        private final Set<InterAddress> addressSet;
        //
        protected LinkedBuilderImpl(Class<T> serviceType) {
            this.serviceDefine = new ServiceInfo<T>(serviceType);
            this.addressSet = new HashSet<InterAddress>();
            RsfSettings settings = getRsfContext().getEnvironment().getSettings();
            //
            RsfService serviceInfo = new AnnoRsfServiceValue(settings, serviceType);
            ServiceDomain<T> domain = this.serviceDefine.getDomain();
            domain.setServiceType(RsfServiceType.Consumer);
            domain.setBindGroup(serviceInfo.group());
            domain.setBindName(serviceInfo.name());
            domain.setBindVersion(serviceInfo.version());
            domain.setSerializeType(serviceInfo.serializeType());
            domain.setClientTimeout(serviceInfo.clientTimeout());
        }
        //
        @Override
        public ConfigurationBuilder<T> group(String group) {
            Hasor.assertIsNotNull(group, "group is null.");
            if (group.contains("/")) {
                throw new FormatException(group + " contain '/'");
            }
            this.serviceDefine.getDomain().setBindGroup(group);
            return this;
        }
        //
        @Override
        public ConfigurationBuilder<T> name(String name) {
            Hasor.assertIsNotNull(name, "name is null.");
            if (name.contains("/")) {
                throw new FormatException(name + " contain '/'");
            }
            this.serviceDefine.getDomain().setBindName(name);
            return this;
        }
        //
        @Override
        public ConfigurationBuilder<T> version(String version) {
            Hasor.assertIsNotNull(version, "version is null.");
            if (version.contains("/")) {
                throw new FormatException(version + " contain '/'");
            }
            this.serviceDefine.getDomain().setBindVersion(version);
            return this;
        }
        //
        @Override
        public ConfigurationBuilder<T> timeout(int clientTimeout) {
            if (clientTimeout < 1) {
                throw new FormatException("clientTimeout must be greater than 0");
            }
            this.serviceDefine.getDomain().setClientTimeout(clientTimeout);
            return this;
        }
        //
        @Override
        public ConfigurationBuilder<T> serialize(String serializeType) {
            Hasor.assertIsNotNull(serializeType, "serializeType is null.");
            if (serializeType.contains("/")) {
                throw new FormatException(serializeType + " contain '/'");
            }
            this.serviceDefine.getDomain().setSerializeType(serializeType);
            return this;
        }
        //
        public ConfigurationBuilder<T> bindFilter(String filterID, RsfFilter instance) {
            this.serviceDefine.addRsfFilter(filterID, instance);
            return this;
        }
        //
        public ConfigurationBuilder<T> bindFilter(String filterID, Provider<? extends RsfFilter> provider) {
            this.serviceDefine.addRsfFilter(filterID, provider);
            return this;
        }
        @Override
        public FilterBindBuilder<T> bindFilter(String filterID, Class<? extends RsfFilter> rsfFilterType) {
            final AppContext appContext = getRsfContext().getAppContext();
            this.serviceDefine.addRsfFilter(filterID, new RsfFilterProvider(appContext, rsfFilterType));
            return this;
        }
        //
        @Override
        public ConfigurationBuilder<T> to(final Class<? extends T> implementation) {
            Hasor.assertIsNotNull(implementation);
            final AppContext appContext = getRsfContext().getAppContext();
            return this.toProvider(new Provider<T>() {
                public T get() {
                    return appContext.getInstance(implementation);
                }
            });
        }
        @Override
        public ConfigurationBuilder<T> toInfo(final BindInfo<T> bindInfo) {
            Hasor.assertIsNotNull(bindInfo);
            final AppContext appContext = getRsfContext().getAppContext();
            return this.toProvider(new Provider<T>() {
                public T get() {
                    return appContext.getInstance(bindInfo);
                }
            });
        }
        @Override
        public ConfigurationBuilder<T> toInstance(T instance) {
            return this.toProvider(new InstanceProvider<T>(instance));
        }
        //
        @Override
        public ConfigurationBuilder<T> toProvider(Provider<T> provider) {
            this.serviceDefine.getDomain().setServiceType(RsfServiceType.Provider);
            this.serviceDefine.setCustomerProvider(provider);
            return this;
        }
        //
        @Override
        public RegisterBuilder<T> bindAddress(String rsfHost, int port) throws URISyntaxException {
            String unitName = getRsfContext().getEnvironment().getSettings().getUnitName();
            return this.bindAddress(new InterAddress(rsfHost, port, unitName));
        }
        @Override
        public RegisterBuilder<T> bindAddress(String rsfURI, String... array) throws URISyntaxException {
            if (!StringUtils.isBlank(rsfURI)) {
                this.bindAddress(new InterAddress(rsfURI));
            }
            if (array.length > 0) {
                for (String bindItem : array) {
                    this.bindAddress(new InterAddress(bindItem));
                }
            }
            return this;
        }
        @Override
        public RegisterBuilder<T> bindAddress(URI rsfURI, URI... array) {
            if (rsfURI != null && (InterServiceAddress.checkFormat(rsfURI) || InterAddress.checkFormat(rsfURI))) {
                this.bindAddress(new InterAddress(rsfURI));
            }
            if (array.length > 0) {
                for (URI bindItem : array) {
                    if (rsfURI != null && (InterServiceAddress.checkFormat(bindItem) || InterAddress.checkFormat(bindItem))) {
                        this.bindAddress(new InterAddress(bindItem));
                    }
                    throw new FormatException(bindItem + " check fail.");
                }
            }
            return this;
        }
        public RegisterBuilder<T> bindAddress(InterAddress rsfAddress, InterAddress... array) {
            if (rsfAddress != null) {
                this.addressSet.add(rsfAddress);
            }
            if (array.length > 0) {
                for (InterAddress bindItem : array) {
                    if (bindItem == null)
                        continue;
                    this.addressSet.add(bindItem);
                }
            }
            return this;
        }
        //
        public RegisterReference<T> register() throws IOException {
            String serviceID = this.serviceDefine.getDomain().getBindID();
            RsfUpdater updater = getRsfContext().getUpdater();
            // .先发布服务,避免地址发布成功之后服务发布失败,遗留垃圾数据的问题。
            RegisterReference<T> ref = getContainer().publishService(this.serviceDefine);
            updater.appendStaticAddress(serviceID, this.addressSet);
            return ref;
        }
        @Override
        public void updateFlowControl(String flowControl) {
            String serviceID = this.serviceDefine.getDomain().getBindID();
            getRsfContext().getUpdater().updateFlowControl(serviceID, flowControl);
        }
        @Override
        public void updateArgsRoute(String scriptBody) {
            String serviceID = this.serviceDefine.getDomain().getBindID();
            getRsfContext().getUpdater().updateArgsRoute(serviceID, scriptBody);
        }
        @Override
        public void updateMethodRoute(String scriptBody) {
            String serviceID = this.serviceDefine.getDomain().getBindID();
            getRsfContext().getUpdater().updateMethodRoute(serviceID, scriptBody);
        }
        @Override
        public void updateServiceRoute(String scriptBody) {
            String serviceID = this.serviceDefine.getDomain().getBindID();
            getRsfContext().getUpdater().updateServiceRoute(serviceID, scriptBody);
        }
    }
}