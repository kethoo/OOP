<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="main.ProductCatalog, main.Product, main.StoreContextListener, java.util.List" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Student Store - Mozilla Firefox</title>
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
            max-width: 600px;
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
            margin: 3px 0;
        }
        a {
            color: blue;
            text-decoration: underline;
        }
    </style>
</head>
<body>
<div class="container">
    <h1>Student Store</h1>

    <p>Items available:</p>

    <ul>
        <%
            ProductCatalog catalog = (ProductCatalog) application.getAttribute(StoreContextListener.PRODUCT_CATALOG_ATTR);
            if (catalog != null) {
                List<Product> products = catalog.getAllProducts();
                for (Product product : products) {
        %>
        <li><a href="show-product.jsp?id=<%= product.getProductId() %>"><%= product.getName() %></a></li>
        <%
                }
            }
        %>
    </ul>

    <p><a href="shopping-cart.jsp">View Shopping Cart</a></p>
</div>
</body>
</html>