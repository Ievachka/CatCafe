public class MenuItem {

    private String name;
    private String emoji;
    private int coinsValue;
    private String spriteFile;   // e.g. "latte.png" - the image itself is loaded by the view layer

    public MenuItem(String name, String emoji, int coinsValue, String spriteFile) {
        this.name = name;
        this.emoji = emoji;
        this.coinsValue = coinsValue;
        this.spriteFile = spriteFile;
    }

    public String getName()       { return name; }
    public String getEmoji()      { return emoji; }
    public int getCoinsValue()    { return coinsValue; }
    public String getSpriteFile() { return spriteFile; }

    @Override
    public String toString() {
        return emoji + " " + name;
    }

    // Two items are "the same" if their names match.
    // This makes order-checking work even when the objects are different.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MenuItem)) return false;
        return name.equals(((MenuItem) o).name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}