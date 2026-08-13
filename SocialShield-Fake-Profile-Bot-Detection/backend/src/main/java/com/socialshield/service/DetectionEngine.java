package com.socialshield.service;

import com.socialshield.entity.*;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class DetectionEngine {
 public Profile analyze(Profile p,long sameDevice,long sameIp){
  int score=0, confidence=50; List<String> r=new ArrayList<>();
  double ratio=p.getFollowers()==0?(p.getFollowing()>0?100:0):(double)p.getFollowing()/p.getFollowers();
  double engagement=p.getFollowers()==0?0:((p.getAverageLikes()+p.getAverageComments())/p.getFollowers())*100;

  if(p.getAccountAgeDays()<14){score+=18;confidence+=6;r.add("Very new account (<14 days)");}
  else if(p.getAccountAgeDays()<30){score+=10;confidence+=3;r.add("New account (<30 days)");}
  if(ratio>=30){score+=18;confidence+=5;r.add("Extreme following/follower ratio");}
  else if(ratio>=10){score+=10;confidence+=3;r.add("High following/follower ratio");}
  if(p.getPostsLast24h()>=80){score+=15;confidence+=5;r.add("Abnormally high posting velocity");}
  else if(p.getPostsLast24h()>=40){score+=8;r.add("High posting velocity");}
  if(engagement<0.5&&p.getFollowers()>=100){score+=12;confidence+=4;r.add("Very low engagement for follower count");}
  if(p.getFollowersGainedLast7d()>=Math.max(500,p.getFollowers()*2L)){score+=10;confidence+=4;r.add("Rapid follower growth");}
  if(!p.isProfilePicture()){score+=8;r.add("Missing profile picture");}
  if(!p.isBioPresent()){score+=5;r.add("Incomplete profile");}
  if(p.getUsername()!=null&&p.getUsername().matches(".*\\d{5,}.*")){score+=7;r.add("Suspicious numeric username pattern");}
  if(p.getSuspiciousLogins()>=5){score+=8;confidence+=4;r.add("Multiple suspicious login events");}
  if(sameDevice>=4){score+=10;confidence+=5;r.add("Device associated with multiple profiles");}
  if(sameIp>=6){score+=8;confidence+=4;r.add("IP associated with many profiles");}
  if(p.isVerified()){score-=12;r.add("Verified account reduces risk");}

  score=Math.max(0,Math.min(100,score)); confidence=Math.max(50,Math.min(98,confidence));
  p.setRiskScore(score);p.setConfidence(confidence);
  p.setStatus(score>=65?RiskStatus.BOT:score>=35?RiskStatus.SUSPICIOUS:RiskStatus.GENUINE);
  p.setSeverity(score>=80?RiskSeverity.CRITICAL:score>=65?RiskSeverity.HIGH:score>=35?RiskSeverity.MEDIUM:RiskSeverity.LOW);
  p.setReasons(String.join(" • ",r.isEmpty()?List.of("No significant risk signals detected"):r));
  return p;
 }
}
