package main;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/UpdateCartServlet")
public class UpdateCartServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public UpdateCartServlet() {
        super();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        ShoppingCart cart = (ShoppingCart) session.getAttribute(SessionListener.SHOPPING_CART_ATTR);

        if (cart != null) {
            for (String productId : cart.getItems().keySet()) {
                String quantityStr = request.getParameter("quantity_" + productId);
                if (quantityStr != null) {
                    try {
                        int quantity = Integer.parseInt(quantityStr);
                        cart.updateQuantity(productId, quantity);
                    } catch (NumberFormatException e) {
                        cart.updateQuantity(productId, 0);
                    }
                }
            }
        }

        RequestDispatcher dispatcher = request.getRequestDispatcher("shopping-cart.jsp");
        dispatcher.forward(request, response);
    }
}