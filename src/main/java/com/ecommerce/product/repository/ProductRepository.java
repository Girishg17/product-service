package com.ecommerce.product.repository;

import com.ecommerce.product.model.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository  extends JpaRepository<Product,Long> {
    List<Product> findAllByDeletedFalse();
//    List<Product> findAllByCategoryNameAndDeletedFalse(String categoryName);
    List<Product> findAllByCategoryIdAndDeletedFalse(Long categoryId);
   List<Product> findByMerchantIdAndDeletedFalse(Long merchantId);

}
