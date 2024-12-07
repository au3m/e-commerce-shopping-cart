package com.u3.shoppingcart.service.product;

import com.u3.shoppingcart.dto.ImageDto;
import com.u3.shoppingcart.dto.ProductDto;
import com.u3.shoppingcart.exceptions.AlreadyEsistsException;
import com.u3.shoppingcart.exceptions.ProductNotFoundException;
import com.u3.shoppingcart.model.Category;
import com.u3.shoppingcart.model.Image;
import com.u3.shoppingcart.model.Product;
import com.u3.shoppingcart.repository.CategoryRepository;
import com.u3.shoppingcart.repository.ImageRepository;
import com.u3.shoppingcart.repository.ProductRepository;
import com.u3.shoppingcart.request.AddProductRequest;
import com.u3.shoppingcart.request.ProductUpdateRequest;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService implements IProductService{
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;
    private final ImageRepository imageRepository;

    public ProductService(CategoryRepository categoryRepository, ProductRepository productRepository, ModelMapper modelMapper, ImageRepository imageRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
        this.imageRepository = imageRepository;
    }

    @Override
    public Product addProduct(AddProductRequest addProductRequest) {
        // check if the category is found in the DB
        // if yes, set it as the new product category
        // if no, the save it as a new category
        // the set as the new product category
        if(productExists(addProductRequest.getName(), addProductRequest.getBrand())){
            throw new AlreadyEsistsException(addProductRequest.getBrand() + " " +addProductRequest.getName()+ " already exists");
        }

        Category category = Optional.ofNullable(categoryRepository.findByName(addProductRequest.getCategory().getName()))
                .orElseGet(() -> {
                    Category newCategory = new Category(addProductRequest.getCategory().getName());
                    return categoryRepository.save(newCategory);
                });
        addProductRequest.setCategory(category);
        return productRepository.save(createProduct(addProductRequest, category));
    }

    private Product createProduct(AddProductRequest addProductRequest, Category category) {
        return new Product(
                addProductRequest.getName(),
                addProductRequest.getBrand(),
                addProductRequest.getPrice(),
                addProductRequest.getInventory(),
                addProductRequest.getDescription(),
                category
        );
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));
    }

    @Override
    public void deleteProductById(Long id) {
        productRepository.findById(id).ifPresentOrElse(productRepository::delete, () -> {throw new ProductNotFoundException("Product not found!");} );
    }

    @Override
    public Product updateProduct(ProductUpdateRequest productUpdateRequest, Long productId) {

        return productRepository.findById(productId)
                .map(existingProduct -> updateExistingProduct(existingProduct, productUpdateRequest))
                .map(productRepository::save)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));
    }

    private Product updateExistingProduct(Product existingProduct, ProductUpdateRequest productUpdateRequest) {
        existingProduct.setName(productUpdateRequest.getName());
        existingProduct.setBrand(productUpdateRequest.getBrand());
        existingProduct.setPrice(productUpdateRequest.getPrice());
        existingProduct.setInventory(productUpdateRequest.getInventory());
        existingProduct.setDescription(productUpdateRequest.getDescription());
        Category category = categoryRepository.findByName(productUpdateRequest.getCategory().getName());
        existingProduct.setCategory(category);
        return existingProduct;

    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategoryName(category);
    }

    @Override
    public List<Product> getProductsByBrand(String brand) {
        return productRepository.findByBrand(brand);
    }

    @Override
    public List<Product> getProductsByCategoryAndBrand(String category, String brand) {
        return productRepository.findByCategoryNameAndBrand(category, brand);
    }

    @Override
    public List<Product> getProductsByName(String name) {
        return productRepository.findByName(name);
    }

    @Override
    public List<Product> getProductsByBrandAndName(String brand, String name) {
        return productRepository.findByBrandAndName(brand, name);
    }

    @Override
    public Long countProductsByBrandAndName(String brand, String name) {
        return productRepository.countByBrandAndName(brand, name);
    }

    @Override
    public List<ProductDto> getConvertedProducts(List<Product> products){
        return products.stream()
                .map(this::convertToDto)
                .toList();
    }

    private boolean productExists(String name, String brand){
        return productRepository.existsByNameAndBrand(name, brand);
    }


    @Override
    public ProductDto convertToDto(Product product){
        ProductDto productDto = modelMapper.map(product, ProductDto.class);
        List<Image> images = imageRepository.findByProductId(product.getId());
        List<ImageDto> imageDtos = images.stream()
                .map(image -> modelMapper.map(image, ImageDto.class))
                .toList();
        productDto.setImages(imageDtos);
        return productDto;
    }

}
