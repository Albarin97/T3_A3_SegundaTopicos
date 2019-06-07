import java.security.SecureRandom;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

interface Buffer{
public void blockingPut(int value) throws InterruptedException;

public int blockingGet() throws InterruptedException;
}

class Producer implements Runnable{
	
	private static final SecureRandom generator = new SecureRandom();
	private final Buffer sharedLocation;
	
	public Producer(Buffer sharedLocation) {
		this.sharedLocation=sharedLocation;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		int sum = 0;
		
		for (int i = 1; i <=10 ; i++) {
			try {
				Thread.sleep(generator.nextInt(3000));
				sharedLocation.blockingPut(i);
				sum+= 1;
				//System.out.printf("\t%2d%n", sum);
			} catch (Exception e) {
				// TODO: handle exception
				Thread.currentThread().interrupt();
			}
		}
		System.out.printf("Producer done producing%nTerminating Producer%n");
	}
	
}

class Consumer implements Runnable{

	private static final SecureRandom generator = new SecureRandom();
	private final Buffer sharedLocation;
	
	public Consumer(Buffer sharedLocation) {
		this.sharedLocation=sharedLocation;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		int sum = 0;
		
		for (int i = 1; i <=10 ; i++) {
			try {
				Thread.sleep(generator.nextInt(3000));
				sum+=sharedLocation.blockingGet();
				//System.out.printf("\t\t\t%2d%n", sum);
			} catch (Exception e) {
				// TODO: handle exception
				Thread.currentThread().interrupt();
			}
		}
		
		System.out.printf("%n%s %d%n%s%n","Consumer read values totaling", sum, "Terminating Consumer");
	}
	
}

class UnsynchronizedBuffer implements Buffer{

	private int buffer = -1;
	
	@Override
	public void blockingPut(int value) throws InterruptedException {
		// TODO Auto-generated method stub
		System.out.printf("Producer writes\t%2d", value);
		buffer = value;
	}

	@Override
	public int blockingGet() throws InterruptedException {
		// TODO Auto-generated method stub
		System.out.printf("Consumer reads\t%2d", buffer);
		return buffer;
	}
	
}

public class SharedBufferTest {
public static void main(String[] args) {
	ExecutorService es = Executors.newCachedThreadPool();
	
	Buffer sharedLocation = new UnsynchronizedBuffer();
	
	System.out.println("Action\t\tValue\t Sun of Produced\tSunofConsumed");
	System.out.printf("------\t\t-----\t---------------\t---------------%n%n");
	
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
