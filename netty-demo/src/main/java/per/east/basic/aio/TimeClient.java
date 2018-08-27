package per.east.basic.aio;

public class TimeClient {
	
	public static void main(String[] args) {
		int port=8765;
		if(args!=null&&args.length>0){
			try {
				port=Integer.parseInt(args[0]);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		new Thread(new AsyncTimeClientHandler("127.0.0.1",port), "TimeClient-001").start();
	}
}
