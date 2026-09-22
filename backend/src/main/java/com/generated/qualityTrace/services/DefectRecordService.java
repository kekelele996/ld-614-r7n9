package com.generated.qualityTrace.services;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import com.generated.qualityTrace.constructors.DefectRecordDtoFactory;
import com.generated.qualityTrace.repositories.DefectRecordRepository;

@Service
public class DefectRecordService {
  private final DefectRecordRepository repo;

  public DefectRecordService(DefectRecordRepository repo) {
    this.repo = repo;
  }

  public List<Map<String, Object>> list() {
    return repo.findAll().stream().map(DefectRecordDtoFactory::toDto).toList();
  }
}
