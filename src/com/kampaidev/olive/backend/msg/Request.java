package com.kampaidev.olive.backend.msg;

import java.nio.charset.StandardCharsets;

import io.netty.handler.codec.http.FullHttpRequest;

public class Request {
	
    private FullHttpRequest request;
    
    public Request(final FullHttpRequest request) {
        this.request = request;
    }

    public String body() {
        return request.content().toString(StandardCharsets.UTF_8);
    }
}
    