package com.ecommerce.product.repository;

import com.ecommerce.product.model.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository  extends JpaRepository<Product,Long> {
    List<Product> findAllByDeletedFalse();
//    List<Product> findAllByCategoryNameAndDeletedFalse(String categoryName);
    List<Product> findAllByCategoryIdAndDeletedFalse(Long categoryId);
}
