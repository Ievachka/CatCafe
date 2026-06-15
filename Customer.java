import java.util.List;

public class Customer {

    private String name;
    private List<MenuItem> order;
    private String introText;
    private String successText;
    private String failText;

    public Customer(String name, List<MenuItem> order, String introText, String successText, String failText) {
        this.name = name;
        this.order = order;
        this.introText = introText;
        this.successText = successText;
        this.failText = failText;
    }

    public String getName() {
        return name;
    }

    public void speakIntro() {
        System.out.println(name + ": " + introText);
    }

    public void speakSuccess() {
        System.out.println(name + ": " + successText);
    }

    public void speakFail() {
        System.out.println(name + ": " + failText);
    }

}