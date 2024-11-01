package com.ecommerce.product.service;

import com.ecommerce.product.response.AllProductRes;
import org.springframework.stereotype.Service;

import java.util.List;

public interface ProductService {
    List<AllProductRes> getAllProduct();
}
