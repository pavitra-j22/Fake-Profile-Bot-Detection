package com.socialshield.service;

import com.socialshield.entity.*;
import com.socialshield.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
public class ProfileService {
 private final ProfileRepository profiles; private final AnalysisHistoryRepository history; private final DetectionEngine engine;
 public ProfileService(ProfileRepository p,AnalysisHistoryRepository h,DetectionEngine e){profiles=p;history=h;engine=e;}

 @Transactional public Profile create(Profile p){
  long d=p.getDeviceId()==null?0:profiles.findAll().stream().filter(x->p.getDeviceId().equals(x.getDeviceId())).count();
  long ip=p.getIpAddress()==null?0:profiles.findAll().stream().filter(x->p.getIpAddress().equals(x.getIpAddress())).count();
  Profile saved=profiles.save(engine.analyze(p,d+1,ip+1));
  history.save(new AnalysisHistory(saved.getId(),saved.getRiskScore(),saved.getConfidence(),saved.getStatus(),saved.getReasons()));
  return saved;
 }
 public List<Profile> find(String q){return q==null||q.isBlank()?profiles.findAll():profiles.findByUsernameContainingIgnoreCase(q);}
 public Profile get(Long id){return profiles.findById(id).orElseThrow(()->new IllegalArgumentException("Profile not found: "+id));}
 @Transactional public Profile analyze(Long id){
  Profile p=get(id);
  long d=p.getDeviceId()==null?0:profiles.findAll().stream().filter(x->p.getDeviceId().equals(x.getDeviceId())).count();
  long ip=p.getIpAddress()==null?0:profiles.findAll().stream().filter(x->p.getIpAddress().equals(x.getIpAddress())).count();
  profiles.save(engine.analyze(p,d,ip));
  history.save(new AnalysisHistory(p.getId(),p.getRiskScore(),p.getConfidence(),p.getStatus(),p.getReasons()));
  return p;
 }
 public List<AnalysisHistory> history(Long id){return history.findByProfileIdOrderByAnalyzedAtDesc(id);}
 @Transactional public void delete(Long id){history.deleteAll(history.findByProfileIdOrderByAnalyzedAtDesc(id));profiles.deleteById(id);}
 public long total(){return profiles.count();} public long genuine(){return profiles.countByStatus(RiskStatus.GENUINE);}
 public long suspicious(){return profiles.countByStatus(RiskStatus.SUSPICIOUS);} public long bots(){return profiles.countByStatus(RiskStatus.BOT);}
}
