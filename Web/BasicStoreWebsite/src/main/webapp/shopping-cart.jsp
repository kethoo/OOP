<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="main.ShoppingCart, main.ProductCatalog, main.Product, main.SessionListener, main.StoreContextListener, java.util.Map, java.math.BigDecimal" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Shopping Cart - Mozilla Firefox</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 20px;
            background-color: #f0f0f0;
        }
        .container {
            background-color: white;
            padding: 20px;
            border: 1px solid #ccc;
            max-width: 500px;
        }
        h1 {
            font-size: 18px;
            margin-bottom: 15px;
        }
        ul {
            list-style-type: disc;
            margin-left: 20px;
        }
        li {
            margin: 5px 0;
        }
        input[type="text"] {
            width: 30px;
            padding: 2px;
            margin-right: 5px;
        }
        input[type="submit"] {
            padding: 5px 10px;
            margin: 10px 5px;
        }
        a {
            color: blue;
            text-decoration: underline;
        }
        .total {
            font-weight: bold;
            margin: 15px 0;
        }
    </style>
</head>
<body>
<div class="container">
    <h1>Shopping Cart</h1>

    <%
        ShoppingCart cart = (ShoppingCart) session.getAttribute(SessionListener.SHOPPING_CART_ATTR);
        ProductCatalog catalog = (ProductCatalog) application.getAttribute(StoreContextListener.PRODUCT_CATALOG_ATTR);

        if (cart != null && !cart.isEmpty() && catalog != null) {
    %>
    <form action="UpdateCartServlet" method="post">
        <ul>
            <%
                for (Map.Entry<String, Integer> entry : cart.getItems().entrySet()) {
                    Product product = catalog.getProductById(entry.getKey());
                    if (product != null) {
            %>
            <li>
                <input type="text" name="quantity_<%= entry.getKey() %>" value="<%= entry.getValue() %>">
                <%= product.getName() %>, <%= product.getPrice() %>
            </li>
            <%
                    }
                }
            %>
        </ul>

        <div class="total">
            Total: $<%= cart.getTotalCost() %>
        </div>

        <input type="submit" value="Update Cart">
    </form>

    <p><a href="store-index.jsp">Continue Shopping</a></p>

    <% } else { %>
    <p>Your shopping cart is empty.</p>
    <p><a href="store-index.jsp">Continue Shopping</a></p>
    <% } %>
</div>
</body>
</html>