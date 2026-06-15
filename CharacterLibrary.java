import java.util.ArrayList;
import java.util.List;

public class CharacterLibrary {

    public static List<CharacterData> getAllCharacters() {
        List<CharacterData> characters = new ArrayList<>();

        // 1. Luna (Manager)
        characters.add(new CharacterData(
                "Luna",
                "Hey again! I want to be your first customer to prepare you before the real ones arrive. Don't be scared if anything goes wrong!",
                "Great job! You're ready for the real customers now!",
                "Don't worry, just take a deep breath and try again!"
        ));

        // Garfield
        characters.add(new CharacterData(
                "Garfield",
                "Yo, just make me something tasty, yeah? No pressure though, I'm easy!",
                "Yooo, that's fire! You're the cat!",
                "Eh, all good bro. Just retry, I got time."
        ));

        // 4. Cheeto
        characters.add(new CharacterData(
                "Cheeto",
                "I don't have time for mistakes. Make my order right the first time.",
                "Finally, someone who does their job properly.",
                "This is unacceptable. Get it together next time."
        ));

        // 5. Panther
        characters.add(new CharacterData(
                "Panther",
                "Darling, I hope you can make something as fabulous as me!",
                "Oh honey, that's absolutely divine!",
                "This is not fabulous enough, sweetie. Try again."
        ));

        // 6. Carl
        characters.add(new CharacterData(
                "Carl",
                "OMG hiiii!!! I'm so excited to try your coffee! Tell me everything about it!",
                "YAAAAS! This is AMAZING! You're my new best friend!",
                "Oh no, but don't worry babe, you can totally do it next time! I believe in you!"
        ));

        // 7. Tom
        characters.add(new CharacterData(
                "Tom",
                "Just make my coffee and don't make a mess like last time.",
                "Alright, not bad. You're getting better.",
                "Ugh, this is the same mistake again. Really?"
        ));

        // 8. Miau
        characters.add(new CharacterData(
                "Miau",
                "I expect nothing but perfection. I am accustomed to the finest service.",
                "Finally! Someone who understands my impeccable taste!",
                "This is beneath my standards. I suggest you try harder."
        ));

        return characters;
    }

    public static List<CharacterData> getNonTutorialCharacters() {
        List<CharacterData> all = getAllCharacters();
        all.remove(0);
        return all;
    }
}
