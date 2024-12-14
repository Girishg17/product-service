package com.ecommerce.product.service.serviceimpl;

import com.cloudinary.Cloudinary;
import com.ecommerce.product.model.dto.CategoryDTO;
import com.ecommerce.product.model.entity.Product;
import com.ecommerce.product.repository.ProductRepository;
import com.ecommerce.product.request.ProductRequest;
import com.ecommerce.product.request.ProductUpdate;
import com.ecommerce.product.response.AllProductRes;
import com.ecommerce.product.response.ProdResponse;
import com.ecommerce.product.service.ProductService;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private Cloudinary cloudinary;

    @Override
    public void addproductswithCloudinary(ProductRequest productRequest, Long merchantId) throws IOException {
        String imageUrl = upload(productRequest.getImage());


        Product product = new Product();
        product.setName(productRequest.getName());
        product.setUsp(productRequest.getUsp());
        product.setDescription(productRequest.getDescription());
        product.setFile(imageUrl);
        product.setPrice(productRequest.getPrice());
        product.setStock(productRequest.getStock());
        product.setMerchantId(merchantId);
        product.setCategoryId(productRequest.getCategoryId());
        product.setRating(0.0);
        product.setRatingCount(0);
        Product saved = productRepository.save(product);
        indexProductInElasticsearch(saved); //will
    }

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

    @Override
    public List<AllProductRes> getProductByCategory(Long Id) {
        CategoryDTO category = fetchCategoryById(Id);
        if (category == null) {
            throw new RuntimeException("Category not found");
        }
        List<Product> products = productRepository.findAllByCategoryIdAndDeletedFalse(Id);
        List<AllProductRes> allProductResponses = new ArrayList<>();
        for (Product product : products) {
            AllProductRes response = modelMapper.map(product, AllProductRes.class);
            response.setCategory(category.getName());

            allProductResponses.add(response);
        }

        return allProductResponses;
    }

    @Override
    public List<ProdResponse> getAllProductOfMerchant(Long merchantId) {
        List<Product> products = productRepository.findByMerchantIdAndDeletedFalse(merchantId);
        return products.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new EntityNotFoundException("Product with ID " + id + " not found");
        }
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product with ID " + id + " not found"));

        product.setDeleted(true);
        productRepository.save(product);
        String url="http://localhost:8085/products/search/delete/"+id;
        try {
            restTemplate.delete(url);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete product in productSearch microservice", e);
        }
    }

    @Override
    public void updateProduct(Long id, ProductUpdate p) throws IOException {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (p.getImage() != null && !p.getImage().isEmpty()) {
            String imageUrl = upload(p.getImage());
            existingProduct.setFile(imageUrl);
        }

        if(p.getCategoryId()!=null){
            CategoryDTO category = fetchCategoryById(p.getCategoryId());
            existingProduct.setCategoryId(category.getId());
        }
        existingProduct.setName(p.getName());
        existingProduct.setPrice(p.getPrice());
        existingProduct.setStock(p.getStock());


        Product existing = productRepository.save(existingProduct);
        indexProductInElasticsearch(existing);//will
    }


    @Override
    public void updateProductRating(Long ProductId, double rating) {
        Product existingProduct = productRepository.findById(ProductId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        Integer currentRatingCount = existingProduct.getRatingCount();
        Double currentRating = existingProduct.getRating();
        double newRating = calculateNewRating(rating, currentRatingCount, currentRating);
        existingProduct.setRating(newRating);
        existingProduct.setRatingCount(existingProduct.getRatingCount() + 1);
        updateRatingCartItem(newRating,ProductId);
        Product existing = productRepository.save(existingProduct);
        indexProductInElasticsearch(existing);// will
    }


    //Others
    public void updateStockofProduct(Product p) {
        Product existingProduct = productRepository.findById(p.getId())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        existingProduct.setStock(p.getStock());
        Product existing = productRepository.save(existingProduct);

  indexProductInElasticsearch(existing);//will
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    private double calculateNewRating(double rating, int currentRatingCount, double currentRating) {
        double newRating = ((currentRating * currentRatingCount) + rating) / (currentRatingCount + 1);
        newRating = Math.min(5.0, Math.round(newRating * 10) / 10.0);
        return newRating;
    }
    private ProdResponse convertToResponse(Product product) {
        return modelMapper.map(product, ProdResponse.class);
    }

    private void updateRatingCartItem(double rating, Long productId) {
        String url = "http://localhost:8082/api/order/rating/update/" + productId;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestPayload = Map.of("rating", rating);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestPayload, headers);

        try {
            ResponseEntity<Void> response = restTemplate.postForEntity(url, request, Void.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("Product rating updated successfully in the external service.");
            } else {
                System.out.println("Failed to update product rating in the external service.");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error updating product rating in the external service", e);
        }
    }


    private CategoryDTO fetchCategoryById(Long categoryId) {
        System.out.println("cateid"+ categoryId);
        String url = "http://localhost:8082/api/categories/fetch/" + categoryId;
        return restTemplate.getForObject(url, CategoryDTO.class);
    }
    private String upload(MultipartFile file) throws IOException {
        Map data=cloudinary.uploader().upload(file.getBytes(),Map.of());
        return data.get("url").toString();
    }

    private void indexProductInElasticsearch(Product product) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Product> request = new HttpEntity<>(product, headers);

        ResponseEntity<Void> response = restTemplate.postForEntity("http://localhost:8085/products/search/index", request, Void.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            System.out.println("Product indexed successfully in Elasticsearch.");
        } else {
            System.out.println("Failed to index product in Elasticsearch.");
        }
    }
}
