// A counter that dispenses one menu item (e.g. the Latte station).
// Pure data + geometry; the view layer (GamePanel) draws it using its sprite cache.
public class Station {
    private int x, y, width, height;
    private MenuItem item;          // what this station gives

    public Station(int x, int y, int width, int height, MenuItem item) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.item = item;
    }

    public MenuItem getItem() { return item; }
    public int getX()      { return x; }
    public int getY()      { return y; }
    public int getWidth()  { return width; }
    public int getHeight() { return height; }

    // Is the chef close enough to interact with this station?
    public boolean isPlayerNearby(int px, int py, int pSize, int range) {
        int pcx = px + pSize / 2, pcy = py + pSize / 2;
        int scx = x + width / 2,  scy = y + height / 2;
        return Math.abs(pcx - scx) < range && Math.abs(pcy - scy) < range;
    }
}