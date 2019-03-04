package com.kampaidev.olive;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class OliveOptions {

	private String[] args;

	public OliveOptions(String args[]) {
		this.args = args;
	}

	public void createIndex(Olive olive) {
		try {
			File index = new File("webfiles/index.html");
			index.createNewFile();

			String str = "<h1>Hello</h1>";
			BufferedWriter writer = new BufferedWriter(new FileWriter(index));
			writer.write(str);

			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
