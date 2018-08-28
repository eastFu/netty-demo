package per.east.basic.bio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @ClassName: TimeServer
 * @Description: 同步阻塞IO创建的TimeServer
 * @author East.F
 * @date 2017年3月25日 下午11:15:58
 *
 */
public class TimeServer {
	
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
		try {
			server=new ServerSocket(port);
			System.out.println("The time server is start in port:"+port);
			Socket socket=null;
			while (true) {
				socket=server.accept();
				new Thread(new TimeServerHandler(socket)).start();
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
