package com.kampaidev.olive.backend.msg;

import java.nio.charset.StandardCharsets;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

public class Response {
	
    private FullHttpResponse response;
    
    public Response(FullHttpResponse request) {
        this.response = request;
    }

    public String body() {
        return response.content().toString(StandardCharsets.UTF_8);
    }
    
}
