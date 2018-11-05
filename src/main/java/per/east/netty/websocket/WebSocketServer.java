package per.east.netty.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;

public class WebSocketServer {


    public void run(int port) throws Exception{
        EventLoopGroup boosGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(boosGroup,workerGroup).channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) {
                    ChannelPipeline pipeline = socketChannel.pipeline();
                    pipeline.addLast("http-codec",new HttpServerCodec());
                    pipeline.addLast("aggregator",new HttpObjectAggregator(65536));
                    pipeline.addLast("http-chunked",new ChunkedWriteHandler());
                    pipeline.addLast("handler",new WebsocketServerHandler());

                }
            });

            Channel ch = b.bind(port).sync().channel();
            System.out.println("web socket server started at port"+ port);
            System.out.println("open your browser and navigate to http://localhost:"+port);
            ch.closeFuture().sync();

        }catch (Exception e){
            boosGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        int port = 9527;
        if(args!=null && args.length>0){
            try {
                port = Integer.parseInt(args[0]);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        new WebSocketServer().run(port);
    }
}
