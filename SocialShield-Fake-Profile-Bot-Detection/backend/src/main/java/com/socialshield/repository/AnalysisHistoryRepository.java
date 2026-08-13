package com.socialshield.repository;
import com.socialshield.entity.AnalysisHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface AnalysisHistoryRepository extends JpaRepository<AnalysisHistory,Long>{
 List<AnalysisHistory> findByProfileIdOrderByAnalyzedAtDesc(Long profileId);
}
