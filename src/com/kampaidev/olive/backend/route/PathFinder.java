package com.kampaidev.olive.backend.route;

import java.util.Stack;

import io.netty.handler.codec.http.HttpMethod;

public class PathFinder {
	
    private Stack<Route> routes = new Stack<>();
    
    public PathFinder add(Route route) {
    	routes.add(route);
    	return this;
    }
    
    public PathFinder remove(Route route) {
    	routes.remove(route);
    	return this;
    }

    public Route find(HttpMethod method, String path) {
    	return routes.stream().filter(route-> route.getMethod().equals(method) && route.getPath().equals(path)).findFirst().orElse(null);
    }
    
}
