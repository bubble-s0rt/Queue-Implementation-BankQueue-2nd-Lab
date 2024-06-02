public class Server {
    private int serviceTimeLeft;

    public Server() {
        this.serviceTimeLeft = 0;
    }

    public boolean isBusy() {
        return serviceTimeLeft > 0;
    }

    public void serveCustomer(int serviceTime) {
        this.serviceTimeLeft = serviceTime;
    }

    public void work() {
        if (serviceTimeLeft > 0) {
            serviceTimeLeft--;
        }
    }
}
