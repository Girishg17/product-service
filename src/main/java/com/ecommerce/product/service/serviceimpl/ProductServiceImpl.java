package com.ecommerce.product.service.serviceimpl;

import com.ecommerce.product.model.dto.CategoryDTO;
import com.ecommerce.product.model.entity.Product;
import com.ecommerce.product.repository.ProductRepository;
import com.ecommerce.product.response.AllProductRes;
import com.ecommerce.product.service.ProductService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public List<AllProductRes> getAllProduct() {
        List<Product> products = productRepository.findAllByDeletedFalse();
        List<AllProductRes> allProductResponses = new ArrayList<>();
        for (Product product : products) {
            AllProductRes response = modelMapper.map(product, AllProductRes.class);
            Long categoryId = product.getCategoryId();
            CategoryDTO category = fetchCategoryById(categoryId);
            response.setCategory(category.getName());
            allProductResponses.add(response);
        }

        return allProductResponses;
    }

    private CategoryDTO fetchCategoryById(Long categoryId) {
        String url = "http://localhost:8082/api/categories/fetch/" + categoryId;
        return restTemplate.getForObject(url, CategoryDTO.class);
    }
}