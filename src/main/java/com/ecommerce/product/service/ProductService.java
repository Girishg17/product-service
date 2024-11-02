package com.ecommerce.product.service;

import com.ecommerce.product.model.entity.Product;
import com.ecommerce.product.request.ProductRequest;
import com.ecommerce.product.request.ProductUpdate;
import com.ecommerce.product.response.AllProductRes;
import com.ecommerce.product.response.ProdResponse;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

public interface ProductService {
    List<AllProductRes> getAllProduct();
    List<AllProductRes> getProductByCategory(Long Id);
    List<ProdResponse> getAllProductOfMerchant(Long merchantId);
    public void deleteProduct(Long id);
    void updateProduct(Long id, ProductUpdate p) throws IOException;
    public void updateProductRating(Long ProductId,double rating);
    void updateStockofProduct(Product p);
    void addproductswithCloudinary(ProductRequest productRequest, Long merchantId) throws IOException;
    Product getProductById(Long id);
}
