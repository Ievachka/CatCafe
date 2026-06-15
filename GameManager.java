import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GameManager {
    private Scanner scanner;
    private List<MenuItem> menu;
    private Player player;

    public GameManager() {
        this.scanner = new Scanner(System.in);
        this.player = new Player();
        this.menu = new ArrayList<>();
        initializeMenu();
    }

    private void initializeMenu() {
        menu.add(new MenuItem("Latte", "☕", 3));
        menu.add(new MenuItem("Espresso", "☕", 2));
        menu.add(new MenuItem("Capucino", "☕", 3));

        menu.add(new MenuItem("Cookie", "🍪", 2));
        menu.add(new MenuItem("Brownie", "🍫", 4));

        menu.add(new MenuItem("Green tea", "🍵", 2));
        menu.add(new MenuItem("Hot chocolate", "🍵", 4));
    }

    public void start () {
        System.out.println("Please enter your username: ");
        String username = scanner.nextLine();

        player.setUsername(username);

        System.out.println("\n" + username + "... what a lovely name.");

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String[] lunaDialogues = {
                "Welcome to the Cat Cafe!",
                "My name is Luna and I am boss assistant.",
                "Our cafe is a cozy place where you can enjoy coffee, tea, and snacks. >.<",
                "Come visit us for a warm drink and some purr-fect moments! :3",
                "\nOh, wait...",
                "You came here for a job, didn't you?",
                "Alright... stay here.",
                "I'll call our boss. He'll speak with you shortly."
        };

        int[] lunaDelays = {1000, 1500, 3000, 5000, 2000, 3000, 1000, 6000};

        for (int i = 0; i < lunaDialogues.length; i++) {
            System.out.println(lunaDialogues[i]);
            try {
                Thread.sleep(lunaDelays[i]);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("\n --- Boss arrives ---\n");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        handleBossDialog();

        if (!player.isTutorialCompleted()) {
            return;
        }

        String[] bossIntroDialogues = {
                "Okay, let's move on to your first day. We will call it a tutorial day.",
                "If you complete the tutorial, you will be able to start the next days, upgrade the cafe, and unlock new drinks and snacks!",
                "But if you do not complete the tutorial, you will need to start over... or I will just have to fire you."
        };

        int[] bossIntroDelays = {3000, 4000, 4000};

        for (int i = 0; i < bossIntroDialogues.length; i++) {
            System.out.println(bossIntroDialogues[i]);
            try {
                Thread.sleep(bossIntroDelays[i]);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        handleBossSecondChoice();
        if (!player.isTutorialCompleted()) {
            return;
        }

        String[] bossGameplayDialogues = {
                "So, your main task is to serve the customer.",
                "Read what they want, prepare the item, and give it to them.",
                "Be careful. At first, it might be easy, but later customers will order more drinks and snacks.",
                "If your order is purrfect - 100% accurate - customers will give you Kitty Tips.",
                "More Kitty Tips means more money. More money means more upgrades!",
                "Oh, here comes the customer. Be ready!"
        };

        int[] bossGameplayDelays = {2000, 3000, 4000, 3000, 3000, 2000};

        for (int i = 0; i < bossGameplayDialogues.length; i++) {
            System.out.println(bossGameplayDialogues[i]);
            try {
                Thread.sleep(bossGameplayDelays[i]);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        playTutorial();
    }

    public void playTutorial() {
        boolean allStagesPerfect = false;

        while (!allStagesPerfect) {
            System.out.println("\n --- Tutorial - 3 Stages ---");

            CharacterData luna = CharacterLibrary.getAllCharacters().get(0);
            CharacterData customer1 = getRandomCharacter();
            CharacterData customer2 = getRandomCharacter();

            String[] customerNames = {luna.getName(), customer1.getName(), customer2.getName()};
            String[][] dialogues = {
                    {luna.getIntroText(), luna.getSuccessText(), luna.getFailText()},
                    {customer1.getIntroText(), customer1.getSuccessText(), customer1.getFailText()},
                    {customer2.getIntroText(), customer2.getSuccessText(), customer2.getFailText()}
            };

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

                boolean stagePerfect = playOneRound(customer, customerOrder);

                if (!stagePerfect) {
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

    private void showMenu() {
        System.out.println("\n --- Menu ---");
        for (int i = 0; i < menu.size(); i++) {
            System.out.println((i+1) + ". " + menu.get(i));
        }
        System.out.println("--------\n");
    }

    private void handleBossDialog() {
        System.out.println("Hello, I heard from my assistant Luna that you came here for a job?");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("1. Yes! I would love to work in this adorable cafe!");
        System.out.println("2. Excuse me? No, I think there was a misunderstanding.");

        System.out.print("\nYour choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice == 1) {
            System.out.println("\nGreat! First I need to see if you are capable!");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("\nOh, well goodbye then! See you next time!");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            player.declineJob();
        }
    }

    private void handleBossSecondChoice() {
        System.out.println("Are you ready?");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("1. Yes, let's go!");
        System.out.println("2. No, sorry, I changed my mind.");

        System.out.print("\nYour choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice == 1) {
            System.out.println("\nPerfect! Let's begin!");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("\nThat's unfortunate. Goodbye!");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            player.declineJob();
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
                System.out.println("\n Perfect! 100% accurate! \n");
                customer.speakSuccess();
                isPerfect = true;
                success = true;

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            } else {
                System.out.println("\n Incorrect order! Try again! \n");
                customer.speakFail();
                System.out.println("\n Expected: " + customerOrder);
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

    private CharacterData getRandomCharacter() {
        List<CharacterData> all = CharacterLibrary.getAllCharacters();
        int randomIndex = (int)(Math.random() * all.size());
        return all.get(randomIndex);
    }

    public void playDay(int dayNumber) {
        int goal = dayNumber * 10;
        int coinsEarned = 0;
        int customerCount = 0;
        int accurateOrders = 0;

        System.out.println("\n -------------------------");
        System.out.println("  ⭐ DAY " + dayNumber + " ⭐");
        System.out.println("  GOAL: " + goal + " coins");
        System.out.println("\n -------------------------");

        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        while (coinsEarned < goal) {
            customerCount++;

            System.out.println("\n -------------------------");
            System.out.println("  CUSTOMER  " + customerCount);
            System.out.println("\n -------------------------");

            CharacterData characterData = getRandomCharacter();
            int itemCount = Math.min(1 + (dayNumber / 2), 4);
            List<MenuItem> customerOrder = generateRandomOrder(itemCount);

            Customer customer = new Customer(characterData.getName(), customerOrder, characterData.getIntroText(), characterData.getSuccessText(), characterData.getFailText());

            boolean roundAccurate = playOneRound(customer, customerOrder);

            int roundCoins = 0;
            for (MenuItem item : customerOrder) {
                roundCoins += item.getCoinsValue();
            }

            if (roundAccurate) {
                accurateOrders++;
                System.out.println(" ACCURATE! +" + roundCoins + " Coins!");
            } else {
                customer.speakFail();
                System.out.println("\n Next customer...");
            }

            coinsEarned += roundCoins;
            player.addKittyCoin(roundCoins);

            System.out.println("\n Today: " + coinsEarned + "/" + goal + " coins");

            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

            System.out.println("\n  DAY " + dayNumber + " COMPLETE! ");
            System.out.println("   Final coins: " + coinsEarned + "/" + goal);
            System.out.println("   Customers served: " + customerCount);

        if (accurateOrders == customerCount && customerCount > 0) {
            int bonus = (int) (coinsEarned * 0.5);
            player.addKittyCoin(bonus);
            System.out.println("   ALL ACCURATE! +50% BONUS: " + bonus + " coins!");
        }

        System.out.println("   Total balance: " + player.getKittyCoin());
        System.out.println("\n -------------------------");

        player.completedDay();

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    }

    public void showMainMenu() {
        boolean playing = true;
        int currentDay = 1;

        while (playing) {
            System.out.println("\n -------------------------");
            System.out.println("   MAIN MENU");
            System.out.println("   Balance: " + player.getKittyCoin());
            System.out.println("   Current Day: " + currentDay);
            System.out.println("\n -------------------------");

            System.out.println("\n1. Play Day " + currentDay);
            System.out.println("2. View Stats");
            System.out.println("3. Visit Shop");
            System.out.println("4. Quit Game");

            System.out.print("\nYour choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    playDay(currentDay);
                    currentDay++;
                    break;
                case 2:
                    showStats();
                    break;
                case 3:
                    System.out.println("\n Shop - Coming Soon!\n");
                    break;
                case 4:
                    System.out.println("\nThanks for playing! Goodbye! \n");
                    playing = false;
                    break;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    private void showStats() {
        System.out.println("\n -------------------------");
        System.out.println("  STATS");
        System.out.println("  Player: " + player.getUsername());
        System.out.println("  Total Balance: " + player.getKittyCoin());
        System.out.println("  Current Day: " + player.getDaysCompleted());
        System.out.println("\n -------------------------");
    }
}