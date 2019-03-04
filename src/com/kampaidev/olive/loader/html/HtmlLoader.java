package com.kampaidev.olive.loader.html;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.kampaidev.olive.Olive;

public class HtmlLoader {

	private Olive olive;
	
	public HtmlLoader(Olive olive) {
		this.olive = olive;
	}
	
	public String readTemplate(File file) {
		StringBuilder contentBuilder = new StringBuilder();
		try {
		    BufferedReader in = new BufferedReader(new FileReader(file));
		    String str;
		    while ((str = in.readLine()) != null) {
		        contentBuilder.append(str);
		    }
		    in.close();
		} catch (IOException e) {
		}
		
		String translated = translateTemplate(contentBuilder);
		String path = file.getAbsolutePath();
		return translated;
	}
	
	public String translateTemplate(StringBuilder template) {
		return template.toString();
	}
	
}
