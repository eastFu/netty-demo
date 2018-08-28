package per.east.basic.aio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;

public class AsyncTimeClientHandler implements Runnable,CompletionHandler<Void,AsyncTimeClientHandler>{

    private AsynchronousSocketChannel client;

    private String host;

    private int port;

    private CountDownLatch latch;

    public AsyncTimeClientHandler(String host,int port){
        this.host = host;
        this.port = port;
        try {
            client = AsynchronousSocketChannel.open();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        latch = new CountDownLatch(1);
        client.connect(new InetSocketAddress(this.host,this.port),this,this);
        try {
            latch.await();
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            client.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void completed(Void result, AsyncTimeClientHandler attachment) {
        byte[] req = "QUERY TIME ORDER".getBytes();
        ByteBuffer writeBuffer = ByteBuffer.allocate(req.length);
        writeBuffer.put(req);
        writeBuffer.flip();
        client.write(writeBuffer, writeBuffer, new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer result, ByteBuffer attachment) {
                if(attachment.hasRemaining()){
                    client.write(attachment,attachment,this);
                }else{
                    ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                    client.read(readBuffer, readBuffer, new CompletionHandler<Integer, ByteBuffer>() {
                        @Override
                        public void completed(Integer result, ByteBuffer attachment) {
                            attachment.flip();
                            byte[] bytes = new byte[attachment.remaining()];
                            attachment.get(bytes);
                            String body ;
                            try {
                                body = new String(bytes,"UTF-8");
                                System.out.println("Now is : "+body);
                                latch.countDown();
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void failed(Throwable exc, ByteBuffer attachment) {
                            try {
                                client.close();
                                latch.countDown();
                            }catch (Exception e){
                                //ignore on close
                            }
                        }
                    });
                }
            }

            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
                try {
                    client.close();
                    latch.countDown();
                }catch (Exception e){
                    //ignore on close
                }
            }
        });

    }

    @Override
    public void failed(Throwable exc, AsyncTimeClientHandler attachment) {
        try {
            client.close();
            latch.countDown();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
