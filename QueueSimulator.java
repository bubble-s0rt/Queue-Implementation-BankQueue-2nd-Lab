import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class QueueSimulator {
    private final int simulationTime;  // In minutes
    private final BankQueue bankQueue;
    private final GroceryQueues groceryQueues;
    
    // BankQueue counters
    private final AtomicInteger totalBankCustomers;
    private final AtomicInteger bankCustomersServed;
    private final AtomicInteger bankCustomersLeft;
    private final AtomicInteger totalBankServiceTime;
    
    // GroceryQueues counters
    private final AtomicInteger totalGroceryCustomers;
    private final AtomicInteger groceryCustomersServed;
    private final AtomicInteger groceryCustomersLeft;
    private final AtomicInteger totalGroceryServiceTime;
    
    private final Random random;
    private final int scaleFactor = 10;  // Scaling factor for faster simulation

    public QueueSimulator(int simulationTime, int numberOfTellers, int maxQueueLength, int numberOfCashiers, int groceryMaxQueueLength) {
        this.simulationTime = simulationTime;
        this.bankQueue = new BankQueue(numberOfTellers, maxQueueLength);
        this.groceryQueues = new GroceryQueues(numberOfCashiers, groceryMaxQueueLength);
        
        // Initialize counters for BankQueue
        this.totalBankCustomers = new AtomicInteger(0);
        this.bankCustomersServed = new AtomicInteger(0);
        this.bankCustomersLeft = new AtomicInteger(0);
        this.totalBankServiceTime = new AtomicInteger(0);
        
        // Initialize counters for GroceryQueues
        this.totalGroceryCustomers = new AtomicInteger(0);
        this.groceryCustomersServed = new AtomicInteger(0);
        this.groceryCustomersLeft = new AtomicInteger(0);
        this.totalGroceryServiceTime = new AtomicInteger(0);
        
        this.random = new Random();
    }

    public void startSimulation() {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(bankQueue.getNumberOfTellers() + groceryQueues.getNumberOfCashiers() + 2);

        // Task for customer arrival in BankQueue
        executor.scheduleAtFixedRate(() -> {
            int arrivalTime = (int) (System.currentTimeMillis() / 1000);
            int serviceTime = random.nextInt(241) + 60;  // Service time between 60s to 300s
            Customer customer = new Customer(arrivalTime, serviceTime);
            if (bankQueue.addCustomer(customer)) {
                totalBankCustomers.incrementAndGet();
            } else {
                bankCustomersLeft.incrementAndGet();
            }
        }, 0, (random.nextInt(41) + 20) * scaleFactor, TimeUnit.MILLISECONDS);

        // Task for customer arrival in GroceryQueues
        executor.scheduleAtFixedRate(() -> {
            int arrivalTime = (int) (System.currentTimeMillis() / 1000);
            int serviceTime = random.nextInt(241) + 60;
            Customer customer = new Customer(arrivalTime, serviceTime);
            if (groceryQueues.addCustomer(customer)) {
                totalGroceryCustomers.incrementAndGet();
            } else {
                groceryCustomersLeft.incrementAndGet();
            }
        }, 0, (random.nextInt(41) + 20) * scaleFactor, TimeUnit.MILLISECONDS);

        // Task for serving customers in BankQueue
        for (int i = 0; i < bankQueue.getNumberOfTellers(); i++) {
            executor.scheduleAtFixedRate(() -> {
                Customer customer = bankQueue.getNextCustomer();
                if (customer != null) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(customer.getServiceTime() * scaleFactor);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    totalBankServiceTime.addAndGet(customer.getServiceTime());
                    bankCustomersServed.incrementAndGet();
                }
            }, 0, 1, TimeUnit.MILLISECONDS);
        }

        // Task for serving customers in GroceryQueues
        for (int i = 0; i < groceryQueues.getNumberOfCashiers(); i++) {
            int cashierIndex = i;
            executor.scheduleAtFixedRate(() -> {
                Customer customer = groceryQueues.getNextCustomer(cashierIndex);
                if (customer != null) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(customer.getServiceTime() * scaleFactor);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    totalGroceryServiceTime.addAndGet(customer.getServiceTime());
                    groceryCustomersServed.incrementAndGet();
                }
            }, 0, 1, TimeUnit.MILLISECONDS);
        }

        // Task to end the simulation
        executor.schedule(() -> {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
            }
            printResults();
        }, simulationTime * 60L * scaleFactor, TimeUnit.MILLISECONDS);
    }

    private void printResults() {
        System.out.println("Simulation Results:");
        
        // BankQueue results
        System.out.println("\n--- BankQueue Results ---");
        System.out.println("Total customers arrived: " + totalBankCustomers.get());
        System.out.println("Total customers served: " + bankCustomersServed.get());
        System.out.println("Total customers left without being served: " + bankCustomersLeft.get());
        System.out.println("Average service time: " + (bankCustomersServed.get() > 0 ? (totalBankServiceTime.get() / bankCustomersServed.get()) / scaleFactor : 0) + " seconds");
        
        // GroceryQueues results
        System.out.println("\n--- GroceryQueues Results ---");
        System.out.println("Total customers arrived: " + totalGroceryCustomers.get());
        System.out.println("Total customers served: " + groceryCustomersServed.get());
        System.out.println("Total customers left without being served: " + groceryCustomersLeft.get());
        System.out.println("Average service time: " + (groceryCustomersServed.get() > 0 ? (totalGroceryServiceTime.get() / groceryCustomersServed.get()) / scaleFactor : 0) + " seconds");
        
        System.out.println("\nTotal simulation time: " + simulationTime + " minutes");
    }

    public static void main(String[] args) {
        int simulationTime = 120;  // Simulation time in minutes
        int numberOfTellers = 3;
        int maxQueueLength = 5;
        int numberOfCashiers = 3;
        int groceryMaxQueueLength = 2;
        QueueSimulator simulator = new QueueSimulator(simulationTime, numberOfTellers, maxQueueLength, numberOfCashiers, groceryMaxQueueLength);
        simulator.startSimulation();
    }
}
