package per.east.netty.delimiterbasedframedecoder;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * DelimiterBasedFrameDecoder 服务端.
 */
public class EchoServer {

    public void bind(int port)throws Exception{
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b =new ServerBootstrap();
            b.group(bossGroup,workerGroup).channel(NioServerSocketChannel.class).option(ChannelOption.SO_BACKLOG,100)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    ByteBuf delimiter = Unpooled.copiedBuffer("$_".getBytes());
                    socketChannel.pipeline().addLast(new DelimiterBasedFrameDecoder(1024,delimiter));
                    socketChannel.pipeline().addLast(new StringDecoder());
                    socketChannel.pipeline().addLast(new EchoServerHandler());
                }
            });

            //绑定端口，同步等待成功
            ChannelFuture f = b.bind(port).sync();
            //等待服务端监听端口关闭
            f.channel().closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
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
        new EchoServer().bind(port);
    }
}
