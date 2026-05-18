import java.util.List;

public class Dialog {

    private String text;
    private int delay;
    private List<String> choices;

    public Dialog(String text, int delay) {
        this.text = text;
        this.delay = delay;
        this.choices = null;
    }

    public Dialog(String text, int delay, List<String> choices) {
        this.text = text;
        this.delay = delay;
        this.choices = choices;
    }

    public String getText() {
        return text;
    }

    public int getDelay() {
        return delay;
    }

    public List<String> getChoices() {
        return choices;
    }

    public boolean hasChoices() {
        return choices != null && !choices.isEmpty();
    }
}