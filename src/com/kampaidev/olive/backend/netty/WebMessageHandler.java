package com.kampaidev.olive.backend.netty;

import com.kampaidev.olive.backend.OliveServer;
import com.kampaidev.olive.backend.WebServer;
import com.kampaidev.olive.backend.msg.Request;
import com.kampaidev.olive.backend.route.Route;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;

public class WebMessageHandler extends SimpleChannelInboundHandler<Object> {

	private OliveServer server;

	public WebMessageHandler(OliveServer server) {
		this.server = server;
	}

	@Override
	protected void messageReceived(ChannelHandlerContext context, Object message) throws Exception {
		if (!(message instanceof FullHttpRequest))
			return;

		final FullHttpRequest request = (FullHttpRequest) message;
		final HttpMethod method = request.method();
		final String uri = request.uri();

		final Route route = server.pathfinder().find(method, uri);
		if (route == null) {
			server.writeNotFound(context, request);
			return;
		}

		try {
			final Request requestWrapper = new Request(request);
			final Object obj = route.getHandler().handle(requestWrapper, null);
			final String content = obj == null ? "" : obj.toString();
			server.writeResponse(context, request, HttpResponseStatus.OK, route.getType().toString(), content);
		} catch (final Exception ex) {
			ex.printStackTrace();
			server.writeInternalServerError(context, request);
		}
	}

	@Override
	public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) {
		ctx.close();
	}

	@Override
	public void channelReadComplete(final ChannelHandlerContext ctx) {
		ctx.flush();
	}
}
