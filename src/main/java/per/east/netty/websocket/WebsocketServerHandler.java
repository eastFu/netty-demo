package per.east.netty.websocket;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WebsocketServerHandler extends SimpleChannelInboundHandler<Object> {

    private static final Logger logger = Logger.getLogger(WebsocketServerHandler.class.getName());

    private WebSocketServerHandshaker handshaker;

    @Override
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        //传统的HTTP接入
        if(o instanceof FullHttpRequest){
            handleHttpRequest(channelHandlerContext, (FullHttpRequest) o);
        }
        //WEBSOCKET 接入
        else if(o instanceof WebSocketFrame){
            handleWebsocketFrame(channelHandlerContext, (WebSocketFrame) o);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception{
        //如果解码失败，返回HTTP 异常
        if(req.getDecoderResult().isSuccess()||(!"websocket".equals(req.headers().get("Upgrade")))){
            sendHttpResponse(ctx,req,new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;
        }

        //构造握手相应返回，本机测试
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory("ws://localhost:9527/websocket",null,false);
        handshaker = wsFactory.newHandshaker(req);

        if(handshaker==null){
            WebSocketServerHandshakerFactory.sendUnsupportedWebSocketVersionResponse(ctx.channel());
        }else{
            handshaker.handshake(ctx.channel(),req);
        }
    }

    private void handleWebsocketFrame(ChannelHandlerContext ctx,WebSocketFrame frame) throws Exception{
        //判断是否是关闭链路的指令
        if(frame instanceof CloseWebSocketFrame){
            handshaker.close(ctx.channel(),((CloseWebSocketFrame) frame).retain());
        }
        //判断是否是ping消息
        if(frame instanceof PingWebSocketFrame){
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
        }
        //本例程仅支持文本消息，不支持二进制消息
        if(!(frame instanceof TextWebSocketFrame)){
            throw  new UnsupportedOperationException(String.format("%s frame types not supported",frame.getClass().getName()));
        }

        //返回应答消息
        String request = ((TextWebSocketFrame) frame).text();
        if(logger.isLoggable(Level.FINE)){
            logger.fine(String.format("%s reveived %s",ctx.channel(),request));
        }
        ctx.channel().write(new TextWebSocketFrame(request+",欢迎使用Netty Websocket服务，现在时刻："+new Date().toString()));
    }

    private static void sendHttpResponse(ChannelHandlerContext ctx , FullHttpRequest req, FullHttpResponse res){
        //返回应答给客户端
        if(res.getStatus().code()!=200){
            ByteBuf buf = Unpooled.copiedBuffer(res.getStatus().toString(), CharsetUtil.UTF_8);
            res.content().writeBytes(buf);
            buf.release();
            HttpHeaders.setContentLength(res,res.content().readableBytes());
        }

        //如果是非keep-alive，关闭连接
        ChannelFuture f = ctx.channel().writeAndFlush(res);
        if(!HttpHeaders.isKeepAlive(req)||res.getStatus().code()!=200){
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
