package com.kampaidev.olive;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.kampaidev.olive.backend.OliveServer;
import com.kampaidev.olive.backend.WebServer;
import com.kampaidev.olive.config.impl.DefaultConfig;
import com.kampaidev.olive.loader.OliveLoader;
import com.kampaidev.olive.plugin.OlivePlugin;
import com.kampaidev.olive.plugin.impl.OliveIndex;

/*
 * TODO:
 * - Config system (DONE)
 * - Plugin system that supports: Java, Javascript
 * - Template system, similar to Django
 * - Loader for plugins & templates
 * - Just testing and optimizing after these then we upload ^
 */

public class Olive {

	private static Olive olive;
	private List<OlivePlugin> plugins = new ArrayList<>();
	private OliveOptions options;
	private WebServer oliveServer;
	private DefaultConfig config;
	private OliveLoader loader;
	private File index;

	public Olive(String[] args) {
		options = new OliveOptions(args);
		loader = new OliveLoader(this);

		System.out.println("====================================");
		System.out.println("Welcome to OliveServer...");
		System.out.println("Please report any issue founds to my github");
		System.out.println("");

		System.out.println("Loading webfiles...");

		File webfiles = new File("webfiles");
		if (!webfiles.exists()) {
			webfiles.mkdir();
			options.createIndex(this);
		} else {
			index = new File("webfiles/index.html");
			if (!index.exists()) {
				options.createIndex(this);
			}
		}
		

		System.out.println("Webfiles have been loaded");
		System.out.println("");

		System.out.println("Loading config...");

		File config = new File("config.yaml");
		if (!config.exists()) {
			try {
				config.createNewFile();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		this.config = new DefaultConfig(config);
		this.config.load();
		
		System.out.println("Config has been loaded");
		System.out.println("");
		
		System.out.println("Loading plugins...");

		File plugins = new File("plugins");
		if (!plugins.exists()) {
			plugins.mkdir();
		} else {
			try {
				loader.getPluginLoader().loadPlugins();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		
		//boolean disable_default = this.config.getBoolean("disable_default_plugins");
		//if(!disable_default)
			this.plugins.add(new OliveIndex(this, new File("webfiles/index.html")));

		System.out.println("[" + this.plugins.size() + "] Plugins have been loaded");
		
		System.out.println("====================================");

		Thread oliveThread = new Thread(() -> {
			try {
				
				//String name = this.config.getString("name");
				//int port = this.config.getInt("port");
				//boolean https = this.config.getBoolean("https-enabled");
				
				oliveServer = new OliveServer("OliveServer", 80, true)
						.plugin(this.plugins)
						
						.start(this.config);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});
		oliveThread.start();
		System.out.println("Olive has been started");
	}
	
	public static void main(String[] args) {
		olive = new Olive(args);
		
	}

	public File setIndex(File file) {
		this.index = file;
		return index;
	}
	
	public List<OlivePlugin> getPlugins() {
		return plugins;
	}
	
	public WebServer getServer() {
		return oliveServer;
	}
	
	public OliveLoader getLoader() {
		return loader;
	}

	public static Olive getInstance() {
		return olive;
	}

}
