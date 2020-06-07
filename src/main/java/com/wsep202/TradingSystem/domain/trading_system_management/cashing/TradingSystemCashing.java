package com.wsep202.TradingSystem.domain.trading_system_management.cashing;

import com.wsep202.TradingSystem.domain.trading_system_management.Product;
import com.wsep202.TradingSystem.domain.trading_system_management.ShoppingCart;
import com.wsep202.TradingSystem.domain.trading_system_management.Store;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
@Data
public class TradingSystemCashing {

    private Map<String, ShoppingCart> shoppingCartMap;

    private Set<Product> products;

    public TradingSystemCashing (){
        shoppingCartMap = new HashMap<>();
        products = new HashSet<>();
    }

    public void addProduct(Product product){
        products.add(product);
    }

    public void removeProduct(int productSn){
        products.stream()
                .filter(product -> product.getProductSn() == productSn)
                .findFirst()
                .map(product -> products.remove(product));
    }

    public void editProduct(Product product){
        removeProduct(product.getProductSn());
        addProduct(product);
    }

    public Set<Product> getProducts(){
        return new HashSet<>(products);
    }

    public void addShoppingCart(String username, ShoppingCart shoppingCart){
        shoppingCartMap.put(username, shoppingCart);
    }

    public void addShoppingCart(String username){
        shoppingCartMap.remove(username);
    }

    public void editShoppingCart(String username, ShoppingCart shoppingCart){
        removeShoppingCart(username);
        addShoppingCart(username, shoppingCart);
    }

    public ShoppingCart removeShoppingCart(String username){
        return shoppingCartMap.remove(username);
    }

    public ShoppingCart getShoppingCart(String username) {
        return shoppingCartMap.get(username);
    }

    public void addProducts(Set<Product> productSet) {
        products = productSet;
    }

}
