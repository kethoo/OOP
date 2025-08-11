package main;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

@WebListener
public class SessionListener implements HttpSessionListener {

    public static final String SHOPPING_CART_ATTR = "shoppingCart";

    public void sessionCreated(HttpSessionEvent se) {
        HttpSession session = se.getSession();
        ShoppingCart cart = new ShoppingCart();
        session.setAttribute(SHOPPING_CART_ATTR, cart);
        System.out.println("New shopping cart created for session: " + session.getId());
    }

    public void sessionDestroyed(HttpSessionEvent se) {
        HttpSession session = se.getSession();
        session.removeAttribute(SHOPPING_CART_ATTR);
        System.out.println("Shopping cart removed for session: " + session.getId());
    }
}