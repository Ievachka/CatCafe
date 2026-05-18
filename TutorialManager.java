import java.util.List;
import java.util.Scanner;

public class TutorialManager {

    private Luna luna;
    private Boss boss;
    private Player player;
    private Scanner scanner;


    public TutorialManager() {
        this.luna = new Luna();
        this.boss = new Boss();
        this.player = new Player();
        this.scanner = new Scanner(System.in);
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

        while (luna.hasMoreDialogs()) {
            luna.speak();
            luna.nextDialog();
        }

        System.out.println("\n --- Boss arrives ---\n");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        handleBossDialog();

        if (player.isTutorialCompleted()) {
            return;
        }

        if (!player.isTutorialCompleted()) {
            return;
        }

        for (int i = 0; i < 3; i++) {
            boss.speak();
            boss.nextDialog();
        }

        handleBossSecondChoice();
        if (player.isTutorialCompleted()) {
            return;
        }

        while (boss.hasMoreDialogs()) {
            boss.speak();
            boss.nextDialog();
        }

        player.completeTutorial();

    }

    private void handleBossDialog() {
        Dialog bossDialog = boss.getCurrentDialog();
        boss.speak();

        if (bossDialog.hasChoices()) {
            List<String> choices = bossDialog.getChoices();
            for (int i = 0; i < choices.size(); i++) {
                System.out.println((i + 1) + ". " + choices.get(i));
            }

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

                boss.nextDialog();
            } else {
                System.out.println("\nOh, well goodbye then! See you next time!");

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                player.incompleteTutorial();
                return;
            }
        }
    }

    private void handleBossSecondChoice() {
        Dialog bossDialog = boss.getCurrentDialog();
        boss.speak();

        if (bossDialog.hasChoices()) {
            List<String> choices = bossDialog.getChoices();
            for (int i = 0; i < choices.size(); i++) {
                System.out.println((i + 1) + ". " + choices.get(i));
            }

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

                boss.nextDialog();
            } else {
                System.out.println("\nThat's unfortunate. Goodbye!");

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                player.incompleteTutorial();
                return;
            }
        }
    }
}