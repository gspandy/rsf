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
package net.hasor.rsf.utils;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.container.RsfBeanContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
/**
 * 业务线程
 * @version : 2014年11月11日
 * @author 赵永春(zyc@hasor.net)
 */
public class ExecutesManager {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    private       ThreadPoolExecutor                        defaultExecutor;
    private final ConcurrentMap<String, ThreadPoolExecutor> servicePoolCache;
    //
    public ExecutesManager(int minCorePoolSize, int maxCorePoolSize, int queueSize, long keepAliveTime) {
        logger.info("executesManager init ->> minCorePoolSize ={}, maxCorePoolSize ={}, queueSize ={}, keepAliveTime ={}", minCorePoolSize, maxCorePoolSize, queueSize, keepAliveTime);
        //
        final BlockingQueue<Runnable> inWorkQueue = new LinkedBlockingQueue<Runnable>(queueSize);
        this.defaultExecutor = new ThreadPoolExecutor(minCorePoolSize, maxCorePoolSize, //
                keepAliveTime, TimeUnit.SECONDS, inWorkQueue, //
                new NameThreadFactory("RSF-Biz-%s"), new ThreadPoolExecutor.AbortPolicy());
        this.servicePoolCache = new ConcurrentHashMap<String, ThreadPoolExecutor>();
    }
    //
    public Executor getExecute(String serviceUniqueName) {
        if (!this.servicePoolCache.isEmpty() && serviceUniqueName != null) {
            ThreadPoolExecutor executor = this.servicePoolCache.get(serviceUniqueName);
            if (executor != null) {
                return executor;
            }
        }
        return this.defaultExecutor;
    }
    /**初始化服务特殊线程*/
    public void init(RsfBeanContainer rsfBeanContainer) {
        List<String> serviceList = rsfBeanContainer.getServiceIDs();
        for (String serviceID : serviceList) {
            RsfBindInfo<?> rsfServiceInfo = rsfBeanContainer.getRsfBindInfo(serviceID);
        }
    }
    /**停止应用服务。*/
    public void shutdown() {
        List<ThreadPoolExecutor> executorList = new ArrayList<ThreadPoolExecutor>(this.servicePoolCache.values());
        executorList.add(this.defaultExecutor);
        this.servicePoolCache.clear();
        this.defaultExecutor = null;
        //
        for (ThreadPoolExecutor exec : executorList) {
            exec.shutdown();
        }
        while (true) {
            boolean jump = true;
            for (ThreadPoolExecutor exec : executorList) {
                if (!exec.isShutdown()) {
                    jump = false;
                    break;
                }
            }
            if (jump) {
                break;
            }
            try {
                Thread.sleep(100);
            } catch (Exception e) { /**/ }
        }
    }
}