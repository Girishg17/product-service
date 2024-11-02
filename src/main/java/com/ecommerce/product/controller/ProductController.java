package com.ecommerce.product.controller;

import com.ecommerce.product.model.entity.Product;
import com.ecommerce.product.request.ProductRequest;
import com.ecommerce.product.request.ProductUpdate;
import com.ecommerce.product.response.AllProductRes;
import com.ecommerce.product.response.ProdResponse;
import com.ecommerce.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {
    @Autowired
    private ProductService productService;

    @PostMapping("/{merchantId}/upld")
    public ResponseEntity<String> uploadfile(
            @ModelAttribute ProductRequest productRequest,
            @PathVariable Long merchantId
    ) throws IOException {
        productService.addproductswithCloudinary(productRequest, merchantId);
        return ResponseEntity.ok("Product created successfully");

    }

    @GetMapping("/all")
    public List<AllProductRes> findAllProducts() {
        return productService.getAllProduct();
    }

    @GetMapping("/category/{categoryId}")
    public List<AllProductRes>getProductByCategory(@PathVariable Long categoryId){
        return productService.getProductByCategory(categoryId);
    }

    @GetMapping("/{merchantId}/products")
    public ResponseEntity<List<ProdResponse>> getAllProductsOfMerchant(@PathVariable Long merchantId) {
        List<ProdResponse> products = productService.getAllProductOfMerchant(merchantId);
        return ResponseEntity.ok(products);
    }

    @DeleteMapping("/delete/{productId}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.ok("Product deleted successfully.");
    }

    @PostMapping("/update/product/{productId}")
    public ResponseEntity<String> updateProduct(
            @PathVariable Long productId,
            ProductUpdate p
    ) throws IOException {

        productService.updateProduct(productId, p);
        return ResponseEntity.ok("updated success");
    }

    @PostMapping("/setrating/{productId}")
    public  ResponseEntity<String>updateRating(@PathVariable Long productId,@RequestParam("rating") double rating){
        productService.updateProductRating(productId,rating);
        return ResponseEntity.ok("updated success");
    }

    @GetMapping("/products/{id}") //should be in product
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        if (product != null) {
            return ResponseEntity.ok(product);
        } else {
            return ResponseEntity.status(404).body(null);
        }
    }


    @PutMapping("/{id}/stock")
    public ResponseEntity<Product> updateProductStock(@PathVariable Long id, @RequestBody Product product) {
        // Ensure the product ID in the URL matches the product ID in the request body
        if (!id.equals(product.getId())) {
            return ResponseEntity.badRequest().build();
        }

        try {
            productService.updateStockofProduct(product);
            return ResponseEntity.ok(product);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }


}
