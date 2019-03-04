package com.kampaidev.olive.backend.route;

import com.kampaidev.olive.backend.msg.Request;
import com.kampaidev.olive.backend.msg.Response;

import io.netty.handler.codec.http.HttpMethod;

public class Route {
	
    private String path;
    private RouteHandler handler;
    private HttpMethod method;
    private RouteType routeType;

    public Route(String path, HttpMethod method, RouteType routeType, RouteHandler handler) {
        this.method = method;
        this.path = path;
        this.handler = handler;
        this.routeType = routeType;
    }

	public HttpMethod getMethod() {
        return method;
    }
    
    public RouteType getType() {
    	return routeType;
    }

    public String getPath() {
        return path;
    }

    public RouteHandler getHandler() {
        return handler;
    }
    
	public interface RouteHandler {
	    Object handle(Request request, Response response) throws Exception;
	}
    
}
