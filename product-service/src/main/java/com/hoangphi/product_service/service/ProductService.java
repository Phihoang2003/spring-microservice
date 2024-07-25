package com.hoangphi.product_service.service;

import com.hoangphi.product_service.dto.ProductRequest;
import com.hoangphi.product_service.dto.ProductResponse;
import com.hoangphi.product_service.model.Product;
import com.hoangphi.product_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;
    public void createProduct(ProductRequest productRequest){
        Product product= Product.builder()
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .build();

        productRepository.save(product);
        log.info("Product {} is created", product.getId());
    }

    public ProductResponse getProductById(String id){
        Product product= productRepository.findById(id).orElseThrow(()->new RuntimeException("Product not found"));
        return mapToProductResponse(product);
    }

    public List<ProductResponse> getAllProducts(){
        List<Product> products= productRepository.findAll();
        return products.stream().map(this::mapToProductResponse).toList();
    }
    private ProductResponse mapToProductResponse(Product product){
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .build();
    }
}
