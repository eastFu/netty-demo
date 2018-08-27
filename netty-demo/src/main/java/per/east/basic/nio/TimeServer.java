package per.east.basic.nio;


/**
 * @ClassName: TimeServer
 * @Description: NIO创建TimeServer
 * @author East.F
 * @date 2017年3月28日 下午2:39:17
 */
public class TimeServer {

	public static void main(String[] args) {
		int port=8080;
		if(args!=null&&args.length>0){
			try {
				port=Integer.parseInt(args[0]);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		MulitiplexerTimeServer timeServer =new MulitiplexerTimeServer(port);
		new Thread(timeServer, "NIO-MulitiplexerTimeServer-001").start();
	}
}
