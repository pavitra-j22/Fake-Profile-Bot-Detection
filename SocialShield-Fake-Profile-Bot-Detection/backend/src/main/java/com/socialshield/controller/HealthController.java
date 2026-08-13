package com.socialshield.controller;
import org.springframework.web.bind.annotation.*; import java.util.*;
@RestController public class HealthController {
 @GetMapping("/api/health") public Map<String,String> health(){return Map.of("status","UP","service","SocialShield Detection API");}
}
