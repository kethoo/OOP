<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="main.ProductCatalog, main.Product, main.StoreContextListener" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <%
        String productId = request.getParameter("id");
        ProductCatalog catalog = (ProductCatalog) application.getAttribute(StoreContextListener.PRODUCT_CATALOG_ATTR);
        Product product = null;
        if (catalog != null && productId != null) {
            product = catalog.getProductById(productId);
        }
        String productName = (product != null) ? product.getName() : "Product Not Found";
    %>
    <title><%= productName %> - Mozilla Firefox</title>
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
            max-width: 400px;
            text-align: center;
        }
        h1 {
            font-size: 18px;
            margin-bottom: 15px;
        }
        img {
            max-width: 200px;
            margin: 10px 0;
        }
        .price-section {
            margin: 15px 0;
        }
        input[type="submit"] {
            padding: 5px 10px;
            margin-left: 10px;
        }
    </style>
</head>
<body>
<div class="container">
    <% if (product != null) { %>
    <h1><%= product.getName() %></h1>

    <img src="store-images/<%= product.getImageFile() %>" alt="<%= product.getName() %>">

    <div class="price-section">
        $<%= product.getPrice() %>
        <form action="AddToCartServlet" method="post" style="display: inline;">
            <input name="productID" type="hidden" value="<%= product.getProductId() %>"/>
            <input type="submit" value="Add to Cart">
        </form>
    </div>

    <p><a href="shopping-cart.jsp">View Shopping Cart</a></p>
    <% } else { %>
    <h1>Product Not Found</h1>
    <p>The requested product could not be found.</p>
    <% } %>
</div>
</body>
</html>