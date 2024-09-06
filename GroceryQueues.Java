import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;

public class GroceryQueues {
    private final int maxQueueLength;
    private final List<Queue<Customer>> queues;
    private final int numberOfCashiers;
    private final Semaphore queueSemaphore;

    public GroceryQueues(int numberOfCashiers, int maxQueueLength) {
        this.numberOfCashiers = numberOfCashiers;
        this.maxQueueLength = maxQueueLength;
        this.queues = new ArrayList<>();
        for (int i = 0; i < numberOfCashiers; i++) {
            queues.add(new LinkedList<>());
        }
        this.queueSemaphore = new Semaphore(1);
    }

    public boolean addCustomer(Customer customer) {
        boolean added = false;
        try {
            queueSemaphore.acquire();
            int minQueueIndex = getMinQueue();
            if (queues.get(minQueueIndex).size() < maxQueueLength) {
                queues.get(minQueueIndex).add(customer);
                added = true;
            } else {
                customer.setLeftQueue(true);  // Customer leaves the queue if no space.
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            queueSemaphore.release();
        }
        return added;
    }

    public Customer getNextCustomer(int cashierIndex) {
        Customer customer = null;
        try {
            queueSemaphore.acquire();
            customer = queues.get(cashierIndex).poll();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            queueSemaphore.release();
        }
        return customer;
    }

    private int getMinQueue() {
        int minIndex = 0;
        int minSize = queues.get(0).size();
        for (int i = 1; i < queues.size(); i++) {
            if (queues.get(i).size() < minSize) {
                minIndex = i;
                minSize = queues.get(i).size();
            }
        }
        return minIndex;
    }
}
