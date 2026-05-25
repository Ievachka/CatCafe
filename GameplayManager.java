import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GameplayManager {
    private Scanner scanner;
    private List<MenuItem> menu;
    private Player player;

    public GameplayManager(Scanner scanner, Player player) {
        this.scanner = scanner;
        this.player = player;
        this.menu = new ArrayList<>();
        initializeMenu();
    }

    private void initializeMenu() {
        menu.add(new MenuItem("Latte", "☕\uFE0F"));
        menu.add(new MenuItem("Cookie", "\uD83C\uDF6A"));
        menu.add(new MenuItem("Green tea", "\uD83C\uDF75"));
        menu.add(new MenuItem("Strawberry cupcake", "\uD83E\uDDC1"));
    }

    private void showMenu() {
        System.out.println("\n --- Menu ---");

        for (int i = 0; i < menu.size(); i++) {
            System.out.println((i+1) + ". " + menu.get(i));
        }

        System.out.println("--------\n");
    }

    public void playTutorial() {
        System.out.println("\n --- Tutorial gameplay --- \n");

        List<MenuItem> customerOrder = new ArrayList<>();
        customerOrder.add(menu.get(0)); // Latte

        Customer customer = new Customer("Luna", customerOrder);
        boolean success = false;

        while(!success) {

            customer.speak();
            showMenu();

            Order playerOrder = new Order();

            while (true) {
                System.out.print("Select item (1-4) or 0 to submit: ");
                int choice = scanner.nextInt();
                scanner.nextLine();

                if (choice == 0) {
                    break;
                }

                if (choice >= 1 && choice <= menu.size()) {
                    MenuItem selectedItem = menu.get(choice - 1);
                    playerOrder.addItem(selectedItem);
                    System.out.println("Added: " + selectedItem);
                } else {
                    System.out.println("Invalid choice! Try again.");
                }
            }

            if (checkOrder(customerOrder, playerOrder)) {
                System.out.println("\n Perfect! 100% accurate!");
                System.out.println("+5 Kitty Tips! ");
                player.completeTutorial();
                success = true;
            } else {
                System.out.println("\n Incorrect order! Try again!");
                System.out.println("Expected: " + customerOrder);
                System.out.println("You made: " + playerOrder.getItems());

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("\n Let's try again! \n");

                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }
    private boolean checkOrder(List<MenuItem> customerOrder, Order playerOrder) {
        List<MenuItem> playerItems = playerOrder.getItems();

        if (playerItems.size() != customerOrder.size()) {
            return false;
        }

        return playerItems.containsAll(customerOrder);
    }
}

