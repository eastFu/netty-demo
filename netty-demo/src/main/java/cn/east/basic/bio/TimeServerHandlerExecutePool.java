package cn.east.basic.bio;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName: TimeServerHandlerExecutePool
 * @Description: 伪异步IO 处理器线程池
 * @author East.F
 * @date 2017年3月26日 下午10:09:19
 *
 */
public class TimeServerHandlerExecutePool{

	private ExecutorService executor;
	
	public TimeServerHandlerExecutePool(int maxPoolSize,int queueSize){
		this.executor=new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(),
				maxPoolSize, 120L, TimeUnit.SECONDS, 
				new ArrayBlockingQueue<Runnable>(queueSize));
	}
	
	public  void excute(Runnable task){
		executor.execute(task);
	}
}
