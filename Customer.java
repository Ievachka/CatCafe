import java.util.List;

public class Customer {

    private String name;
    private String introText;
    private String successText;
    private String failText;

    private List<MenuItem> order;

    private int x, y, size;
    private int spriteIndex; // Stores which cat PNG to use (0 to 3)

    private boolean active  = false;
    private boolean served  = false;
    private boolean leaving = false;
    private int introTimer = 0;
    private int waitTime   = 0;
    private int patience   = 600;
    private int leaveTimer = 0;

    // Message shown in a speech bubble while leaving (success or timeout-fail line).
    private String leaveMessage = null;
    private boolean leftHappy = false;

    // Temporary reaction bubble (e.g. wrong order, but the customer stays to retry).
    private int reactionTimer = 0;
    private String reactionText = null;
    private boolean reactionHappy = false;

    private static final int INTRO_DURATION = 150;     // ~2.5s greeting before the order shows
    private static final int REACTION_DURATION = 150;  // ~2.5s reaction bubble

    public Customer(int x, int y, int size) {
        this.x = x;
        this.y = y;
        this.size = size;
    }

    // UPDATED: Added 'int spriteIndex' to the parameters
    public void seat(CharacterData who, List<MenuItem> order, int patience, int spriteIndex) {
        this.name        = who.getName();
        this.introText   = who.getIntroText();
        this.successText = who.getSuccessText();
        this.failText    = who.getFailText();
        this.order       = order;
        this.patience    = patience;
        this.spriteIndex = spriteIndex; // Assign the random cat sprite

        this.active     = true;
        this.served     = false;
        this.leaving    = false;
        this.introTimer = INTRO_DURATION;
        this.waitTime   = 0;
        this.leaveTimer = 0;
        this.leaveMessage = null;
        this.reactionTimer = 0;
        this.reactionText = null;
    }

    public void update() {
        if (reactionTimer > 0) reactionTimer--;
        if (active && !served) {
            if (introTimer > 0) introTimer--;
            else waitTime++;
        }
        if (leaving) leaveTimer++;
    }

    public boolean isGreeting() {
        return active && !served && introTimer > 0;
    }

    public boolean hasLostPatience() {
        return active && !served && introTimer == 0 && waitTime >= patience;
    }

    public boolean shouldDisappear() {
        return leaving && leaveTimer >= 150; // ~2.5s, gives the bubble time to show
    }

    public void serve() { 
        if (active && !served) served = true; 
    }

    public void startLeaving() { 
        startLeaving(null, false); 
    }

    public void startLeaving(String message, boolean happy) {
        leaving = true;
        leaveMessage = message;
        leftHappy = happy;
    }

    // Show a short reaction bubble without leaving (used for wrong-order retries).
    public void react(String text, boolean happy) {
        reactionText = text;
        reactionHappy = happy;
        reactionTimer = REACTION_DURATION;
    }

    public void disappear() {
        active = false; 
        served = false; 
        leaving = false;
        introTimer = 0; 
        waitTime = 0; 
        leaveTimer = 0;
        leaveMessage = null; 
        reactionTimer = 0; 
        reactionText = null; 
        order = null;
    }

    // Getters
    public String getName()         { return name; }
    public String getIntroText()    { return introText; }
    public String getSuccessText()  { return successText; }
    public String getFailText()     { return failText; }
    public List<MenuItem> getOrder(){ return order; }
    public String getLeaveMessage() { return leaveMessage; }
    public boolean leftHappy()      { return leftHappy; }
    public boolean hasReaction()    { return reactionTimer > 0; }
    public String getReactionText() { return reactionText; }
    public boolean reactionHappy()  { return reactionHappy; }
    
    public int getX()               { return x; }
    public int getY()               { return y; }
    public int getSize()            { return size; }
    
    // NEW: Getter for the sprite index so GamePanel knows which cat to draw
    public int getSpriteIndex()     { return spriteIndex; }
    
    public int getWaitTime()        { return waitTime; }
    public int getPatience()        { return patience; }
    public int getLeaveTimer()      { return leaveTimer; }
    public boolean isActive()       { return active; }
    public boolean isServed()       { return served; }
    public boolean isLeaving()      { return leaving; }
}