package kz.temirlan.company;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/companies")
public class CompanyController {
    private final CompanyService companyService;

    @GetMapping
    public List<Company> getAllCompanies() {
        return companyService.getAllCompanies();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Company> getCompany(@PathVariable Long id) {
        try{
            companyService.getCompanyById(id);
            return ResponseEntity.ok().body(companyService.getCompanyById(id));
        } catch (ResponseStatusException e){
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Company> createCompany(@RequestBody Company company) {
        return ResponseEntity.ok(companyService.createCompany(company));
    }

    @PutMapping("/{companyId}")
    public ResponseEntity<Company> updateCompany(@PathVariable Long companyId,
                                        @RequestBody Company company) {
        return ResponseEntity.ok(companyService.updateCompany(companyId, company));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteJob(@PathVariable Long id) {
        try {
            companyService.deleteCompanyById(id);
            return ResponseEntity.ok("Deleted job with id " + id);
        }
        catch (ResponseStatusException e){
            return ResponseEntity.status(e.getStatusCode()).body(null);
        }
    }

}
