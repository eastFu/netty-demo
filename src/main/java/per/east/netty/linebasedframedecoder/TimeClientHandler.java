package per.east.netty.linebasedframedecoder;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 *  Netty 时间服务器客户端 TimeClientHandler(使用netty LineBasedFrameDecoder 解决TCP粘包)
 */
public class TimeClientHandler extends ChannelHandlerAdapter{

    private byte[] req;

    private int counter;

    public TimeClientHandler(){
        req =("QUERY TIME ORDER"+System.getProperty("line.separator")).getBytes();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //TCP连接建立成功后执行此方法
        ByteBuf message = null;
        for (int i =0;i<100;i++){
            message = Unpooled.buffer(req.length);
            message.writeBytes(req);
            ctx.writeAndFlush(message);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //服务器端返回数据的时候执行此方法
        String body = (String) msg;
        System.out.println("Now is : "+body+"; the counter is : "+ (++counter));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("Unexpected exception from downstream : "+cause.getMessage());
        ctx.close();
    }
}
