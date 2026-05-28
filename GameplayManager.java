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
        menu.add(new MenuItem("Latte", "☕"));
        menu.add(new MenuItem("Espresso", "☕"));
        menu.add(new MenuItem("Capucino", "☕"));

        menu.add(new MenuItem("Cookie", "🍪"));
        menu.add(new MenuItem("Brownie", "🍫"));

        menu.add(new MenuItem("Green tea", "🍵"));
        menu.add(new MenuItem("Hot chocolate", "🍵"));
    }

    private void showMenu() {
        System.out.println("\n --- Menu ---");

        for (int i = 0; i < menu.size(); i++) {
            System.out.println((i+1) + ". " + menu.get(i));
        }

        System.out.println("--------\n");
    }

    public void playTutorial() {
        boolean allStagesPerfect = false;

        while (!allStagesPerfect) {
        System.out.println("\n --- Tutorial - 3 Stages ---");

        String lunaIntro = "Hey again! I want to be your first customer to prepare you before the real ones arrive. Don't be scared if anything goes wrong!";
        String lunaSuccess = "Great job! You're ready for the real customers now!";
        String lunaFail = "Don't worry, just take a deep breath and try again!";

        String garfieldIntro = "Yo, just make me something tasty, yeah? No pressure though, I'm easy!";
        String garfieldSuccess = "Yooo, that's fire! You're the cat!";
        String garfieldFail = "Eh, all good bro. Just retry, I got time.";

        String tomIntro = "Hi. Doesn't matter if it's your first or last day, I just want my coffee to be good.";
        String tomSuccess = "Impressive. You actually know what you're doing.";
        String tomFail = "That's not it. Try harder next time.";

        String[][] dialogues = {
                {lunaIntro, lunaSuccess, lunaFail},
                {garfieldIntro, garfieldSuccess, garfieldFail},
                {tomIntro, tomSuccess, tomFail}
        };

        String[] customerNames = {"Luna", "Garfield", "Tom"};
        int[] itemCounts = {1, 2, 3};

        allStagesPerfect = true;

        for (int stage = 0; stage < 3; stage++) {

            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("\n --- Stage " + (stage + 1) + " ---\n");
            List<MenuItem> customerOrder = generateRandomOrder(itemCounts[stage]);

            Customer customer = new Customer(
                    customerNames[stage],
                    customerOrder,
                    dialogues[stage][0],  // intro
                    dialogues[stage][1],  // success
                    dialogues[stage][2]   // fail
            );

            playOneRound(customer, customerOrder);

            if (!player.isTutorialCompleted()) {
                allStagesPerfect = false;
            }
        }

        if (allStagesPerfect) {
            System.out.println("\n--- All stages completed PERFECTLY! ---\n");
            player.completeTutorial();
        } else {
            System.out.println("\n--- Some stages were not perfect! Let's try again! ---\n");

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println(" Restarting tutorial...\n");

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        }
    }

    private boolean playOneRound(Customer customer, List<MenuItem> customerOrder) {
        boolean success = false;
        boolean isPerfect = false;

        while (!success) {
            customer.speakIntro();

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.print("\n" + customer.getName() + " wants: ");
            for (int i = 0; i < customerOrder.size(); i++) {
                System.out.print(customerOrder.get(i));
                if (i < customerOrder.size() - 1) {
                    System.out.print(" and ");
                }
            }
            System.out.println("\n");

            showMenu();

            Order playerOrder = new Order();

            while (true) {
                System.out.print("Select item (1-" + menu.size() + ") or 0 to submit: ");
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
                customer.speakSuccess();
                System.out.println("+5 Kitty Tips! ");
                isPerfect = true;
                success = true;

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            } else {
                System.out.println("\n Incorrect order! Try again!");
                customer.speakFail();
                System.out.println("Expected: " + customerOrder);
                System.out.println("You made: " + playerOrder.getItems());

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.out.println("\n Let's try again!\n");

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        return isPerfect;
    }

    private boolean checkOrder(List<MenuItem> customerOrder, Order playerOrder) {
        List<MenuItem> playerItems = playerOrder.getItems();

        if (playerItems.size() != customerOrder.size()) {
            return false;
        }

        return playerItems.containsAll(customerOrder);
    }

    private List<MenuItem> generateRandomOrder(int itemCount) {
        List<MenuItem> order = new ArrayList<>();

        for (int i = 0; i < itemCount; i++) {
            int randomIndex = (int) (Math.random() * menu.size());
            MenuItem randomItem = menu.get(randomIndex);
            order.add(randomItem);
        }

        return order;
    }
}