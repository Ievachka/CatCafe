import java.util.Scanner;

public class CatCafe {

    public static void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {}
    }
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.print("Please enter your username: ");
        String username = scanner.nextLine();
        System.out.println("\n" + username + "... what a lovely name.");
        sleep(1500);
        System.out.println("Welcome to the Cat Cafe!");
        System.out.println("Our cafe is a cozy place where you can enjoy coffee, tea, and snacks. >.<");
        System.out.println("Come visit us for a warm drink and some purr-fect moments! :3 ");
        System.out.println("\nOh, wait...");
        sleep(1200);
        System.out.println("You came here for a job, didn’t you?");
        sleep(1500);
        System.out.println("Alright... stay here.");
        sleep(1200);
        System.out.println("I’ll call our boss. He’ll speak with you shortly.");
        sleep(1500);


        scanner.close();
    }
}