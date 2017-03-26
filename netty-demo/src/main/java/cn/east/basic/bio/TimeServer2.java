package cn.east.basic.bio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @ClassName: TimeServer2
 * @Description: 伪异步IO创建的TimeServer
 * 				<p>将客户端socket 封装成一个Task投递到后端的线程池做处理，
 * 				JDK的线程池维护一个消息队列和N个活跃的线程，因此它的资源占用是可控的，
 * 				无论多少个客户端并发访问，都不会导致资源的耗尽和宕机。</p>
 * @author East.F
 * @date 2017年3月26日 下午9:43:57
 *
 */
public class TimeServer2 {

	public static void main(String[] args) throws IOException {
		int port=8080;
		if(args!=null&args.length>0){
			try {
				port=Integer.parseInt(args[0]);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		ServerSocket server=null;
		TimeServerHandlerExecutePool singleExecute=new TimeServerHandlerExecutePool(50, 1000);
		try {
			server=new ServerSocket(port);
			System.out.println("The time server is start in port:"+port);
			Socket socket=null;
			while (true) {
				socket=server.accept();
				singleExecute.excute(new TimeServerHandler(socket));
			}
		}finally{
			if(server!=null){
				System.out.println("The time server close");
				server.close();
				server=null;
			}
		}
	}
}
