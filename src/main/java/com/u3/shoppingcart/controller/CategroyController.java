package com.u3.shoppingcart.controller;

import com.u3.shoppingcart.exceptions.AlreadyEsistsException;
import com.u3.shoppingcart.exceptions.ResourceNOtFoundException;
import com.u3.shoppingcart.model.Category;
import com.u3.shoppingcart.response.ApiResponse;
import com.u3.shoppingcart.service.category.CategoryService;
import com.u3.shoppingcart.service.category.ICategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("${api.prefix}/categories")
public class CategroyController {

    private final ICategoryService categoryService;

    public CategroyController(ICategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllCategories(){
        try{
            List<Category> categories = categoryService.getAllCategories();
            return ResponseEntity.ok(new ApiResponse("Found!", categories));
        } catch (Exception e){
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addCategory(@RequestBody Category name){
        try {
            Category category = categoryService.addCategory(name);
            return ResponseEntity.ok(new ApiResponse("Success", category));
        } catch (AlreadyEsistsException e) {
            return ResponseEntity.status(CONFLICT).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("/category/{id}/categroy")
    public ResponseEntity<ApiResponse> getCategoryById(@PathVariable Long id)   {
        try {
            Category category = categoryService.getCategoryById(id);
            return ResponseEntity.ok(new ApiResponse("Found", category));
        } catch (ResourceNOtFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("/category/{name}/categroy")
    public ResponseEntity<ApiResponse> getCategoryByname(@PathVariable String name)   {
        try {
            Category category = categoryService.getCategoryByName(name);
            return ResponseEntity.ok(new ApiResponse("Found", category));
        } catch (ResourceNOtFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @DeleteMapping("/category/{id}/delete")
    public ResponseEntity<ApiResponse> deleteCategory(@PathVariable Long id)   {
        try {
            categoryService.deleteCategory(id);
            return ResponseEntity.ok(new ApiResponse("Found", null));
        } catch (ResourceNOtFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PutMapping("/category/{id}/update")
    public ResponseEntity<ApiResponse> deleteCategory(@PathVariable Long id, @RequestBody Category category)   {
        try {
            Category category1 =  categoryService.updateCategory(category, id);
            return ResponseEntity.ok(new ApiResponse("Found", category1));
        } catch (ResourceNOtFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }

}
