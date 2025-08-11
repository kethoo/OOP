package main;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/AddToCartServlet")
public class AddToCartServlet extends HttpServlet {

    public AddToCartServlet() {
        super();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String productID = request.getParameter("productID");

        HttpSession session = request.getSession();
        ShoppingCart cart = (ShoppingCart) session.getAttribute(SessionListener.SHOPPING_CART_ATTR);

        if (cart != null && productID != null) {
            cart.addItem(productID);
        }

        RequestDispatcher dispatcher = request.getRequestDispatcher("shopping-cart.jsp");
        dispatcher.forward(request, response);
    }
}