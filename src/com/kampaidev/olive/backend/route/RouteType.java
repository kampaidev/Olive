package com.kampaidev.olive.backend.route;

public enum RouteType {

	HTML("text/html"), PLAIN("text/plain"), JSON("application/json");
	
	String name;
	
	RouteType(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return this.name + "; charset=UTF-8";
	}
	
	
}
