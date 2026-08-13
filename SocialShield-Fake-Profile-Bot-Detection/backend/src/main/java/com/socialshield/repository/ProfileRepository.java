package com.socialshield.repository;
import com.socialshield.entity.Profile;
import com.socialshield.entity.RiskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface ProfileRepository extends JpaRepository<Profile,Long>{
 long countByStatus(RiskStatus status);
 List<Profile> findByUsernameContainingIgnoreCase(String username);
}
