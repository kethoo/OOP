package main;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class ShoppingCart {
    private final Map<String, Integer> items;
    private final ProductCatalog catalog;

    public ShoppingCart() {
        items = new HashMap<>();
        catalog = new ProductCatalog();
    }

    public void addItem(String productId) {
        items.put(productId, items.getOrDefault(productId, 0) + 1);
    }

    public void updateQuantity(String productId, int quantity) {
        if (quantity <= 0) {
            items.remove(productId);
        } else {
            items.put(productId, quantity);
        }
    }

    public Map<String, Integer> getItems() {
        return items;
    }

    public BigDecimal getTotalCost() {
        BigDecimal total = BigDecimal.ZERO;

        for (Map.Entry<String, Integer> entry : items.entrySet()) {
            Product product = catalog.getProductById(entry.getKey());
            if (product != null) {
                BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(entry.getValue()));
                total = total.add(itemTotal);
            }
        }

        return total;
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }
}