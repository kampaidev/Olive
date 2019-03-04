package com.kampaidev.olive.backend;

import java.util.List;

import com.kampaidev.olive.backend.route.Route;
import com.kampaidev.olive.backend.route.Route.RouteHandler;
import com.kampaidev.olive.backend.route.RouteType;
import com.kampaidev.olive.config.Config;
import com.kampaidev.olive.plugin.OlivePlugin;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;

public interface WebServer {

	public WebServer start(Config config) throws InterruptedException;
	public WebServer channel(EventLoopGroup loopGroup, Class<? extends ServerChannel> serverChannelCl) throws InterruptedException;
	public WebServer route(Route route);
	public WebServer post(String path, RouteHandler handler);
	public WebServer get(String path,  RouteHandler handler);
	public WebServer post(String path, RouteType type, RouteHandler handler);
	public WebServer get(String path, RouteType type,  RouteHandler handler);
	public WebServer plugin(List<OlivePlugin> plugins);
	public int port();
	
}
