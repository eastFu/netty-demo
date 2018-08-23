package cn.east.basic.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

/**
 * @ClassName: MulitiplexerTimeServer
 * @Description: NIO 时间服务器
 * @author East.F
 * @date 2017年3月28日 下午3:18:04
 *
 */
public class MulitiplexerTimeServer implements Runnable{

	private Selector selector;
	
	private ServerSocketChannel serverChannel;
	
	private volatile boolean stop;
	
	/**
	 * <p>Description: 初始化多路复用器，绑定监听端口</p>
	 * @param port
	 */
	public MulitiplexerTimeServer(int port){
		try {
			selector = Selector.open();
			serverChannel =ServerSocketChannel.open();
			serverChannel.configureBlocking(false);
			serverChannel.socket().bind(new InetSocketAddress(port));
			serverChannel.register(selector, SelectionKey.OP_ACCEPT);
			System.out.println("The time server is start in port :" +port);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public void stop(){
		this.stop=true;
	}
	
	@Override
	public void run() {
		while (!stop) {
			try {
				selector.select(1000);
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
				Iterator<SelectionKey> it =selectedKeys.iterator();
				SelectionKey key =null;
                while (it.hasNext()) {
					key =it.next();
					it.remove();
					try {
						handleInput(key);
					} catch (Exception e) {
						if(key!=null){
							key.cancel();
							if(key.channel()!=null){
								key.channel().close();
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		// 多路复用器关闭后，所有注册在上面的Channel 和Pipe 等资源都会被自动去注册并关闭，所以不需要重复的释放资源
		if(selector!=null){
			try {
				selector.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void handleInput(SelectionKey key) throws IOException{
		if(key.isValid()){
			//处理新接入的请求消息
			if(key.isAcceptable()){
				// Accept the new connection
				ServerSocketChannel ssc=(ServerSocketChannel) key.channel();
				SocketChannel sc=ssc.accept();
				sc.configureBlocking(false);
				//Add the new connection to the selector
				sc.register(selector, SelectionKey.OP_READ);
			}
			
			if(key.isReadable()){
				//读取数据
				SocketChannel sc= (SocketChannel) key.channel();
				ByteBuffer readBuffer=ByteBuffer.allocate(1024);
				int readBytes = sc.read(readBuffer);
				if(readBytes >0){
					readBuffer.flip();
					byte[] bytes=new byte[readBuffer.remaining()];
					readBuffer.get(bytes);
					String body= new String(bytes, "UTF-8");
					System.out.println("The time server receive order : "+ body);
					String currentTime="QUERY TIME ORDER".equalsIgnoreCase(body) ? 
							new Date(System.currentTimeMillis()).toString(): "BAD ORDER";
					doWrite(sc, currentTime);
				}else if(readBytes <0){
					// 对端链路关闭
					key.cancel();
					sc.close();
				}else{
					//读到0 字节，忽略
				}
			}
		}
	}
	
	private void doWrite(SocketChannel channel,String response) throws IOException{
		if(response!=null&& response.trim().length()>0){
			byte[] bytes=response.getBytes();
			ByteBuffer writeBuffer =ByteBuffer.allocate(bytes.length);
			writeBuffer.put(bytes);
			writeBuffer.flip();
			channel.write(writeBuffer);
		}
	}

}
