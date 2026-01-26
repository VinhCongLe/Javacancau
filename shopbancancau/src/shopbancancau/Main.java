package shopbancancau;

import shopbancancau.controller.LoginController;
import shopbancancau.view.LoginView;

public class Main {

    public static void main(String[] args) {

    	 LoginView view = new LoginView();
    	    new LoginController(view);
    	    view.setVisible(true);
    }
}
