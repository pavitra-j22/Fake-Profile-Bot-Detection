package com.socialshield.controller;
import com.socialshield.entity.*; import com.socialshield.service.ProfileService;
import jakarta.validation.Valid; import org.springframework.http.ResponseEntity; import org.springframework.web.bind.annotation.*; import java.util.*;
@RestController @RequestMapping("/api/profiles") @CrossOrigin(origins="http://localhost:5173")
public class ProfileController {
 private final ProfileService service; public ProfileController(ProfileService s){service=s;}
 @PostMapping public ResponseEntity<Profile> create(@Valid @RequestBody Profile p){return ResponseEntity.ok(service.create(p));}
 @GetMapping public List<Profile> list(@RequestParam(required=false)String search){return service.find(search);}
 @GetMapping("/{id}") public Profile get(@PathVariable Long id){return service.get(id);}
 @PostMapping("/{id}/analyze") public Profile analyze(@PathVariable Long id){return service.analyze(id);}
 @GetMapping("/{id}/history") public List<AnalysisHistory> history(@PathVariable Long id){return service.history(id);}
 @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable Long id){service.delete(id);return ResponseEntity.noContent().build();}
}
