package main;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class StoreContextListener implements ServletContextListener {

    public static final String PRODUCT_CATALOG_ATTR = "productCatalog";

    public void contextInitialized(ServletContextEvent sce) {
        ProductCatalog productCatalog = new ProductCatalog();
        ServletContext context = sce.getServletContext();
        context.setAttribute(PRODUCT_CATALOG_ATTR, productCatalog);
        System.out.println("ProductCatalog initialized and stored in ServletContext");
    }

    public void contextDestroyed(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        context.removeAttribute(PRODUCT_CATALOG_ATTR);
        try {
            DBConnection.closeDataSource();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("ServletContext cleaned up");
    }
}