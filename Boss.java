import java.util.Arrays;

public class Boss extends Character {

    public Boss() {
        super("Boss");

        addDialog(new Dialog("Hello, I heard from my assistant Luna that you came here for a job?", 2000, Arrays.asList("Yes! I would love to work in this adorable cafe!", "Excuse me? No, I think there was a misunderstanding.")));
        addDialog(new Dialog("Okay, let's move on to your first day. We will call it a tutorial day.", 3000));
        addDialog(new Dialog("If you complete the tutorial, you will be able to start the next days, upgrade the cafe, and unlock new drinks and snacks!", 4000));
        addDialog(new Dialog("But if you do not complete the tutorial, you will need to start over... or I will just have to fire you.", 4000));
        addDialog(new Dialog("Are you ready?", 1000, Arrays.asList("Yes, let's go!", "No, sorry, I changed my mind.")));
        addDialog(new Dialog("So, your main task is to serve the customer.", 2000));
        addDialog(new Dialog("Read what they want, prepare the item, and give it to them.", 3000));
        addDialog(new Dialog("Be careful. At first, it might be easy, but later customers will order more drinks and snacks.", 4000));
        addDialog(new Dialog("If your order is purrfect - 100% accurate - customers will give you Kitty Tips.", 3000));
        addDialog(new Dialog("More Kitty Tips means more money. More money means more upgrades!", 3000));
        addDialog(new Dialog("Oh, here comes the customer. Be ready!", 2000));
    }
}