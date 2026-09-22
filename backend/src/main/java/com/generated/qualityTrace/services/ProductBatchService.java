package com.generated.qualityTrace.services;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import com.generated.qualityTrace.constructors.ProductBatchDtoFactory;
import com.generated.qualityTrace.repositories.ProductBatchRepository;

@Service
public class ProductBatchService {
  private final ProductBatchRepository repo;

  public ProductBatchService(ProductBatchRepository repo) {
    this.repo = repo;
  }

  public List<Map<String, Object>> list() {
    return repo.findAll().stream().map(ProductBatchDtoFactory::toDto).toList();
  }
}
