package main;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class MyContextListener implements ServletContextListener {

    public static final String ACCOUNT_MANAGER_ATTR = "accountManager";

    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        context.setAttribute(ACCOUNT_MANAGER_ATTR, new AccountManager());
    }

    public void contextDestroyed(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        context.removeAttribute(ACCOUNT_MANAGER_ATTR);
    }
}