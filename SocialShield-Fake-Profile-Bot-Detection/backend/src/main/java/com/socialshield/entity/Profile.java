package com.socialshield.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Table(name="profiles", indexes={
 @Index(name="idx_username",columnList="username"),
 @Index(name="idx_status",columnList="status"),
 @Index(name="idx_device",columnList="deviceId"),
 @Index(name="idx_ip",columnList="ipAddress")
})
public class Profile {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @NotBlank @Column(nullable=false,unique=true) private String username;
 private String displayName;
 @Min(0) private long followers;
 @Min(0) private long following;
 @Min(0) private long posts;
 @Min(0) private int accountAgeDays;
 private boolean profilePicture;
 private boolean bioPresent;
 private boolean verified;
 @Min(0) private double averageLikes;
 @Min(0) private double averageComments;
 @Min(0) private int postsLast24h;
 @Min(0) private int followersGainedLast7d;
 @Min(0) private int suspiciousLogins;
 private String deviceId;
 private String ipAddress;
 private int riskScore;
 private int confidence;
 @Enumerated(EnumType.STRING) private RiskStatus status;
 @Enumerated(EnumType.STRING) private RiskSeverity severity;
 @Column(length=2500) private String reasons;
 private LocalDateTime createdAt;
 private LocalDateTime updatedAt;

 @PrePersist void create(){createdAt=LocalDateTime.now();updatedAt=createdAt;}
 @PreUpdate void update(){updatedAt=LocalDateTime.now();}

 public Long getId(){return id;}
 public String getUsername(){return username;} public void setUsername(String v){username=v;}
 public String getDisplayName(){return displayName;} public void setDisplayName(String v){displayName=v;}
 public long getFollowers(){return followers;} public void setFollowers(long v){followers=v;}
 public long getFollowing(){return following;} public void setFollowing(long v){following=v;}
 public long getPosts(){return posts;} public void setPosts(long v){posts=v;}
 public int getAccountAgeDays(){return accountAgeDays;} public void setAccountAgeDays(int v){accountAgeDays=v;}
 public boolean isProfilePicture(){return profilePicture;} public void setProfilePicture(boolean v){profilePicture=v;}
 public boolean isBioPresent(){return bioPresent;} public void setBioPresent(boolean v){bioPresent=v;}
 public boolean isVerified(){return verified;} public void setVerified(boolean v){verified=v;}
 public double getAverageLikes(){return averageLikes;} public void setAverageLikes(double v){averageLikes=v;}
 public double getAverageComments(){return averageComments;} public void setAverageComments(double v){averageComments=v;}
 public int getPostsLast24h(){return postsLast24h;} public void setPostsLast24h(int v){postsLast24h=v;}
 public int getFollowersGainedLast7d(){return followersGainedLast7d;} public void setFollowersGainedLast7d(int v){followersGainedLast7d=v;}
 public int getSuspiciousLogins(){return suspiciousLogins;} public void setSuspiciousLogins(int v){suspiciousLogins=v;}
 public String getDeviceId(){return deviceId;} public void setDeviceId(String v){deviceId=v;}
 public String getIpAddress(){return ipAddress;} public void setIpAddress(String v){ipAddress=v;}
 public int getRiskScore(){return riskScore;} public void setRiskScore(int v){riskScore=v;}
 public int getConfidence(){return confidence;} public void setConfidence(int v){confidence=v;}
 public RiskStatus getStatus(){return status;} public void setStatus(RiskStatus v){status=v;}
 public RiskSeverity getSeverity(){return severity;} public void setSeverity(RiskSeverity v){severity=v;}
 public String getReasons(){return reasons;} public void setReasons(String v){reasons=v;}
 public LocalDateTime getCreatedAt(){return createdAt;} public LocalDateTime getUpdatedAt(){return updatedAt;}
}
