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
package net.hasor.paxos.domain;
/**
 * 决议
 * @version : 2016年09月10日
 * @author 赵永春(zyc@hasor.net)
 */
public class AcceptMessage {
    private ProposalID proposalID;  // 提案ID
    private String     dataSnapshot;// 数据快照
    //
    public ProposalID getProposalID() {
        return proposalID;
    }
    public void setProposalID(ProposalID proposalID) {
        this.proposalID = proposalID;
    }
    public String getDataSnapshot() {
        return dataSnapshot;
    }
    public void setDataSnapshot(String dataSnapshot) {
        this.dataSnapshot = dataSnapshot;
    }
}