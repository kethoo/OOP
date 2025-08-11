package main;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public LoginServlet() {
        super();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        AccountManager accountManager = (AccountManager)
                getServletContext().getAttribute(MyContextListener.ACCOUNT_MANAGER_ATTR);

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (accountManager.isValidPassword(username, password)) {
            request.setAttribute("username", username);
            RequestDispatcher dispatcher = request.getRequestDispatcher("welcome.jsp");
            dispatcher.forward(request, response);
        } else {
            RequestDispatcher dispatcher = request.getRequestDispatcher("login-error.jsp");
            dispatcher.forward(request, response);
        }
    }
}