package com.socialshield.controller;
import com.socialshield.service.ProfileService; import org.springframework.web.bind.annotation.*; import java.util.*;
@RestController @RequestMapping("/api/dashboard") @CrossOrigin(origins="http://localhost:5173")
public class DashboardController {
 private final ProfileService s; public DashboardController(ProfileService s){this.s=s;}
 @GetMapping("/stats") public Map<String,Long> stats(){return Map.of("total",s.total(),"genuine",s.genuine(),"suspicious",s.suspicious(),"bots",s.bots());}
}
