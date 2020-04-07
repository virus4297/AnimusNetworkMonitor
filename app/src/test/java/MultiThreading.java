import java.lang.Thread;

 class A extends Thread{
	public void run() {
		try {
		for (int i=0;i<1000;i++) {
			System.out.println("A\t"+i);
			//Thread.sleep(1000);
		}
		
		
		System.out.println("End of A");
	}
		catch(Exception e) {
			e.getMessage();
		}
	}
}
 class B extends Thread{
	public void run() {
		try {
		for (int i=0;i<10;i++) {
			System.out.println("B\t"+i);
			//Thread.sleep(20000);
		}
		System.out.println("End of B");
		
		}
		catch(Exception e) {
			e.getMessage();
		}
		
		
	}
}
 class C extends Thread{
	public void run() {
		try {
		for (int i=0;i<10;i++) {
			System.out.println("C\t"+i);
			//Thread.sleep(10000);
		}
		System.out.println("End of C");
		
		}
		catch(Exception e) {
			e.getMessage();
		}
		
	}
}
public class MultiThreading {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		A t1 = new A(); 
        t1.start(); 
    	B t2 = new B(); 
        t2.start();
    	C t3 = new C(); 
        t3.start();
        
	}

}
