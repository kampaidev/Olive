package com.kampaidev.olive.loader;

import com.kampaidev.olive.Olive;
import com.kampaidev.olive.loader.html.HtmlLoader;
import com.kampaidev.olive.loader.plugin.PluginLoader;

public class OliveLoader {

	private Olive olive;
	private HtmlLoader htmlLoader;
	private PluginLoader pluginLoader;
	
	public OliveLoader(Olive olive) {
		this.olive = olive;
		this.htmlLoader = new HtmlLoader(olive);
		this.pluginLoader = new PluginLoader(olive);
	}
	
	public HtmlLoader getHtmlLoader() {
		return htmlLoader;
	}
	
	public PluginLoader getPluginLoader() {
		return pluginLoader;
	}
	
}
