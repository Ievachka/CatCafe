import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GamePanel extends JPanel implements MouseListener, MouseMotionListener, KeyListener {

    // ===== Game states =====
    enum GameState { MENU, INTRO, TUTORIAL, DAY_INTRO, PLAYING, DAY_COMPLETE, GAME_OVER }
    private GameState state = GameState.MENU;

    // ===== Progress / flow =====
    private Player profile = new Player();
    private int currentDay = 1;
    private static final int MAX_DAYS = 10;
    private boolean tutorialDone = false;

    // ===== Timers (frames, ~60/sec) =====
    private int introTimer = 0;
    private int dayCompleteTimer = 0;
    private int comingSoonTimer = 0;

    // ===== Tutorial =====
    private int tutorialStage = 0;
    private int lastTutorialSeat = -1;

    // ===== Intro dialogue (job interview) =====
    private final List<Line> introScript = new ArrayList<>();
    private int introIndex = 0;
    private Rectangle choiceA, choiceB;

    // ===== Day bookkeeping =====
    private int dayGoal = 0, dayCoins = 0, dayCustomersServed = 0, dayCustomersSpawned = 0, dayBonus = 0;
    private boolean dayHadFail = false;
    private boolean dayPassed = false;
    private int spawnTimer = 0;
    private int spawnDelay = 150;                          // ~2.5s between arrivals
    private static final int DAY_CUSTOMER_LIMIT = 10;      // max cats per day
    private static final int PATIENCE_STEP = 60;           // patience lost per day (~1s)
    private static final int MIN_PATIENCE = 240;           // floor (~4s)
    private static final int TUTORIAL_PATIENCE = 999999;   // effectively no timeout

    // ===== Menu layout =====
    private int mouseX = 0, mouseY = 0;
    private int btnX = 130, btnW = 540, btnH = 100;
    private int startY = 310, shopY = 450, exitY = 590;

    // ===== World =====
    private Chef chef;
    private final List<MenuItem> menu = ItemLibrary.getAllItems();
    private final List<Customer> seats = new ArrayList<>();
    private final List<Station> stations = new ArrayList<>();
    private int interactionRange = 70;

    // ===== Images =====
    private BufferedImage menuBg, menuStartBg, menuShopBg, menuExitBg, cafeBg, customerSprite;
    private final Map<String, BufferedImage> itemSprites = new HashMap<>();

    private Timer gameLoopTimer;

    public GamePanel() {
        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);
        setFocusable(true);

        chef = new Chef(350, 350, 128, 8, "/images/spritesheet.png", 28, 1);

        // Seats placed on the 4 tables of cafe1.png (size 96, biased to the front edge for bubble room).
        seats.add(new Customer(70, 150, 96));    // top-left table
        seats.add(new Customer(605, 150, 96));   // top-right table
        seats.add(new Customer(130, 400, 96));   // middle-left table
        seats.add(new Customer(615, 430, 96));   // bottom-right table

        loadImages();
        buildStations();

        gameLoopTimer = new Timer(16, e -> { updateGame(); repaint(); });
        gameLoopTimer.start();
    }

    private void loadImages() {
        try {
            menuBg         = ImageIO.read(getClass().getResourceAsStream("/images/menu1.png"));
            menuStartBg    = ImageIO.read(getClass().getResourceAsStream("/images/menuStart.png"));
            menuShopBg     = ImageIO.read(getClass().getResourceAsStream("/images/menuShop.png"));
            menuExitBg     = ImageIO.read(getClass().getResourceAsStream("/images/menuExit.png"));
            cafeBg         = ImageIO.read(getClass().getResourceAsStream("/images/cafe1.png"));
            customerSprite = ImageIO.read(getClass().getResourceAsStream("/images/customer.png"));
        } catch (IOException e) {
            System.err.println("Error loading background images!");
        }
        for (MenuItem m : menu) {
            try {
                itemSprites.put(m.getSpriteFile(),
                        ImageIO.read(getClass().getResourceAsStream("/images/" + m.getSpriteFile())));
            } catch (IOException | IllegalArgumentException e) {
                System.err.println("Missing item sprite: " + m.getSpriteFile());
            }
        }
    }

    private void buildStations() {
        int sz = 72, y = 704, startX = 20, step = 114;
        for (int i = 0; i < menu.size(); i++) {
            stations.add(new Station(startX + i * step, y, sz, sz, menu.get(i)));
        }
    }

    // ============================================================
    //  UPDATE
    // ============================================================
    private void updateGame() {
        if (comingSoonTimer > 0) comingSoonTimer--;

        switch (state) {
            case MENU:                                break;
            case INTRO:                               break;  // advances on click
            case TUTORIAL:     updateTutorial();      break;
            case DAY_INTRO:    updateDayIntro();      break;
            case PLAYING:      updatePlaying();       break;
            case DAY_COMPLETE: updateDayComplete();   break;
            case GAME_OVER:                           break;
        }
    }

    private void updateTutorial() {
        chef.update(getWidth(), getHeight());
        for (Customer c : seats) {
            c.update();
            if (c.shouldDisappear()) c.disappear();
        }
    }

    private void updateDayIntro() {
        introTimer++;
        if (introTimer > 120) {            // ~2s
            introTimer = 0;
            startDay();
        }
    }

    private void updatePlaying() {
        chef.update(getWidth(), getHeight());

        for (Customer c : seats) {
            c.update();
            if (c.hasLostPatience()) {
                dayHadFail = true;
                c.startLeaving(c.getFailText(), false);   // fail bubble shows above them
            }
            if (c.shouldDisappear()) c.disappear();
        }

        if (dayCoins >= dayGoal) { endDay(true); return; }

        if (dayCustomersSpawned < DAY_CUSTOMER_LIMIT) {
            spawnTimer++;
            if (spawnTimer >= spawnDelay) {
                spawnTimer = 0;
                seatNewCustomer();
            }
        } else if (noActiveCustomers()) {
            endDay(false);
        }
    }

    private void updateDayComplete() {
        dayCompleteTimer++;
        if (dayCompleteTimer > 240) {      // ~4s
            dayCompleteTimer = 0;
            if (dayPassed) {
                currentDay++;
                if (currentDay > MAX_DAYS) state = GameState.GAME_OVER;
                else { state = GameState.DAY_INTRO; introTimer = 0; }
            } else {
                state = GameState.DAY_INTRO;   // repeat the same day
                introTimer = 0;
            }
        }
    }

    // ============================================================
    //  DAY / TUTORIAL FLOW
    // ============================================================
    private void startDay() {
        dayGoal = currentDay * 10;
        dayCoins = 0;
        dayCustomersServed = 0;
        dayCustomersSpawned = 0;
        dayHadFail = false;
        dayBonus = 0;
        dayPassed = false;
        spawnTimer = spawnDelay;
        for (Customer c : seats) c.disappear();
        chef.clearTray();
        state = GameState.PLAYING;
    }

    private void seatNewCustomer() {
        for (Customer c : seats) {
            if (!c.isActive()) {
                int n = Math.min(1 + currentDay / 2, 4);
                CharacterData who = randomCharacter();
                c.seat(who, generateRandomOrder(n), effectivePatience(who.getBasePatience()));
                dayCustomersSpawned++;
                return;
            }
        }
    }

    private int effectivePatience(int base) {
        return Math.max(MIN_PATIENCE, base - (currentDay - 1) * PATIENCE_STEP);
    }

    private void endDay(boolean passed) {
        dayPassed = passed;
        if (passed && !dayHadFail && dayCustomersServed > 0) {
            dayBonus = (int) (dayCoins * 0.5);
            profile.addKittyCoin(dayBonus);
        } else {
            dayBonus = 0;
        }
        if (passed) profile.completedDay();
        for (Customer c : seats) c.disappear();
        chef.clearTray();
        state = GameState.DAY_COMPLETE;
        dayCompleteTimer = 0;
    }

    private boolean noActiveCustomers() {
        for (Customer c : seats) if (c.isActive()) return false;
        return true;
    }

    private void enterTutorial() {
        tutorialStage = 0;
        for (Customer c : seats) c.disappear();
        chef.clearTray();
        state = GameState.TUTORIAL;
        seatTutorialCustomer();
    }

    private void seatTutorialCustomer() {
        CharacterData who = (tutorialStage == 0)
                ? CharacterLibrary.getAllCharacters().get(0)   // Luna is always first
                : randomCharacter();
        int n = tutorialStage + 1;                              // 1, 2, 3 items
        int idx;
        do { idx = (int) (Math.random() * seats.size()); } while (seats.size() > 1 && idx == lastTutorialSeat);
        lastTutorialSeat = idx;
        seats.get(idx).seat(who, generateRandomOrder(n), TUTORIAL_PATIENCE);
    }

    // ============================================================
    //  INTRO DIALOGUE
    // ============================================================
    private void startIntro() {
        buildIntroScript();
        introIndex = 0;
        state = GameState.INTRO;
    }

    private void buildIntroScript() {
        introScript.clear();
        introScript.add(new Line("Luna", "Welcome to the Cat Cafe!"));
        introScript.add(new Line("Luna", "My name is Luna and I am the boss's assistant."));
        introScript.add(new Line("Luna", "Our cafe is a cozy place where you can enjoy coffee, tea, and snacks. >.<"));
        introScript.add(new Line("Luna", "Come visit us for a warm drink and some purr-fect moments! :3"));
        introScript.add(new Line("Luna", "Oh, wait... You came here for a job, didn't you?"));
        introScript.add(new Line("Luna", "Alright... stay here. I'll call our boss."));
        introScript.add(new Line("Boss", "Hello! I heard from Luna that you came here looking for a job?"));
        introScript.add(Line.choice("Boss", "So, do you want to work here?", "Yes! I'd love to!", "No, my mistake"));
        introScript.add(new Line("Boss", "Great! First I need to see if you are capable."));
        introScript.add(new Line("Boss", "Let's start with your first day - a tutorial day."));
        introScript.add(new Line("Boss", "Complete it and you can start real days, earn coins and unlock more!"));
        introScript.add(new Line("Boss", "Fail it... and I'll have to let you go."));
        introScript.add(Line.choice("Boss", "Are you ready?", "Yes, let's go!", "No, not yet"));
        introScript.add(new Line("Boss", "Perfect! Let's begin!"));
        introScript.add(new Line("Boss", "Your job: read what each customer wants, make it, and serve it."));
        introScript.add(new Line("Boss", "Walk to the stations at the bottom and press E to pick up an item."));
        introScript.add(new Line("Boss", "Bring the EXACT order to the customer and press E to serve."));
        introScript.add(new Line("Boss", "Get it 100% right and they leave Kitty Tips - more coins!"));
        introScript.add(new Line("Boss", "Here comes your first customer. Good luck!"));
    }

    private void handleIntroClick(int mx, int my) {
        Line line = introScript.get(introIndex);
        if (line.isChoice) {
            if (choiceA != null && choiceA.contains(mx, my)) advanceIntro();        // accept
            else if (choiceB != null && choiceB.contains(mx, my)) state = GameState.MENU; // decline
        } else {
            advanceIntro();
        }
    }

    private void advanceIntro() {
        introIndex++;
        if (introIndex >= introScript.size()) {
            enterTutorial();
        }
    }

    // ============================================================
    //  INTERACTION
    // ============================================================
    private void handleInteract() {
        if (!chef.isTrayEmpty()) {
            for (Customer c : seats) {
                if (c.isActive() && !c.isServed() && !c.isLeaving() && !c.isGreeting() && nearCustomer(c)) {
                    deliver(c);
                    return;
                }
            }
        }
        for (Station s : stations) {
            if (s.isPlayerNearby(chef.x, chef.y, chef.size, interactionRange)) {
                chef.addToTray(s.getItem());
                return;
            }
        }
    }

    private void deliver(Customer c) {
        boolean correct = checkOrder(c.getOrder(), chef.getTray());
        chef.clearTray();

        if (state == GameState.TUTORIAL) {
            if (correct) {
                c.serve();
                c.startLeaving(c.getSuccessText(), true);   // success bubble above the cat
                tutorialStage++;
                if (tutorialStage >= 3) {
                    tutorialDone = true;
                    profile.completeTutorial();
                    state = GameState.DAY_INTRO;
                    introTimer = 0;
                } else {
                    seatTutorialCustomer();
                }
            } else {
                c.react(c.getFailText(), false);            // fail bubble, cat stays to retry
            }
            return;
        }

        if (correct) {
            c.serve();
            int coins = 0;
            for (MenuItem m : c.getOrder()) coins += m.getCoinsValue();
            profile.addKittyCoin(coins);
            dayCoins += coins;
            dayCustomersServed++;
            c.startLeaving(c.getSuccessText(), true);     // success bubble shows above them
        } else {
            c.react(c.getFailText(), false);              // wrong order: bubble, stays to retry
        }
    }

    private boolean checkOrder(List<MenuItem> want, List<MenuItem> got) {
        if (got.size() != want.size()) return false;
        List<MenuItem> remaining = new ArrayList<>(want);
        for (MenuItem g : got) {
            if (!remaining.remove(g)) return false;
        }
        return remaining.isEmpty();
    }

    private List<MenuItem> generateRandomOrder(int n) {
        List<MenuItem> order = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            order.add(menu.get((int) (Math.random() * menu.size())));
        }
        return order;
    }

    private CharacterData randomCharacter() {
        List<CharacterData> all = CharacterLibrary.getNonTutorialCharacters();
        return all.get((int) (Math.random() * all.size()));
    }

    private boolean nearCustomer(Customer c) {
        int pcx = chef.x + chef.size / 2, pcy = chef.y + chef.size / 2;
        int ccx = c.getX() + c.getSize() / 2, ccy = c.getY() + c.getSize() / 2;
        return Math.abs(pcx - ccx) < interactionRange && Math.abs(pcy - ccy) < interactionRange;
    }

    // ============================================================
    //  RENDER
    // ============================================================
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        switch (state) {
            case MENU:         drawMenu(g);          break;
            case INTRO:        drawIntro(g);         break;
            case TUTORIAL:     drawTutorial(g);      break;
            case DAY_INTRO:    drawDayIntro(g);      break;
            case PLAYING:      drawPlaying(g);       break;
            case DAY_COMPLETE: drawDayComplete(g);   break;
            case GAME_OVER:    drawGameOver(g);      break;
        }
        if (comingSoonTimer > 0) drawComingSoon(g);
    }

    private void drawScene(Graphics g) {
        if (cafeBg != null) g.drawImage(cafeBg, 0, 0, getWidth(), getHeight(), null);
        drawStations(g);
        drawCustomers(g);
        chef.draw(g);
        drawTray(g);
    }

    private void drawMenu(Graphics g) {
        boolean hoverStart = inButton(mouseX, mouseY, startY);
        boolean hoverShop  = inButton(mouseX, mouseY, shopY);
        boolean hoverExit  = inButton(mouseX, mouseY, exitY);
        BufferedImage bg = menuBg;
        if      (hoverStart && menuStartBg != null) bg = menuStartBg;
        else if (hoverShop  && menuShopBg  != null) bg = menuShopBg;
        else if (hoverExit  && menuExitBg  != null) bg = menuExitBg;
        if (bg != null) g.drawImage(bg, 0, 0, getWidth(), getHeight(), null);
    }

    private void drawIntro(Graphics g) {
        if (cafeBg != null) g.drawImage(cafeBg, 0, 0, getWidth(), getHeight(), null);
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, getWidth(), getHeight());

        Line line = introScript.get(introIndex);
        int bx = 60, bw = getWidth() - 120, bh = 210, by = getHeight() - bh - 70;

        g.setColor(new Color(22, 18, 30, 240));
        g.fillRoundRect(bx, by, bw, bh, 18, 18);
        g.setColor(new Color(255, 222, 110));
        g.drawRoundRect(bx, by, bw, bh, 18, 18);

        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString(line.speaker, bx + 22, by + 34);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        List<String> lines = wrapText(g, line.text, bw - 44);
        FontMetrics fm = g.getFontMetrics();
        int ty = by + 64;
        for (String s : lines) { g.drawString(s, bx + 22, ty); ty += fm.getHeight(); }

        choiceA = null; choiceB = null;
        if (line.isChoice) {
            int btnW = (bw - 66) / 2, btnH = 44, ay = by + bh - btnH - 16;
            choiceA = new Rectangle(bx + 22, ay, btnW, btnH);
            choiceB = new Rectangle(bx + 44 + btnW, ay, btnW, btnH);
            drawButton(g, choiceA, line.optA, new Color(90, 175, 110));
            drawButton(g, choiceB, line.optB, new Color(180, 90, 90));
        } else {
            g.setColor(new Color(255, 255, 255, 170));
            g.setFont(new Font("Arial", Font.ITALIC, 14));
            String hint = "Click to continue >";
            g.drawString(hint, bx + bw - g.getFontMetrics().stringWidth(hint) - 18, by + bh - 16);
        }
    }

    private void drawButton(Graphics g, Rectangle r, String text, Color col) {
        g.setColor(col);
        g.fillRoundRect(r.x, r.y, r.width, r.height, 12, 12);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 15));
        FontMetrics fm = g.getFontMetrics();
        g.drawString(text, r.x + (r.width - fm.stringWidth(text)) / 2, r.y + (r.height + fm.getAscent()) / 2 - 3);
    }

    private void drawTutorial(Graphics g) {
        drawScene(g);
        drawTopPanel(g, "Tutorial", "Stage " + (tutorialStage + 1) + " / 3");
        drawControlsHint(g);
    }

    private void drawDayIntro(Graphics g) {
        drawScene(g);
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(new Color(255, 222, 110));
        g.setFont(new Font("Arial", Font.BOLD, 40));
        drawCentered(g, "Day " + currentDay, getHeight() / 2 - 20);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 22));
        drawCentered(g, "Goal: " + (currentDay * 10) + " coins", getHeight() / 2 + 18);
    }

    private void drawPlaying(Graphics g) {
        drawScene(g);
        drawTopPanel(g, "Day " + currentDay, dayCoins + " / " + dayGoal + " coins");
        drawControlsHint(g);
    }

    private void drawDayComplete(Graphics g) {
        drawScene(g);
        g.setColor(new Color(0, 0, 0, 175));
        g.fillRect(0, 0, getWidth(), getHeight());

        if (dayPassed) {
            g.setColor(new Color(255, 222, 110));
            g.setFont(new Font("Arial", Font.BOLD, 34));
            drawCentered(g, "Day " + currentDay + " complete!", getHeight() / 2 - 50);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            drawCentered(g, "Earned " + dayCoins + " / " + dayGoal + " coins", getHeight() / 2 - 10);
            if (dayBonus > 0) {
                g.setColor(new Color(120, 220, 140));
                drawCentered(g, "All accurate!  Kitty Tips +" + dayBonus, getHeight() / 2 + 20);
            }
            g.setColor(Color.WHITE);
            drawCentered(g, "Balance: " + profile.getKittyCoin() + " Kitty Coins", getHeight() / 2 + 55);
        } else {
            g.setColor(new Color(245, 120, 120));
            g.setFont(new Font("Arial", Font.BOLD, 34));
            drawCentered(g, "Not enough orders!", getHeight() / 2 - 40);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            drawCentered(g, "You reached " + dayCoins + " / " + dayGoal + " coins", getHeight() / 2 + 2);
            drawCentered(g, "Repeating day " + currentDay + "...", getHeight() / 2 + 34);
        }
    }

    private void drawGameOver(Graphics g) {
        g.setColor(new Color(20, 20, 30));
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(new Color(255, 222, 110));
        g.setFont(new Font("Arial", Font.BOLD, 36));
        drawCentered(g, "You completed the game!", getHeight() / 2 - 30);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        drawCentered(g, "Final balance: " + profile.getKittyCoin() + " Kitty Coins", getHeight() / 2 + 10);
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        drawCentered(g, "Click to return to the menu", getHeight() / 2 + 45);
    }

    // ----- HUD panel -----
    private void drawTopPanel(Graphics g, String big, String small) {
        int px = 12, py = 10, pw = 270, ph = 60;
        g.setColor(new Color(0, 0, 0, 175));
        g.fillRoundRect(px, py, pw, ph, 16, 16);
        g.setColor(new Color(255, 255, 255, 70));
        g.drawRoundRect(px, py, pw, ph, 16, 16);

        g.setColor(new Color(255, 222, 110));
        g.setFont(new Font("Arial", Font.BOLD, 26));
        g.drawString(big, px + 16, py + 30);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString(small, px + 16, py + 52);

        g.setFont(new Font("Arial", Font.PLAIN, 14));
        g.drawString("Balance: " + profile.getKittyCoin(), px + 150, py + 52);
    }

    private void drawControlsHint(Graphics g) {
        String t = "WASD move   E collect/serve   Q clear tray";
        g.setFont(new Font("Arial", Font.BOLD, 13));
        FontMetrics fm = g.getFontMetrics();
        int pad = 10, w = fm.stringWidth(t) + pad * 2, h = 26;
        int x = getWidth() - w - 12, y = 12;
        g.setColor(new Color(0, 0, 0, 165));
        g.fillRoundRect(x, y, w, h, 12, 12);
        g.setColor(new Color(255, 255, 255, 235));
        g.drawString(t, x + pad, y + 18);
    }

    private void drawStations(Graphics g) {
        for (Station s : stations) {
            BufferedImage img = itemSprites.get(s.getItem().getSpriteFile());
            if (img != null) g.drawImage(img, s.getX(), s.getY(), s.getWidth(), s.getHeight(), null);
            else { g.setColor(new Color(120, 90, 60)); g.fillRect(s.getX(), s.getY(), s.getWidth(), s.getHeight()); }

            g.setFont(new Font("Arial", Font.BOLD, 13));
            FontMetrics fm = g.getFontMetrics();
            String nm = s.getItem().getName();
            int tx = s.getX() + s.getWidth() / 2 - fm.stringWidth(nm) / 2;
            int ty = s.getY() - 6;
            g.setColor(new Color(0, 0, 0, 210));
            g.drawString(nm, tx + 1, ty + 1);
            g.setColor(Color.WHITE);
            g.drawString(nm, tx, ty);

            if (s.isPlayerNearby(chef.x, chef.y, chef.size, interactionRange)) {
                g.setColor(Color.YELLOW);
                g.setFont(new Font("Arial", Font.BOLD, 22));
                g.drawString("E", s.getX() + s.getWidth() / 2 - 6, s.getY() - 20);
            }
        }
    }

    private void drawCustomers(Graphics g) {
        for (Customer c : seats) {
            if (!c.isActive()) continue;

            float alpha = c.isLeaving() ? Math.max(0f, 1f - c.getLeaveTimer() / 120f) : 1f;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            if (customerSprite != null)
                g2.drawImage(customerSprite, c.getX(), c.getY(), c.getSize(), c.getSize(), null);
            g2.dispose();

            if (c.isLeaving() && c.getLeaveMessage() != null) {
                Color accent = c.leftHappy() ? new Color(40, 150, 70) : new Color(200, 60, 60);
                drawSpeechBubble(g, c, c.getName(), c.getLeaveMessage(), accent);   // success/fail line
            } else if (c.hasReaction()) {
                Color accent = c.reactionHappy() ? new Color(40, 150, 70) : new Color(200, 60, 60);
                drawSpeechBubble(g, c, c.getName(), c.getReactionText(), accent);   // wrong-order reaction
                if (c.getPatience() < 90000) drawPatienceBar(g, c);
            } else if (c.isGreeting()) {
                drawSpeechBubble(g, c, c.getName(), c.getIntroText(), new Color(120, 90, 40));  // intro first
            } else if (!c.isServed()) {
                if (c.getPatience() < 90000) drawPatienceBar(g, c);
                drawOrderBubble(g, c);                                   // then the order
                drawNameTag(g, c, c.getY() - 94);
            }
        }
    }

    private void drawPatienceBar(Graphics g, Customer c) {
        int bw = c.getSize(), bh = 7, bx = c.getX(), by = c.getY() - 14;
        g.setColor(new Color(0, 0, 0, 130)); g.fillRect(bx - 1, by - 1, bw + 2, bh + 2);
        g.setColor(new Color(200, 60, 60));  g.fillRect(bx, by, bw, bh);
        int rem = (int) ((1.0 - (double) c.getWaitTime() / c.getPatience()) * bw);
        g.setColor(new Color(90, 210, 110)); g.fillRect(bx, by, Math.max(0, rem), bh);
    }

    private void drawOrderBubble(Graphics g, Customer c) {
        List<MenuItem> order = c.getOrder();
        if (order == null || order.isEmpty()) return;
        int isz = 26, pad = 3;
        int totalW = order.size() * isz + (order.size() - 1) * pad;
        int bx = c.getX() + c.getSize() / 2 - totalW / 2;
        int by = c.getY() - 86;

        g.setColor(new Color(255, 255, 255, 230));
        g.fillRoundRect(bx - 6, by - 6, totalW + 12, isz + 12, 10, 10);
        g.setColor(new Color(0, 0, 0, 70));
        g.drawRoundRect(bx - 6, by - 6, totalW + 12, isz + 12, 10, 10);
        for (int i = 0; i < order.size(); i++) {
            BufferedImage img = itemSprites.get(order.get(i).getSpriteFile());
            if (img != null) g.drawImage(img, bx + i * (isz + pad), by, isz, isz, null);
        }
    }

    // Speech bubble (intro / success / fail) above the customer, wrapped, clamped on screen.
    private void drawSpeechBubble(Graphics g, Customer c, String name, String text, Color accent) {
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        FontMetrics fm = g.getFontMetrics();
        int maxW = 210, lineH = fm.getHeight();
        List<String> lines = wrapText(g, text, maxW);
        int w = fm.stringWidth(name);
        for (String s : lines) w = Math.max(w, fm.stringWidth(s));
        w += 16;
        int h = (lines.size() + 1) * lineH + 10;
        int cx = c.getX() + c.getSize() / 2;
        int x = Math.max(6, Math.min(cx - w / 2, getWidth() - w - 6));
        int y = c.getY() - 8 - h;
        if (y < 6) y = c.getY() + c.getSize() + 8;   // no room above -> draw below

        g.setColor(new Color(255, 255, 255, 238));
        g.fillRoundRect(x, y, w, h, 10, 10);
        g.setColor(new Color(0, 0, 0, 80));
        g.drawRoundRect(x, y, w, h, 10, 10);

        int ty = y + fm.getAscent() + 5;
        g.setColor(accent);
        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.drawString(name, x + 8, ty);
        ty += lineH;
        g.setColor(new Color(40, 40, 40));
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        for (String s : lines) { g.drawString(s, x + 8, ty); ty += lineH; }
    }

    private void drawNameTag(Graphics g, Customer c, int baselineY) {
        g.setFont(new Font("Arial", Font.BOLD, 15));
        FontMetrics fm = g.getFontMetrics();
        String name = c.getName();
        int nx = c.getX() + c.getSize() / 2 - fm.stringWidth(name) / 2;
        g.setColor(new Color(0, 0, 0, 190));
        g.drawString(name, nx + 1, baselineY + 1);
        g.setColor(Color.WHITE);
        g.drawString(name, nx, baselineY);
    }

    private void drawTray(Graphics g) {
        List<MenuItem> tray = chef.getTray();
        if (tray.isEmpty()) return;
        int isz = 30, pad = 4;
        int totalW = tray.size() * isz + (tray.size() - 1) * pad;
        int startX = chef.x + chef.size / 2 - totalW / 2;
        int y = chef.y - isz - 6;
        for (int i = 0; i < tray.size(); i++) {
            BufferedImage img = itemSprites.get(tray.get(i).getSpriteFile());
            if (img != null) g.drawImage(img, startX + i * (isz + pad), y, isz, isz, null);
        }
    }

    private void drawComingSoon(Graphics g) {
        int w = 360, h = 70, x = (getWidth() - w) / 2, y = 40;
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRoundRect(x, y, w, h, 16, 16);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 22));
        drawCentered(g, "Shop - Coming soon!", y + 44);
    }

    // ============================================================
    //  INPUT
    // ============================================================
    @Override
    public void mouseClicked(MouseEvent e) {
        if (state == GameState.MENU) {
            if (inButton(e.getX(), e.getY(), startY)) {
                startGame();
            } else if (inButton(e.getX(), e.getY(), shopY)) {
                comingSoonTimer = 180;
            } else if (inButton(e.getX(), e.getY(), exitY)) {
                System.exit(0);
            }
            requestFocusInWindow();
        } else if (state == GameState.INTRO) {
            handleIntroClick(e.getX(), e.getY());
            requestFocusInWindow();
        } else if (state == GameState.GAME_OVER) {
            resetGame();
        }
    }

    private void startGame() {
        if (!tutorialDone) startIntro();
        else { state = GameState.DAY_INTRO; introTimer = 0; }
        requestFocusInWindow();
    }

    private void resetGame() {
        profile = new Player();
        currentDay = 1;
        tutorialDone = false;
        for (Customer c : seats) c.disappear();
        chef.clearTray();
        state = GameState.MENU;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        chef.keyPressed(e);
        if (state == GameState.PLAYING || state == GameState.TUTORIAL) {
            if (e.getKeyCode() == KeyEvent.VK_E) handleInteract();
            if (e.getKeyCode() == KeyEvent.VK_Q) chef.clearTray();
        }
    }

    @Override public void keyReleased(KeyEvent e) { chef.keyReleased(e); }
    @Override public void keyTyped(KeyEvent e) {}

    @Override public void mouseMoved(MouseEvent e)   { mouseX = e.getX(); mouseY = e.getY(); }
    @Override public void mouseDragged(MouseEvent e) {}
    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e){}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e)  {}

    // ============================================================
    //  Helpers
    // ============================================================
    private boolean inButton(int mx, int my, int topY) {
        return mx >= btnX && mx <= btnX + btnW && my >= topY && my <= topY + btnH;
    }

    private void drawCentered(Graphics g, String text, int y) {
        FontMetrics fm = g.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(text)) / 2;
        g.drawString(text, x, y);
    }

    private List<String> wrapText(Graphics g, String text, int maxW) {
        FontMetrics fm = g.getFontMetrics();
        List<String> out = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        for (String word : text.split(" ")) {
            String test = cur.length() == 0 ? word : cur + " " + word;
            if (fm.stringWidth(test) > maxW && cur.length() > 0) {
                out.add(cur.toString());
                cur = new StringBuilder(word);
            } else {
                cur = new StringBuilder(test);
            }
        }
        if (cur.length() > 0) out.add(cur.toString());
        return out;
    }

    private Color okGreen() { return new Color(90, 220, 130); }
    private Color failRed() { return new Color(240, 100, 100); }

    // ===== Intro dialogue line =====
    private static class Line {
        final String speaker, text;
        final boolean isChoice;
        final String optA, optB;

        Line(String speaker, String text) {
            this(speaker, text, false, null, null);
        }
        Line(String speaker, String text, boolean isChoice, String optA, String optB) {
            this.speaker = speaker;
            this.text = text;
            this.isChoice = isChoice;
            this.optA = optA;
            this.optB = optB;
        }
        static Line choice(String speaker, String q, String a, String b) {
            return new Line(speaker, q, true, a, b);
        }
    }
}
