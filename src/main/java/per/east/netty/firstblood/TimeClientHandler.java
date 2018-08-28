package per.east.netty.firstblood;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 *  Netty 时间服务器客户端 TimeClientHandler
 */
public class TimeClientHandler extends ChannelHandlerAdapter{
    private final ByteBuf firstMessage;

    public TimeClientHandler(){
        byte[] req ="QUERY TIME ORDER".getBytes();
        firstMessage = Unpooled.buffer(req.length);
        firstMessage.writeBytes(req);
        System.out.println("client request time...");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //TCP连接建立成功后执行此方法
        ctx.writeAndFlush(firstMessage);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //服务器端返回数据的时候执行此方法
        ByteBuf buf = (ByteBuf) msg;
        byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);
        String body = new String(req,"UTF-8");
        System.out.println("Now is : "+body);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("Unexpected exception from downstream : "+cause.getMessage());
        ctx.close();
    }
}
