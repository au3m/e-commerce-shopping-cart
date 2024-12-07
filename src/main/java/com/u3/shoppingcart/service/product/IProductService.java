package com.u3.shoppingcart.service.product;

import com.u3.shoppingcart.dto.ProductDto;
import com.u3.shoppingcart.model.Product;
import com.u3.shoppingcart.request.AddProductRequest;
import com.u3.shoppingcart.request.ProductUpdateRequest;

import java.util.List;

public interface IProductService {
    Product addProduct(AddProductRequest addProductRequest);
    Product getProductById(Long id);
    void deleteProductById(Long id);
    Product updateProduct(ProductUpdateRequest productUpdateRequest, Long productId);
    List<Product> getAllProducts();
    List<Product> getProductsByCategory(String category);
    List<Product> getProductsByBrand(String brand);
    List<Product> getProductsByCategoryAndBrand(String category, String brand);
    List<Product> getProductsByName(String name);
    List<Product> getProductsByBrandAndName(String brand, String name);
    Long countProductsByBrandAndName(String brand, String name);

    List<ProductDto> getConvertedProducts(List<Product> products);

    ProductDto convertToDto(Product product);
}
