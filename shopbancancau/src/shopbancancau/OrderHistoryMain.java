package shopbancancau;

import shopbancancau.controller.OrderHistoryController;
import shopbancancau.view.OrderHistoryView;

public class OrderHistoryMain {

    public static void main(String[] args) {
        OrderHistoryView view = new OrderHistoryView();
        new OrderHistoryController(view);
        view.setVisible(true);
    }
}
