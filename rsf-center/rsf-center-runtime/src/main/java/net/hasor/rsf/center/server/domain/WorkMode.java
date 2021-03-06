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
package net.hasor.rsf.center.server.domain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 工作模式
 *
 * @version : 2015年7月3日
 * @author 赵永春(zyc@hasor.net)
 */
public enum WorkMode {
    /** 0，单机模式 */
    Alone(0, "alone", "单机模式"),
    /** 1，集群模式 */
    Master(1, "master", "集群主机模式"),
    /** 1，集群模式 */
    Follow(1, "follow", "集群丛属,永远不会被选为Leader"),;
    //
    //
    //
    // ---------------------------------------------
    private static Logger logger = LoggerFactory.getLogger(WorkMode.class);
    private int    codeType;
    private String codeString;
    private String message;
    WorkMode(int codeType, String codeString, String message) {
        this.codeType = codeType;
        this.codeString = codeString;
        this.message = message;
    }
    public int getCodeType() {
        return this.codeType;
    }
    public String getCodeString() {
        return this.codeString;
    }
    public String getMessage() {
        return this.message;
    }
    public static WorkMode getModeByCodeType(int codeType) {
        for (WorkMode a : WorkMode.values()) {
            if (a.getCodeType() == codeType) {
                return a;
            }
        }
        logger.error("WorkMode = " + codeType);
        throw new RuntimeException("not found WorkMode: " + codeType);
    }
}
