package com.u3.shoppingcart.controller;

import com.u3.shoppingcart.dto.ProductDto;
import com.u3.shoppingcart.exceptions.AlreadyEsistsException;
import com.u3.shoppingcart.exceptions.ResourceNOtFoundException;
import com.u3.shoppingcart.model.Product;
import com.u3.shoppingcart.request.AddProductRequest;
import com.u3.shoppingcart.request.ProductUpdateRequest;
import com.u3.shoppingcart.response.ApiResponse;
import com.u3.shoppingcart.service.product.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("${api.prefix}/products")
public class ProductController {
    private final IProductService productService;

    public ProductController(IProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllProducts(){
        try {
            List<Product> products = productService.getAllProducts();
            List<ProductDto> convertedProducts = productService.getConvertedProducts(products);
            return ResponseEntity.ok(new ApiResponse("success", convertedProducts));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse("Error:", INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("product/{id}/product")
    public ResponseEntity<ApiResponse> getProductById(@PathVariable Long id){
        try {
            Product product = productService.getProductById(id);
            ProductDto productDto = productService.convertToDto(product);
            return ResponseEntity.ok(new ApiResponse("success", productDto));
    } catch (ResourceNOtFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addProduct (@RequestBody AddProductRequest product){
        try {
            Product product1 = productService.addProduct(product);
            return ResponseEntity.ok(new ApiResponse("add product success", product1));
        } catch (AlreadyEsistsException e) {
            return ResponseEntity.status(CONFLICT).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("product/{productId}/update")
    public ResponseEntity<ApiResponse> updateProduct(@RequestBody ProductUpdateRequest request, @PathVariable Long productId){
        try {
            Product product = productService.updateProduct(request, productId);
            return ResponseEntity.ok(new ApiResponse("Update product success!", product));
        } catch (ResourceNOtFoundException e){
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/product/{productId}/delete")
    public ResponseEntity<ApiResponse> deleteProduct(@PathVariable Long productId){
        try {
            productService.deleteProductById(productId);
            return ResponseEntity.ok(new ApiResponse("Delete product success!", productId));
        } catch (ResourceNOtFoundException e){
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("/by/brand-and-name")
    public ResponseEntity<ApiResponse> getProductByBrandAndName(@RequestParam String brandName, @RequestParam String productName){
        try {
            List<Product> products = productService.getProductsByBrandAndName(brandName, productName);
            List<ProductDto> convertedProducts = productService.getConvertedProducts(products);
            if (products.isEmpty()){
                return ResponseEntity.status(NOT_FOUND).body(new ApiResponse("No products found", null));
            }
            return ResponseEntity.ok(new ApiResponse("success", convertedProducts));
        } catch (Exception e){
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("/products/by/category-and-brand")
    public ResponseEntity<ApiResponse> getProductByCategoryAndBrand(@RequestParam String category, @RequestParam String brand){
        try {
            List<Product> products = productService.getProductsByCategoryAndBrand(category, brand);
            List<ProductDto> convertedProducts = productService.getConvertedProducts(products);
            if (products.isEmpty()){
                return ResponseEntity.status(NOT_FOUND).body(new ApiResponse("No products found", null));}
            return ResponseEntity.ok(new ApiResponse("success", convertedProducts));
        } catch (Exception e){
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));}
    }

    @GetMapping("/products/{name}/products")
    public ResponseEntity<ApiResponse> getProductByName(@PathVariable String name){
        try {
            List<Product> products = productService.getProductsByName(name);
            List<ProductDto> convertedProducts = productService.getConvertedProducts(products);
            if (products.isEmpty()){
                return ResponseEntity.status(NOT_FOUND).body(new ApiResponse("No products found", null));}
            return ResponseEntity.ok(new ApiResponse("success", convertedProducts));
        } catch (Exception e){
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));}
    }


    @GetMapping("/products/by-brand")
    public ResponseEntity<ApiResponse> findProductByBrand(@RequestParam String brand){
        try {
            List<Product> products = productService.getProductsByBrand(brand);
            List<ProductDto> convertedProducts = productService.getConvertedProducts(products);
            if (products.isEmpty()){
                return ResponseEntity.status(NOT_FOUND).body(new ApiResponse("No products found", null));}
            return ResponseEntity.ok(new ApiResponse("success", convertedProducts));
        } catch (Exception e){
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));}
    }


    @GetMapping("/product/{category}/all/products")
    public ResponseEntity<ApiResponse> findProductByCategroy(@PathVariable String category){
        try {
            List<Product> products = productService.getProductsByCategory(category);
            List<ProductDto> convertedProducts = productService.getConvertedProducts(products);
            if (products.isEmpty()){
                return ResponseEntity.status(NOT_FOUND).body(new ApiResponse("No products found", null));}
            return ResponseEntity.ok(new ApiResponse("success", convertedProducts));
        } catch (Exception e){
            return ResponseEntity.ok(new ApiResponse(e.getMessage(), null));}
    }

    @GetMapping("/products/count/by-brand/and-name")
    public ResponseEntity<ApiResponse> countProductsByBrandAndName(@RequestParam String brand, @RequestParam String name){
        try {
            var productCount = productService.countProductsByBrandAndName(brand, name);
            return ResponseEntity.ok(new ApiResponse("Product count!", productCount));
        } catch (Exception e){
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));}
    }







}
