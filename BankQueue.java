import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class BankQueue {
    private final int maxQueueLength;
     private final Queue<Customer> queue;
       private final int numberOfTellers;
        private final Semaphore queueSemaphore;

    public BankQueue(int numberOfTellers, int maxQueueLength) {
        this.numberOfTellers = numberOfTellers;
        this.maxQueueLength = maxQueueLength;
        this.queue = new LinkedList<>();
        this.queueSemaphore = new Semaphore(1);
    }

    public boolean addCustomer(Customer customer) {
        boolean added = false;
        try {
            queueSemaphore.acquire();
            if (queue.size() < maxQueueLength) {
                queue.add(customer);
                added = true;
            } else {
                customer.setServed(false);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            queueSemaphore.release();
        }
        return added;
    }

    public Customer getNextCustomer() {
        Customer customer = null;
        try {
            queueSemaphore.acquire();
            customer = queue.poll();
        } catch (InterruptedException e) 
        {
            Thread.currentThread().interrupt();
        } finally
         {
              queueSemaphore.release();
        }
        return customer;
    }

    public int getQueueSize() {
        int size = 0;
        try {
              queueSemaphore.acquire();
            size = queue.size();
        } catch (InterruptedException e) 
        {
            Thread.currentThread().interrupt();
        } finally
         {
            queueSemaphore.release();
        }
        return size;
    }

    public int getNumberOfTellers() {
        return numberOfTellers;
    }
}
