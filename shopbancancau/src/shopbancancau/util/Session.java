package shopbancancau.util;

import shopbancancau.model.User;

public class Session {
    public static User currentUser = null;

    public static void logout() {
        currentUser = null;
    }
}
