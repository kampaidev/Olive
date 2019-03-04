package com.kampaidev.olive.config;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

public abstract class Config {

	private Yaml yaml = new Yaml();
	protected Map<String, Object> config;
	private File file;

	public Config(File file) {
		this.file = file;
	}

	public boolean getBoolean(String s) {
		config = new HashMap<String, Object>();
		config.put("disable_default_plugins", false);
		return (boolean) config.get(s); // npe
	}

	public int getInt(String s) {
		return (int) config.get(s);
	}

	public String getString(String s) {
		return (String) config.get(s);
	}

	public double getDouble(String s) {
		return (double) config.get(s);
	}

	public void load() {
		
		if (!file.exists()) {
			this.config = saveDefaults();
			save();
			return;
		}
		try {
			config = (Map<String, Object>) yaml.load(new FileReader(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void save() {
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(file));
			//writer.write(toSave);
			 yaml.dump(config, writer);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public abstract Map<String, Object> saveDefaults();

}
