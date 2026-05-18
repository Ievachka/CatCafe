public class Luna extends Character {

    public Luna () {
        super("Luna");
        addDialog(new Dialog("Welcome to the Cat Cafe!",1000));
        addDialog(new Dialog("Our cafe is a cozy place where you can enjoy coffee, tea, and snacks. >.<", 3000));
        addDialog(new Dialog("Come visit us for a warm drink and some purr-fect moments! :3", 5000));
        addDialog(new Dialog("\nOh, wait...", 2000));
        addDialog(new Dialog("You came here for a job, didn’t you?", 3000));
        addDialog(new Dialog("Alright... stay here.", 1000));
        addDialog(new Dialog("I’ll call our boss. He’ll speak with you shortly.", 6000));

    }
}