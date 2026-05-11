package org.frameworkset.spi.remote.http.template;
/**
 * Copyright 2008 biaoping.yin
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.frameworkset.util.DaemonThread;
import com.frameworkset.util.ResourceInitial;
import org.frameworkset.spi.BaseApplicationContext;
import org.frameworkset.spi.assemble.Pro;
import org.frameworkset.spi.runtime.BaseStarter;
import org.frameworkset.util.ResourceUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * <p>Description: </p>
 * <p></p>
 * <p>Copyright (c) 2018</p>
 * @Date 2020/1/17 14:36
 * @author biaoping.yin
 * @version 1.0
 */
public class AOPDslTemplateContainerImpl implements DslTemplateContainer {
	private BaseApplicationContext templatecontext;
	private String baseDir;
	private ConfigHolder configHolder;
	public AOPDslTemplateContainerImpl(ConfigHolder configHolder, String baseDir, BaseApplicationContext templatecontext) {
		this.templatecontext = templatecontext;
		this.baseDir = baseDir;
		this.configHolder = configHolder;
	}

	public AOPDslTemplateContainerImpl(ConfigHolder configHolder, BaseApplicationContext templatecontext) {
		this.templatecontext = templatecontext;
		this.configHolder = configHolder;
	}

	public String getNamespace(){
		return templatecontext.getConfigfile();
	}

	public Set<String> getTempalteNames(){
		return templatecontext.getPropertyKeys();
	}

	public DslTemplateMeta getProBean(String templateName){
		return new AOPDslTemplateMeta(templatecontext.getProBean(templateName));
	}

	public void destroy(boolean clearContext){
		templatecontext.destroy(clearContext);
	}

	public int getPerKeyDSLStructionCacheSize(){
		return templatecontext.getIntProperty(DslTemplateContainer.NAME_perKeyDSLStructionCacheSize, ConfigDSLUtil.defaultPerKeyDSLStructionCacheSize);
	}
	public boolean isAlwaysCacheDslStruction(){
		return templatecontext.getBooleanProperty(DslTemplateContainer.NAME_alwaysCacheDslStruction, ConfigDSLUtil.defaultAlwaysCacheDslStruction);
	}
	public synchronized void reinit(ConfigDSLUtil configDSLUtil){
		String file = templatecontext.getConfigfile();
		templatecontext.removeCacheContext();
		DslSOAFileApplicationContext dslSOAFileApplicationContext = new DslSOAFileApplicationContext(configHolder,baseDir,file);
		dslSOAFileApplicationContext.initProviderManager();
		if(dslSOAFileApplicationContext.getParserError() == null) {
//			esUtil.clearTemplateDatas();
			templatecontext.destroy(false);
			templatecontext = dslSOAFileApplicationContext;
//			templatecontext = new ESSOAFileApplicationContext(file);
			configDSLUtil.buildTemplateDatas(this);
//			trimValues();
//			destroyed = false;
		}
		else{
			templatecontext.restoreCacheContext();
		}
	}
	public void monitor(DaemonThread daemonThread,ResourceInitial resourceTempateRefresh){
		if(this.baseDir == null) {
			daemonThread.addFile(templatecontext.getConfigFileURL(), this.getNamespace(), resourceTempateRefresh);
		}
		else{

			daemonThread.addFile(templatecontext.getConfigFileURL(), ResourceUtils.getRealPath(baseDir,this.getNamespace()), resourceTempateRefresh);
		}
	}

	public List<DslTemplateMeta> getTemplateMetas(final String namespace){

		final List<DslTemplateMeta> DslTemplateMetaList = new ArrayList<DslTemplateMeta>();
		this.templatecontext.start(new BaseStarter() {
			public void start(Pro pro, BaseApplicationContext ioc) {
				Object _service = ioc.getBeanObject(pro.getName());
				if (_service == null || pro.getName().equals(DslTemplateContainer.NAME_perKeyDSLStructionCacheSize) || pro.getName().equals(DslTemplateContainer.NAME_alwaysCacheDslStruction))
					return;
				BaseDslTemplateMeta baseDSLTemplateMeta = new BaseDslTemplateMeta();
				baseDSLTemplateMeta.setName(pro.getName());
				baseDSLTemplateMeta.setNamespace(namespace);
				String templateFile = (String) pro.getExtendAttribute(DslTemplateContainer.NAME_templateFile);
				if (templateFile == null) {
					Object o = pro.getObject();
					if (o != null && o instanceof String) {

						String value = (String) o;
						baseDSLTemplateMeta.setDslTemplate(value);
						baseDSLTemplateMeta.setVtpl(pro.getBooleanExtendAttribute(DslTemplateContainer.NAME_istpl, true));//如果sql语句为velocity模板，则在批处理时是否需要每条记录都需要分析sql语句;
						//标识sql语句是否为velocity模板;
						baseDSLTemplateMeta.setMultiparser(pro.getBooleanExtendAttribute(DslTemplateContainer.NAME_multiparser, baseDSLTemplateMeta.getVtpl()));
						DslTemplateMetaList.add(baseDSLTemplateMeta);
					}
				} else {
					String templateName = (String) pro.getExtendAttribute(DslTemplateContainer.NAME_templateName);
					;
					if (templateName == null) {
						logger.warn(new StringBuilder().append("Ignore this DSL template ")
								.append(pro.getName()).append(" in the DSl file ")
								.append(getNamespace())
								.append(" is defined as a reference to the DSL template in another configuration file ")
								.append(templateFile)
								.append(", but the name of the DSL template statement to be referenced is not specified by the templateName attribute, for example:\r\n")
								.append("<property name= \"querySqlTraces\"\r\n")
								.append("templateFile= \"esmapper/estrace/ESTracesMapper.xml\"\r\n")
								.append("templateName= \"queryTracesByCriteria\"/>").toString());
					} else {
						baseDSLTemplateMeta.setReferenceNamespace(templateFile);
						baseDSLTemplateMeta.setReferenceTemplateName(templateName);
						baseDSLTemplateMeta.setVtpl(false);
						baseDSLTemplateMeta.setMultiparser(false);
						DslTemplateMetaList.add(baseDSLTemplateMeta);
					}
				}
			}
		});
		return DslTemplateMetaList;


	}
}
