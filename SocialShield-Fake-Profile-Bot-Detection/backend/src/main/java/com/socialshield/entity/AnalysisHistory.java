package com.socialshield.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity @Table(name="analysis_history")
public class AnalysisHistory {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 private Long profileId; private int riskScore; private int confidence;
 @Enumerated(EnumType.STRING) private RiskStatus status;
 @Column(length=2500) private String reasons;
 private LocalDateTime analyzedAt;
 public AnalysisHistory(){}
 public AnalysisHistory(Long p,int r,int c,RiskStatus s,String why){profileId=p;riskScore=r;confidence=c;status=s;reasons=why;analyzedAt=LocalDateTime.now();}
 public Long getId(){return id;} public Long getProfileId(){return profileId;} public int getRiskScore(){return riskScore;}
 public int getConfidence(){return confidence;} public RiskStatus getStatus(){return status;} public String getReasons(){return reasons;} public LocalDateTime getAnalyzedAt(){return analyzedAt;}
}
