package cn.east.basic.bio;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * @ClassName: TimeClient
 * @Description: 同步阻塞式IO创建的TimeClient
 * @author East.F
 * @date 2017年3月25日 下午11:27:38
 */
public class TimeClient {

	public static void main(String[] args) {
		int port=8080;
		if(args!=null&&args.length>0){
			try {
				port=Integer.parseInt(args[0]);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		Socket socket=null;
		BufferedReader in =null;
		PrintWriter out=null;
		try {
			socket=new Socket("127.0.0.1",port);
			in=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out=new PrintWriter(socket.getOutputStream(),true);
			out.println("QUERY TIME ORDER");
			System.out.println("Send order 2 server succeed.");
			String resp=in.readLine();
			System.out.println("Now is :" +resp);
		} catch (Exception e) {
			//不处理
		}finally {
			if(out!=null){
				out.close();
				out=null;
			}
			if(in!=null){
				try {
					in.close();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
				in=null;
			}
			if(socket!=null){
				try {
					socket.close();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
				socket=null;
			}
			
		}
		
	}
}
