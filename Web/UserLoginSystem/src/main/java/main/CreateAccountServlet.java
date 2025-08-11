package main;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/CreateAccountServlet")
public class CreateAccountServlet extends HttpServlet {

    public CreateAccountServlet() {
        super();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        AccountManager accountManager = (AccountManager)
                getServletContext().getAttribute(MyContextListener.ACCOUNT_MANAGER_ATTR);

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (accountManager.createAccount(username, password)) {
            request.setAttribute("username", username);
            RequestDispatcher dispatcher = request.getRequestDispatcher("welcome.jsp");
            dispatcher.forward(request, response);
        } else {
            request.setAttribute("requestedUsername", username);
            RequestDispatcher dispatcher = request.getRequestDispatcher("name-in-use.jsp");
            dispatcher.forward(request, response);
        }
    }
}