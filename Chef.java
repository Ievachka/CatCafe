import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// The walking character controlled by the player (the former GUI "Player").
// Renamed to Chef so it does not clash with the terminal Player (progress profile).
public class Chef {
    int x, y, speed, size;
    BufferedImage spriteSheet;
    BufferedImage[][] sprites;
    int currentRow = 0, currentCol = 0;
    int animationSpeed = 8, animationCounter = 0;
    boolean moveUp, moveDown, moveLeft, moveRight;

    // The tray = items the chef has collected and is carrying to a customer.
    private List<MenuItem> tray = new ArrayList<>();

    public Chef(int startX, int startY, int size, int speed, String sheetPath, int cols, int rows) {
        this.x = startX; this.y = startY; this.size = size; this.speed = speed;
        sprites = new BufferedImage[rows][cols];
        try {
            spriteSheet = ImageIO.read(getClass().getResourceAsStream(sheetPath));
            int fw = spriteSheet.getWidth() / cols, fh = spriteSheet.getHeight() / rows;
            for (int r = 0; r < rows; r++)
                for (int c = 0; c < cols; c++)
                    sprites[r][c] = spriteSheet.getSubimage(c*fw, r*fh, fw, fh);
        } catch (IOException e) { System.err.println("Error loading spritesheet: " + sheetPath); }
    }

    // --- tray ---
    public void addToTray(MenuItem item) { tray.add(item); }
    public List<MenuItem> getTray()      { return tray; }
    public void clearTray()              { tray.clear(); }
    public boolean isTrayEmpty()         { return tray.isEmpty(); }

    public void keyPressed(KeyEvent e) {
        int k = e.getKeyCode();
        if (k == KeyEvent.VK_W) { moveUp = true;    currentCol = 19; }
        if (k == KeyEvent.VK_A) { moveLeft = true;  currentCol = 9;  }
        if (k == KeyEvent.VK_S) { moveDown = true;  currentCol = 0;  }
        if (k == KeyEvent.VK_D) { moveRight = true; currentCol = 9;  }
    }

    public void keyReleased(KeyEvent e) {
        int k = e.getKeyCode();
        if (k == KeyEvent.VK_W) moveUp = false;
        if (k == KeyEvent.VK_A) moveLeft = false;
        if (k == KeyEvent.VK_S) moveDown = false;
        if (k == KeyEvent.VK_D) moveRight = false;
        if (!moveUp && !moveDown && !moveLeft && !moveRight) currentCol = 0;
    }

    public void update(int screenWidth, int screenHeight) {
        boolean moving = moveUp || moveDown || moveLeft || moveRight;
        if (moving) {
            if (moveUp) y -= speed;     if (moveDown) y += speed;
            if (moveLeft) x -= speed;   if (moveRight) x += speed;
            animationCounter++;
            if (animationCounter >= animationSpeed) {
                animationCounter = 0;
                if (moveDown) { currentCol++; if (currentCol > 8) currentCol = 0; }
                else if (moveRight || moveLeft) {
                    if (currentCol < 9 || currentCol > 18) currentCol = 9;
                    currentCol++; if (currentCol > 18) currentCol = 9;
                } else if (moveUp) {
                    if (currentCol < 19 || currentCol > 27) currentCol = 19;
                    currentCol++; if (currentCol > 27) currentCol = 19;
                }
            }
        }
        if (x < 0) x = 0; if (y < 0) y = 0;
        if (x > screenWidth - size) x = screenWidth - size;
        if (y > screenHeight - size) y = screenHeight - size;
    }

    public void draw(Graphics g) {
        BufferedImage frame = sprites[0][currentCol];
        if (moveLeft) g.drawImage(frame, x+size, y, x, y+size, 0, 0, frame.getWidth(), frame.getHeight(), null);
        else          g.drawImage(frame, x, y, size, size, null);
        // The tray itself is drawn by GamePanel (it owns the item sprites).
    }
}