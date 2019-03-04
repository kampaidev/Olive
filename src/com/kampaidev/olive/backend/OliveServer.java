package com.kampaidev.olive.backend;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.kampaidev.olive.backend.netty.WebChannelInitalizer;
import com.kampaidev.olive.backend.route.PathFinder;
import com.kampaidev.olive.backend.route.Route;
import com.kampaidev.olive.backend.route.Route.RouteHandler;
import com.kampaidev.olive.backend.route.RouteType;
import com.kampaidev.olive.config.Config;
import com.kampaidev.olive.plugin.OlivePlugin;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderUtil;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

/*
 * Some of this class and other classes were referenced from
 * https://github.com/codyebberson/netty-example/
 */

public class OliveServer implements WebServer {

	private PathFinder pathFinder;
	private String name;
	private Integer port, port2;

	public OliveServer(String name, int port, boolean https) {
		this.pathFinder = new PathFinder();
		this.name = name;
		this.port = port;
		if(https) this.port2 = 443;
	}


	public PathFinder pathfinder() {
		return pathFinder;
	}

	public WebServer config(Config config) {
		// load config here
		return this;
	}

	@Override
	public WebServer start(Config config) throws InterruptedException {
		if (Epoll.isAvailable()) {
			channel(new EpollEventLoopGroup(), EpollServerSocketChannel.class);
		} else {
			channel(new NioEventLoopGroup(), NioServerSocketChannel.class);
		}
		return config(config);
	}

	@Override
	public WebServer post(String path, RouteType type, RouteHandler handler) {
		pathFinder.add(new Route(path, HttpMethod.POST, type, handler));
		return this;
	}

	@Override
	public WebServer plugin(List<OlivePlugin> plugins) {
		plugins.forEach(plugin -> {
			pathFinder.add(plugin.createRoute());
		});
		return this;
	}

	@Override
	public WebServer get(String path, RouteType type, RouteHandler handler) {
		pathFinder.add(new Route(path, HttpMethod.GET, type, handler));
		return this;
	}

	@Override
	public WebServer post(String path, RouteHandler handler) {
		return post(path, RouteType.HTML, handler);
	}

	@Override
	public WebServer get(String path, RouteHandler handler) {
		return get(path, RouteType.HTML, handler);
	}

	@Override
	public WebServer channel(final EventLoopGroup loopGroup, final Class<? extends ServerChannel> serverChannelClass)
			throws InterruptedException {
		try {

			final ServerBootstrap b = new ServerBootstrap();
			b.option(ChannelOption.SO_BACKLOG, 1024);
			b.option(ChannelOption.SO_REUSEADDR, true);
			b.group(loopGroup).channel(serverChannelClass).childHandler(new WebChannelInitalizer(this));
			b.option(ChannelOption.MAX_MESSAGES_PER_READ, Integer.MAX_VALUE);
			b.childOption(ChannelOption.ALLOCATOR, new PooledByteBufAllocator(true));
			b.childOption(ChannelOption.SO_REUSEADDR, true);
			b.childOption(ChannelOption.MAX_MESSAGES_PER_READ, Integer.MAX_VALUE);

			if (port2 == 443) {
				List<Integer> ports = Arrays.asList(port, port2);
				Collection<Channel> channels = new ArrayList<>(ports.size());
				for (int port : ports) {
					Channel serverChannel = b.bind(port).sync().channel();
					channels.add(serverChannel);
				}
				for (Channel ch : channels) {
					ch.closeFuture().sync();
				}
			} else {
				final InetSocketAddress inet = new InetSocketAddress(port);
				final Channel ch = b.bind(inet).sync().channel();
				ch.closeFuture().sync();
			}

		} finally {
			loopGroup.shutdownGracefully().sync();
		}
		return this;
	}

	@Override
	public WebServer route(Route route) {
		pathFinder.add(route);
		return this;
	}

	@Override
	public int port() {
		return port;
	}

	public void writeNotFound(final ChannelHandlerContext ctx, final FullHttpRequest request) {

		writeErrorResponse(ctx, request, HttpResponseStatus.NOT_FOUND);
	}

	public void writeInternalServerError(final ChannelHandlerContext ctx, final FullHttpRequest request) {

		writeErrorResponse(ctx, request, HttpResponseStatus.INTERNAL_SERVER_ERROR);
	}

	public void writeErrorResponse(final ChannelHandlerContext ctx, final FullHttpRequest request,
			final HttpResponseStatus status) {

		writeResponse(ctx, request, status, RouteType.PLAIN.toString(), status.reasonPhrase().toString());
	}

	public void writeResponse(final ChannelHandlerContext ctx, final FullHttpRequest request,
			final HttpResponseStatus status, final CharSequence contentType, final String content) {

		final byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
		final ByteBuf entity = Unpooled.wrappedBuffer(bytes);
		writeResponse(ctx, request, status, entity, contentType, bytes.length);
	}

	public void writeResponse(final ChannelHandlerContext ctx, final FullHttpRequest request,
			final HttpResponseStatus status, final ByteBuf buf, final CharSequence contentType,
			final int contentLength) {

		// Decide whether to close the connection or not.
		final boolean keepAlive = HttpHeaderUtil.isKeepAlive(request);

		// Build the response object.
		final FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, buf, false);

		final ZonedDateTime dateTime = ZonedDateTime.now();
		final DateTimeFormatter formatter = DateTimeFormatter.RFC_1123_DATE_TIME;

		final DefaultHttpHeaders headers = (DefaultHttpHeaders) response.headers();
		headers.set(HttpHeaderNames.SERVER, name);
		headers.set(HttpHeaderNames.DATE, dateTime.format(formatter));
		headers.set(HttpHeaderNames.CONTENT_TYPE, contentType);
		headers.set(HttpHeaderNames.CONTENT_LENGTH, Integer.toString(contentLength));

		// Close the non-keep-alive connection after the write operation is
		// done.
		if (!keepAlive) {
			ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
		} else {
			response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
			ctx.writeAndFlush(response, ctx.voidPromise());
		}
	}

	/**
	 * Writes a 100 Continue response.
	 *
	 * @param ctx
	 *            The HTTP handler context.
	 */
	public void send100Continue(final ChannelHandlerContext ctx) {
		ctx.write(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE));
	}

}
