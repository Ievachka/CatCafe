import javax.swing.JFrame;

public class Main {
    public static void main(String[] args) {
        JFrame window = new JFrame();
        GamePanel panel = new GamePanel();

        window.add(panel);
        window.setTitle("Cat Cafe");
        window.setSize(800, 800);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setVisible(true);
        panel.requestFocusInWindow();
    }
}