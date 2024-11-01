package com.ecommerce.product.controller;

import com.ecommerce.product.response.AllProductRes;
import com.ecommerce.product.response.ProdResponse;
import com.ecommerce.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


}
