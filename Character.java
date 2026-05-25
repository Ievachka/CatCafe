import java.util.List;
import java.util.ArrayList;

public class Character {

    private String name;
    private List<Dialog> dialogues;
    private int currentDialogIndex;

    public Character(String name) {
        this.name = name;
        this.dialogues = new ArrayList<>();
        this.currentDialogIndex = 0;
    }

    public void addDialog(Dialog dialog) {
        dialogues.add(dialog);
    }

    public void speak() {
        Dialog currentDialog = dialogues.get(currentDialogIndex);
        System.out.println(currentDialog.getText());

        try {
            Thread.sleep(currentDialog.getDelay());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void nextDialog() {
        currentDialogIndex++;
    }

    public boolean hasMoreDialogs() {
        return currentDialogIndex < dialogues.size();
    }

    public Dialog getCurrentDialog() {
        if (currentDialogIndex < dialogues.size()) {
            return dialogues.get(currentDialogIndex);
        }
        return null;
    }

}
