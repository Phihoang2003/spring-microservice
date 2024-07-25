package com.hoangphi.inventory_service.service;

import com.hoangphi.inventory_service.dto.InventoryResponse;
import com.hoangphi.inventory_service.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {
    private final InventoryRepository inventoryRepository;
   @Transactional(readOnly = true)
    public List<InventoryResponse> inStock(List<String> skuCodes){
       return inventoryRepository.findBySkuCodeIn(skuCodes).stream()
               .map(inventory->InventoryResponse.builder()
                          .skuCode(inventory.getSkuCode())
                          .isInStock(inventory.getQuantity()>0)
                          .build()
               ).toList();
    }
}
