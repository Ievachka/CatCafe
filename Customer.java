import java.util.List;

public class Customer {

    private String name;
    private List<MenuItem> order;

    public Customer(String name, List<MenuItem> order) {
        this.name = name;
        this.order = order;
    }

    public String getName() {
        return name;
    }

    public List<MenuItem> getOrder() {
        return order;
    }

    public void speak() {
        System.out.print(name + ": Hello! I would like ");

        for (int i = 0; i < order.size(); i++) {
            System.out.print(order.get(i));

            if (i < order.size() - 1) {
                System.out.print(" and ");
            }
        }

        System.out.println(", please!");
    }
}

