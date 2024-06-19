import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class QueueSimulator {
    private final int simulationTime; // in minutes
    private final BankQueue bankQueue;
    private int totalCustomers;
    private int customersServed;
    private int customersLeft;
    private int totalServiceTime;
    private final Random random;

    public QueueSimulator(int simulationTime, int numberOfTellers, int maxQueueLength) {
        this.simulationTime = simulationTime;
        this.bankQueue = new BankQueue(numberOfTellers, maxQueueLength);
        this.totalCustomers = 0;
        this.customersServed = 0;
        this.customersLeft = 0;
        this.totalServiceTime = 0;
        this.random = new Random();
    }

    public void startSimulation() {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(bankQueue.getNumberOfTellers() + 1);

        // Customer arrival generator
        executor.scheduleAtFixedRate(() -> {
            int arrivalTime = (int) (System.currentTimeMillis() / 1000);
            int serviceTime = random.nextInt(241) + 60; // 60 to 300 seconds
            Customer customer = new Customer(arrivalTime, serviceTime);
            if (bankQueue.addCustomer(customer)) {
                totalCustomers++;
            } else {
                customersLeft++;
            }
        }, 0, random.nextInt(41) + 20, TimeUnit.SECONDS); // 20 to 60 seconds

        // Tellers serving customers
        for (int i = 0; i < bankQueue.getNumberOfTellers(); i++) {
            executor.scheduleAtFixedRate(() -> {
                Customer customer = bankQueue.getNextCustomer();
                if (customer != null) {
                    try {
                        TimeUnit.SECONDS.sleep(customer.getServiceTime());
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    totalServiceTime += customer.getServiceTime();
                    customersServed++;
                }
            }, 0, 1, TimeUnit.SECONDS);
        }

        // Stop the simulation after the specified time
        executor.schedule(() -> {
            executor.shutdown();
            printResults();
        }, simulationTime, TimeUnit.MINUTES);
    }

    private void printResults() {
        System.out.println("Simulation Results:");
        System.out.println("Total customers arrived: " + totalCustomers);
        System.out.println("Total customers served: " + customersServed);
        System.out.println("Total customers left without being served: " + customersLeft);
        System.out.println("Average service time: " + (customersServed > 0 ? totalServiceTime / customersServed : 0) + " seconds");
    }

    public static void main(String[] args) {
        int simulationTime = 120; // in minutes
        int numberOfTellers = 3;
        int maxQueueLength = 5;
        QueueSimulator simulator = new QueueSimulator(simulationTime, numberOfTellers, maxQueueLength);
        simulator.startSimulation();
    }
}
