public class Player {

    private String username;
    private boolean tutorialCompleted;

    public Player() {
        username = "";
        tutorialCompleted = true;
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