public class CharacterData {
    private String name;
    private String introText;
    private String successText;
    private String failText;

    public CharacterData(String name, String introText, String successText, String failText) {
        this.name = name;
        this.introText = introText;
        this.successText = successText;
        this.failText = failText;
    }

    public String getName() {
        return name;
    }

    public String getIntroText() {
        return introText;
    }

    public String getSuccessText() {
        return successText;
    }

    public String getFailText() {
        return failText;
    }
}
