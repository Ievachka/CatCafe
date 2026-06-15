public class MenuItem {

    private String name;
    private String emoji;
    private int coinsValue;

    public MenuItem (String name, String emoji, int coinsValue) {
        this.name = name;
        this.emoji = emoji;
        this.coinsValue = coinsValue;
    }

    public int getCoinsValue() {
        return coinsValue;
    }

    @Override
    public String toString() {
        return emoji + " " + name;
    }
}
