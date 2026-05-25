public class MenuItem {

    private String name;
    private String emoji; // later -> sprite

    public MenuItem (String name, String emoji) {
        this.name = name;
        this.emoji = emoji;
    }

    public String getName() {
        return name;
    }

    public String getEmoji() {
        return emoji;
    }

    @Override
    public String toString() {
        return emoji + " " + name;
    }
}

