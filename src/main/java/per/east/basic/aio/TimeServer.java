package per.east.basic.aio;

public class TimeServer {

    public static void main(String[] args) {
        int port=8765;
        if(args!=null&&args.length>0){
            try {
                port=Integer.parseInt(args[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        AsyncTimeServerHandler timeServer = new AsyncTimeServerHandler(port);
        new Thread(timeServer,"AIO-AsyncTimeServerHandler-001").start();
    }
}
