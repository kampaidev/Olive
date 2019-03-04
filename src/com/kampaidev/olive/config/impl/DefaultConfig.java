package com.kampaidev.olive.config.impl;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.kampaidev.olive.config.Config;

public class DefaultConfig extends Config {

	public DefaultConfig(File config) {
		super(config);
	}

	@Override
	public Map<String, Object> saveDefaults() {
		Map<String, Object> defaults = new HashMap<>();
		defaults.put("name", "Olive");
		defaults.put("https-enabled", true);
		defaults.put("port", 80);
		defaults.put("disable_default_plugins", false);
		this.config = defaults; // java is being weird so this is temporary :/
		return defaults;
	}

}
