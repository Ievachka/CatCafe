import java.util.ArrayList;
import java.util.List;

public class ItemLibrary {

    // The single source of truth for the cafe menu (7 items).
    public static List<MenuItem> getAllItems() {
        List<MenuItem> items = new ArrayList<>();
        items.add(new MenuItem("Latte",         "\u2615", 3, "latte.png"));
        items.add(new MenuItem("Espresso",      "\u2615", 2, "espresso.png"));
        items.add(new MenuItem("Donut",         "\uD83C\uDF69", 3, "donut.png"));
        items.add(new MenuItem("Cake",          "\uD83C\uDF70", 2, "cake.png"));
        items.add(new MenuItem("Brownie",       "\uD83C\uDF6B", 4, "brownie.png"));
        items.add(new MenuItem("Green tea",     "\uD83C\uDF75", 2, "greentea.png"));
        items.add(new MenuItem("Hot chocolate", "\uD83C\uDF75", 4, "hotchocolate.png"));
        return items;
    }

    public static MenuItem getRandomItem() {
        List<MenuItem> items = getAllItems();
        return items.get((int) (Math.random() * items.size()));
    }
}
