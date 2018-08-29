package per.east.netty.delimiterbasedframedecoder;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * DelimiterBasedFrameDecoder 服务端EchoServerHandler.
 */
public class EchoServerHandler extends ChannelHandlerAdapter{
    int counter =0;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String body = (String) msg;
        System.out.println("This is "+ (++counter)+" times reveive client : [" +body +"]");
        body+="$_";
        ByteBuf echo = Unpooled.copiedBuffer(body.getBytes());
        ctx.writeAndFlush(echo);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();//发生异常，关闭链路
    }
}

