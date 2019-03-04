package com.kampaidev.olive.plugin.impl;

import java.io.File;

import com.kampaidev.olive.Olive;
import com.kampaidev.olive.backend.msg.Request;
import com.kampaidev.olive.backend.msg.Response;
import com.kampaidev.olive.backend.route.Route;
import com.kampaidev.olive.backend.route.RouteType;
import com.kampaidev.olive.plugin.OlivePlugin;

import io.netty.handler.codec.http.HttpMethod;

public class OliveIndex extends OlivePlugin {

	public OliveIndex(Olive olive, File file) {
		super(olive, "/", HttpMethod.GET, RouteType.HTML, file);
	}

	@Override
	public String translate(String translate) {
		return translate.replaceAll("Hi", "Kameron was here");
	}

	@Override
	public Object handle(Request request, Response response) throws Exception {
		return translate(file) + request.body();
	}

}
