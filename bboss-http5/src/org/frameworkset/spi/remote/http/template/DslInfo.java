package org.frameworkset.spi.remote.http.template;

import com.frameworkset.util.VariableHandler;

public class DslInfo {
	private DslTemplateMeta templatePro;
	private boolean tpl ;
	private DslTemplate estpl;
	private String templateName;
	private String template;
	private boolean multiparser;
	private ConfigDSLUtil configDSLUtil;
	private boolean cache;
	public DslInfo(String templateName, String template, boolean istpl, boolean multiparser, DslTemplateMeta templatePro, boolean cache) {
		this.template = template;
		this.templateName = templateName;
		this.tpl = istpl;
		this.multiparser = multiparser;
		this.templatePro = templatePro;
		this.cache = cache;
	}
	public String getDslFile(){
		return this.configDSLUtil.templateFile;
	}
	public String getTemplate() {
		return template;
	}
	public void setTemplate(String template) {
		this.template = template;
	}
	public boolean isTpl() {
		return tpl;
	}
	public void setTpl(boolean tpl) {
		this.tpl = tpl;
	}
	public DslTemplate getEstpl() {
		return estpl;
	}
	public void setEstpl(DslTemplate estpl) {
		this.estpl = estpl;
	}
	public DslTemplateMeta getTemplatePro() {
		return templatePro;
	}
	public void setTemplatePro(DslTemplateMeta templatePro) {
		this.templatePro = templatePro;
	}
	public String getTemplateName() {
		return templateName;
	}
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}
	public boolean isMultiparser() {
		return multiparser;
	}
	public void setMultiparser(boolean multiparser) {
		this.multiparser = multiparser;
	}
	public ConfigDSLUtil getConfigDSLUtil() {
		return configDSLUtil;
	}
	public void setConfigDSLUtil(ConfigDSLUtil configDSLUtil) {
		this.configDSLUtil = configDSLUtil;
	}
	
	public boolean equals(Object obj)
	{
		if(obj == null)
			return false;
		if(obj instanceof DslInfo)
		{
			DslInfo o = (DslInfo)obj;
			return this.getTemplate().equals(o.getTemplate());
		}
		else
		{
			return false;
		}
	}
	public boolean fromConfig()
	{
		return this.configDSLUtil != null && this.configDSLUtil.fromConfig();
	}
	public int compareTo(DslInfo queryDSL)
	{
		return this.template.compareTo(queryDSL.getTemplate());
	}
	
	public DslInfo getESInfo(String sqlname)
	{
		return this.configDSLUtil.getESInfo( sqlname);
	}
	
	public String getPlainQueryDSL(String sqlname)
	{
		return this.configDSLUtil.getPlainTemplate( sqlname);
	}

	public VariableHandler.URLStruction getTemplateStruction(String template){
		return this.configDSLUtil.getTempateStruction(this,template);
	}

	public int hashCode(){
		return this.getTemplate().hashCode();
	}


	public boolean isCache() {
		return cache;
	}
}
