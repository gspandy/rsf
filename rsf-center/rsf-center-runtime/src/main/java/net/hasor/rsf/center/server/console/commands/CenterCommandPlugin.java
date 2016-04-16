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
package net.hasor.rsf.center.server.console.commands;
import net.hasor.core.ApiBinder;
import net.hasor.core.Module;
import net.hasor.rsf.console.RsfCommand;
import net.hasor.rsf.console.commands.FlowRsfCommand;
import net.hasor.rsf.console.commands.GetSetRsfCommand;
import net.hasor.rsf.console.commands.HelpRsfCommand;
import net.hasor.rsf.console.commands.QuitRsfCommand;
import net.hasor.rsf.console.commands.RuleRsfCommand;
import net.hasor.rsf.console.commands.ServiceRsfCommand;
import net.hasor.rsf.console.commands.SwitchRsfCommand;
/**
 * 内置命令集
 * @version : 2016年4月3日
 * @author 赵永春(zyc@hasor.net)
 */
public class CenterCommandPlugin implements Module {
    @Override
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        apiBinder.bindType(RsfCommand.class).uniqueName().to(HelpRsfCommand.class);
        apiBinder.bindType(RsfCommand.class).uniqueName().to(QuitRsfCommand.class);
        apiBinder.bindType(RsfCommand.class).uniqueName().to(SwitchRsfCommand.class);
        apiBinder.bindType(RsfCommand.class).uniqueName().to(GetSetRsfCommand.class);
        apiBinder.bindType(RsfCommand.class).uniqueName().to(ServiceRsfCommand.class);
        apiBinder.bindType(RsfCommand.class).uniqueName().to(RuleRsfCommand.class);
        apiBinder.bindType(RsfCommand.class).uniqueName().to(FlowRsfCommand.class);
    }
}