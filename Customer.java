public class Customer {
    private final int arrivalTime;
    private final int serviceTime;
    private boolean served;
    private boolean leftQueue;

    public Customer(int arrivalTime, int serviceTime) {
        this.arrivalTime = arrivalTime;
        this.serviceTime = serviceTime;
        this.served = false;  // Initially, the customer is not served.
        this.leftQueue = false;  // Initially, the customer hasn't left the queue.
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getServiceTime() {
        return serviceTime;
    }

    public boolean isServed() {
        return served;
    }

    public void setServed(boolean served) {
        this.served = served;
    }

    public boolean isLeftQueue() {
        return leftQueue;
    }

    public void setLeftQueue(boolean leftQueue) {
        this.leftQueue = leftQueue;
    }
}
