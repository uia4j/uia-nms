package uia.nms.amq;

public class AbstractTest {

    protected void pressToContinue() {
        try {
            System.out.println("Press  to continue...");
            System.in.read();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Stopped");
    }
}
