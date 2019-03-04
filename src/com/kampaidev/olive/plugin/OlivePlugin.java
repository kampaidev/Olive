package com.kampaidev.olive.plugin;

import java.io.File;

import com.kampaidev.olive.Olive;
import com.kampaidev.olive.backend.route.Route;
import com.kampaidev.olive.backend.route.RouteType;
import com.kampaidev.olive.backend.route.Route.RouteHandler;

import io.netty.handler.codec.http.HttpMethod;

public abstract class OlivePlugin implements RouteHandler {
	
	protected Olive olive;
	protected String file, path;
	private Route route;
	private HttpMethod method;
	private RouteType routeType;
	
	public OlivePlugin(Olive olive, String path, HttpMethod method, RouteType routeType, File file) {
		this.olive = olive;
		this.path = path;
		this.method = method;
		this.routeType = routeType;
		this.file = olive.getLoader().getHtmlLoader().readTemplate(file);
	}
	
	public abstract String translate(String translate);

	public Route createRoute() {
		return route = new Route(path, method, routeType, this::handle);
	}
	
	public Route route() {
		return route;
	}
	
	public String file() {
		return file;
	}
	
}
