package dev.minceraft.sonus.web.adapter.pipeline;

import dev.minceraft.sonus.common.participant.builtin.ISonusPlayer;
import dev.minceraft.sonus.web.adapter.WebAdapter;
import dev.minceraft.sonus.web.adapter.connection.WebSocketConnection;
import dev.minceraft.sonus.web.adapter.util.HttpErrorException;
import dev.minceraft.sonus.web.adapter.util.HttpRequestUtil;
import dev.minceraft.sonus.web.adapter.util.WebTokenUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

import static dev.minceraft.sonus.common.SonusConstants.PERMISSION_CONNECT_WEB;
import static dev.minceraft.sonus.web.adapter.WebServer.HTTP_AGGREGATOR;
import static dev.minceraft.sonus.web.adapter.WebServer.HTTP_SOCKET_CODEC;
import static dev.minceraft.sonus.web.adapter.WebServer.HTTP_SOCKET_FRAMER;
import static dev.minceraft.sonus.web.adapter.WebServer.HTTP_SOCKET_HANDLER;
import static dev.minceraft.sonus.web.adapter.WebServer.HTTP_SOCKET_PROTOCOL;
import static dev.minceraft.sonus.web.adapter.WebServer.HTTP_SOCKET_SHAKER;

public class WebSocketHandshaker extends ChannelInboundHandlerAdapter {

    private static final String API_TOKEN_PARAM_PREFIX = "/socket/";

    private static final int TOKEN_LENGTH = 32;

    private final WebAdapter adapter;

    public WebSocketHandshaker(WebAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (!(msg instanceof HttpRequest request)) {
            HttpRequestUtil.doHttpClose(ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR);
            return;
        }
        // ensure it's a websocket request
        if (!HttpRequestUtil.isWebsocketRequest(request)) {
            HttpRequestUtil.doHttpClose(ctx, HttpResponseStatus.BAD_REQUEST);
            return;
        }
        String uri = request.uri();
        try {
            int version = HttpRequestUtil.parseApiVersion(uri);
            String route = HttpRequestUtil.stripApiPrefix(uri, version);
            this.handleRequest(ctx, request, version, route);
        } catch (HttpErrorException exception) {
            HttpRequestUtil.doHttpClose(ctx, exception.getStatus());
        }
    }

    private void handleRequest(ChannelHandlerContext ctx, HttpRequest request, int version, String route) throws HttpErrorException {
        if (!route.startsWith(API_TOKEN_PARAM_PREFIX)) {
            throw new HttpErrorException(HttpResponseStatus.NOT_FOUND);
        }
        String token = route.substring(API_TOKEN_PARAM_PREFIX.length()).trim();
        if (!WebTokenUtil.isValidToken(token)) {
            throw new HttpErrorException(HttpResponseStatus.BAD_REQUEST);
        }
        ISonusPlayer player = this.adapter.getSessions().consumeToken(token);
        if (player == null) {
            throw new HttpErrorException(HttpResponseStatus.FORBIDDEN);
        }
        if (!player.hasPermission(PERMISSION_CONNECT_WEB, true)) {
            throw new HttpErrorException(HttpResponseStatus.FORBIDDEN);
        }
        if (!player.setAdapter(this.adapter)) {
            throw new HttpErrorException(HttpResponseStatus.CONFLICT);
        }

        // valid token, upgrade to websocket connection
        this.upgradeWebsocket(ctx, request, player, version);
    }

    public void upgradeWebsocket(ChannelHandlerContext ctx, HttpRequest request, ISonusPlayer player, int version) {
        WebSocketConnection connection = new WebSocketConnection(this.adapter, player, ctx.channel());
        connection.setVersion(version);
        this.adapter.getSessions().addConnection(connection);
        player.setConnected(true);

        // setup websocket handling pipeline
        ctx.pipeline()
                .addBefore(HTTP_SOCKET_SHAKER, HTTP_SOCKET_PROTOCOL,
                        new WebSocketServerProtocolHandler(request.uri(), null, true))
                .addBefore(HTTP_SOCKET_SHAKER, HTTP_SOCKET_FRAMER, new WebSocketFrameCodec())
                .addBefore(HTTP_SOCKET_SHAKER, HTTP_SOCKET_CODEC, new WebSocketSonusCodec(connection))
                .addBefore(HTTP_SOCKET_SHAKER, HTTP_SOCKET_HANDLER, new WebSocketHandler(connection));

        // remove this handler, it's only used for setting up the connection
        ctx.pipeline().remove(HTTP_SOCKET_SHAKER);

        // re-fire the initial http request to initiate the full websocket connection
        ctx.pipeline().context(HTTP_AGGREGATOR).fireChannelRead(request);

        // initialize player connection
        connection.getPlayer().setVoiceActive(true);
        connection.getAdapter().getSessions().onConnectionEstablished(connection);
    }
}
