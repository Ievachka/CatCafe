public class CharacterData {
    private String name;
    private String introText;
    private String successText;
    private String failText;
    private int basePatience;   // frames before leaving on day 1; lower = more impatient

    public CharacterData(String name, String introText, String successText, String failText, int basePatience) {
        this.name = name;
        this.introText = introText;
        this.successText = successText;
        this.failText = failText;
        this.basePatience = basePatience;
    }

    public String getName()        { return name; }
    public String getIntroText()   { return introText; }
    public String getSuccessText() { return successText; }
    public String getFailText()    { return failText; }
    public int getBasePatience()   { return basePatience; }
}