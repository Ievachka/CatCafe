public class Player {

    private String username;
    private boolean tutorialCompleted;
    private int daysCompleted;
    private int kittyCoin;

    public Player() {
        username = "";
        tutorialCompleted = true;
        daysCompleted = 0;
        kittyCoin = 0;
    }

    public void completedDay() {
        daysCompleted++;
    }

    public int getDaysCompleted() {
        return daysCompleted;
    }

    public int getKittyCoin() {
        return kittyCoin;
    }

    public void setKittyCoin(int amount) {
        this.kittyCoin = amount;
    }

    public void addKittyCoin(int amount) {
        this.kittyCoin += amount;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername () {
        return username;
    }

    public void completeTutorial() {
        tutorialCompleted = true;
        System.out.println("\n --- Tutorial Complete! ---");
    }

    public void incompleteTutorial() {
        tutorialCompleted = false;
        System.out.println("\n --- Tutorial Incomplete! Try again later! ---");
    }

    public boolean isTutorialCompleted() {
        return tutorialCompleted;
    }

    public void declineJob() {
        tutorialCompleted = false;
        System.out.println("\n --- Maybe next time! Goodbye! ---");
    }
}
