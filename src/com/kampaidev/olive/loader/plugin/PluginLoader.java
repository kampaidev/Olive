package com.kampaidev.olive.loader.plugin;

import java.io.File;
import java.io.FileFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Iterator;
import java.util.ServiceLoader;

import com.kampaidev.olive.Olive;
import com.kampaidev.olive.plugin.OlivePlugin;

public class PluginLoader {

	private Olive olive;
	
	public PluginLoader(Olive olive) {
		this.olive = olive;
	}
	
	public void loadPlugins() throws MalformedURLException {
	    File loc = new File("plugins");

        File[] pluginList = loc.listFiles(new FileFilter() {
            public boolean accept(File file) {return file.getPath().toLowerCase().endsWith(".jar");}
        });
        
        URL[] urls = new URL[pluginList.length];
        for (int i = 0; i < pluginList.length; i++)
            urls[i] = pluginList[i].toURI().toURL();
        URLClassLoader ucl = new URLClassLoader(urls);

        ServiceLoader<OlivePlugin> sl = ServiceLoader.load(OlivePlugin.class, ucl);
        Iterator<OlivePlugin> apit = sl.iterator();
        while (apit.hasNext())
            System.out.println(olive.getPlugins().add(apit.next()));
	}
	
}
