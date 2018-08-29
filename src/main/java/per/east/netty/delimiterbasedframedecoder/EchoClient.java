package per.east.netty.delimiterbasedframedecoder;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

/**
 * DelimiterBasedFrameDecoder 客户端.
 */
public class EchoClient {

    public void connect (int port ,String host) throws Exception{
        //配置客户端NIO线程组
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b =new Bootstrap();
            b.group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY,true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            ByteBuf delimiter = Unpooled.copiedBuffer("$_".getBytes());
                            channel.pipeline().addLast(new DelimiterBasedFrameDecoder(1024,delimiter));
                            channel.pipeline().addLast(new StringDecoder());
                            channel.pipeline().addLast(new EchoClientHandler());
                        }
                    });

            //发生异步连接操作
            ChannelFuture f = b.connect(host,port);

            //等待客户端链路关闭
            f.channel().closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception{
        int port = 8765;
        if(args!=null&&args.length>0){
            try {
                port = Integer.parseInt(args[0]);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        new EchoClient().connect(port,"127.0.0.1");
    }
}
