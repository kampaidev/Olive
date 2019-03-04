package com.kampaidev.olive.backend.netty;

import com.kampaidev.olive.backend.OliveServer;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

public class WebChannelInitalizer extends ChannelInitializer<SocketChannel> {

	private OliveServer server;
	
	public WebChannelInitalizer(OliveServer server) {
		this.server = server;
	}
	
	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ch.config().setOption(ChannelOption.TCP_NODELAY, false);
		
        final ChannelPipeline p = ch.pipeline();
        p.addLast("decoder", new HttpRequestDecoder(4096, 8192, 8192, false));
        p.addLast("aggregator", new HttpObjectAggregator(100 * 1024 * 1024));
        p.addLast("encoder", new HttpResponseEncoder());
        p.addLast("handler", new WebMessageHandler(server));		
	}

}
