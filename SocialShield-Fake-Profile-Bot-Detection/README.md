# SocialShield — Fake Profile & Bot Detection Platform

Portfolio-grade full-stack project using Java 21 + Spring Boot 4.1 + MySQL + React/Vite.

Features:
- Profile ingestion
- Explainable weighted risk engine
- Risk score, confidence, severity
- GENUINE / SUSPICIOUS / BOT classification
- Behavioral signals: account age, follower/following ratio, posting velocity, engagement, rapid follower growth
- Profile completeness and verification signals
- Device/IP reuse signals
- Suspicious login signals
- Analysis history
- Dashboard statistics
- Searchable analyst console

## Run

### Database
```sql
CREATE DATABASE socialshield;
```

Edit `backend/src/main/resources/application.properties` and set your MySQL password.

### Backend
```powershell
cd backend
mvn spring-boot:run
```
Runs on http://localhost:8080

Health: http://localhost:8080/api/health

### Frontend
Open a second terminal:
```powershell
cd frontend
npm install
npm run dev
```
Runs on http://localhost:5173

## Main APIs
POST /api/profiles
GET /api/profiles?search=username
GET /api/profiles/{id}
POST /api/profiles/{id}/analyze
GET /api/profiles/{id}/history
DELETE /api/profiles/{id}
GET /api/dashboard/stats

## Demo bot profile
```json
{
  "username": "bot_982341",
  "displayName": "Free Crypto Rewards",
  "followers": 34,
  "following": 1850,
  "posts": 1320,
  "accountAgeDays": 12,
  "profilePicture": false,
  "bioPresent": false,
  "verified": false,
  "averageLikes": 2,
  "averageComments": 0,
  "postsLast24h": 88,
  "followersGainedLast7d": 740,
  "deviceId": "DEV-77A",
  "ipAddress": "10.10.10.44",
  "suspiciousLogins": 6
}
```

This is an explainable rule-based prototype. The detection engine is isolated so an ML model can be added later.
