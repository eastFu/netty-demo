package per.east.netty.delimiterbasedframedecoder;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * DelimiterBasedFrameDecoder 客户端EchoClientHandler.
 */
public class EchoClientHandler extends ChannelHandlerAdapter{

    private int counter;

    static final String ECHO_REQ = "Hi , east . Welcome to Netty.$_";

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        for (int i = 0;i<100;i++){
            ctx.writeAndFlush(Unpooled.copiedBuffer(ECHO_REQ.getBytes()));
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("This is "+(++counter) +" times reveive server : ["+msg+"]");
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
