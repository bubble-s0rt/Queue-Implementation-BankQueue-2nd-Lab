import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class queue {
    private static final int MIN_SERVICE_TIME = 3;
    private static final int MAX_SERVICE_TIME = 15;
    private static final int MIN_ARRIVAL_TIME = 1;
    private static final int MAX_ARRIVAL_TIME = 30;
    private static final int SIMULATION_TIME = 120;
    private static final int MAX_WAITING_TIME = 30;

    private Queue<Customer> customerQueue = new LinkedList<>();
    private Server[] servers = new Server[5];
    private Random random = new Random();

    public queue() {
        for (int i = 0; i < servers.length; i++) {
            servers[i] = new Server();
        }
    }

    public void runSimulation() {
        int currentTime = 0;

        while (currentTime < SIMULATION_TIME) {
            // custiomer comes
            if (random.nextInt(MAX_ARRIVAL_TIME - MIN_ARRIVAL_TIME + 1) + MIN_ARRIVAL_TIME == currentTime) {
                Customer newCustomer = new Customer(currentTime);
                customerQueue.add(newCustomer);
                System.out.println("Time " + currentTime + "s: Customer arrived at the queue.");
            }

            // server works
            for (int i = 0; i < servers.length; i++) {
                Server server = servers[i];
                server.work();
                if (!server.isBusy() && !customerQueue.isEmpty()) {
                    Customer customer = customerQueue.poll();
                    int waitingTime = currentTime - customer.getArrivalTime();

                    if (waitingTime <= MAX_WAITING_TIME) {
                        int serviceTime = random.nextInt(MAX_SERVICE_TIME - MIN_SERVICE_TIME + 1) + MIN_SERVICE_TIME;
                        server.serveCustomer(serviceTime);
                        System.out.println("Time " + currentTime + "s: Server " + (i + 1) + " started serving a customer. Service time: " + serviceTime + "s.");
                    } else{
                        System.out.println("Time " + currentTime + "s: Customer left the queue due to excessive wait time.");
                    }
                }
            }

          
            currentTime++;

        
            try {
                Thread.sleep(1000); 
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Simulation interrupted.");
            }
        }

        System.out.println("Simulation completed.");
    }

    public static void main(String[] args) {
        queue queue = new queue();
        queue.runSimulation();
    }
}
