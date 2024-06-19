import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BankQueue {
    private final int maxQueueLength;
    private final Queue<Customer> queue;
    private final int numberOfTellers;
    private final Lock queueLock;

    public BankQueue(int numberOfTellers, int maxQueueLength) {
        this.numberOfTellers = numberOfTellers;
        this.maxQueueLength = maxQueueLength;
        this.queue = new LinkedList<>();
        this.queueLock = new ReentrantLock();
    }

    public boolean addCustomer(Customer customer) {
        queueLock.lock();
        try {
            if (queue.size() < maxQueueLength) {
                queue.add(customer);
                return true;
            } else {
                customer.setServed(false);
                return false;
            }
        } finally {
            queueLock.unlock();
        }
    }

    public Customer getNextCustomer() {
        queueLock.lock();
        try {
            return queue.poll();
        } finally {
            queueLock.unlock();
        }
    }

    public int getQueueSize() {
        queueLock.lock();
        try {
            return queue.size();
        } finally {
            queueLock.unlock();
        }
    }

    public int getNumberOfTellers() {
        return numberOfTellers;
    }
}
