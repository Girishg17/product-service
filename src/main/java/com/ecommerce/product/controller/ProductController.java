package com.ecommerce.product.controller;

import com.ecommerce.product.request.ProductUpdate;
import com.ecommerce.product.response.AllProductRes;
import com.ecommerce.product.response.ProdResponse;
import com.ecommerce.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {
    @Autowired
    private ProductService productService;

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

}
