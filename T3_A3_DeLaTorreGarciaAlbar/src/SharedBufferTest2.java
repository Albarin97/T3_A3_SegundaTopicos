import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class SynchronizedBuffer implements Buffer{

	private int buffer = -1;
	private boolean occupied = false;
	
	@Override
	public synchronized void blockingPut(int value) throws InterruptedException {
		// TODO Auto-generated method stub
		while(occupied) {
			System.out.println("Producer tries to write.");
			displayState("Buffer full. Producer waits.");
			wait();
		}
		buffer = value;
		
		occupied = true;
		
		displayState("Producer wirtes "+buffer);
		
		notifyAll();
	}

	@Override
	public synchronized int blockingGet() throws InterruptedException {
		// TODO Auto-generated method stub
		while(!occupied) {
			System.out.println("Consumer tries to read.");
			displayState("Buffer empty, Consumer waits.");
			wait();
		}
		
		occupied = false;
		displayState("Consumer reads"+buffer);
		notifyAll();
		
		return buffer;
	}
	
	private synchronized void displayState(String operation) {
		System.out.printf("%-40s%d\t\t%b%n%n", operation, buffer, occupied);
	}
	
	
}

public class SharedBufferTest2 {
public static void main(String[] args) {
	ExecutorService es = Executors.newCachedThreadPool();
	
	Buffer sharedLocation = new SynchronizedBuffer();
	
	System.out.printf("%-40s%s\t\t%s%n%-40s%s%n%n", "Operation","Buffer", "Occupied"
			, "---------", "------\t\t--------");
	
	es.execute(new Producer(sharedLocation));
	es.execute(new Consumer(sharedLocation));
	
	es.shutdown();
	try {
		es.awaitTermination(1, TimeUnit.MINUTES);
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
}

}
