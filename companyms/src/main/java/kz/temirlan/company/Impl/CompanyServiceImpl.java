package kz.temirlan.company.Impl;

import kz.temirlan.company.Company;
import kz.temirlan.company.CompanyRepository;
import kz.temirlan.company.CompanyService;
import kz.temirlan.company.client.ReviewClient;
import kz.temirlan.company.dto.ReviewMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {
    private final ReviewClient reviewClient;
    private final CompanyRepository companyRepository;

    @Override
    public List<Company> getAllCompanies() {
        return companyRepository.findAll();
    }

    @Override
    public Company getCompanyById(Long id) {
        return companyRepository.findById(id)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found")
                );
    }

    @Override
    public Company createCompany(Company company) {
        company.setRating( new BigDecimal("5.0"));
        return companyRepository.save(company);
    }

    @Override
    public Company updateCompany(Long id, Company company) {
        Company existingCompany = companyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found"));

        existingCompany.setName(company.getName());
        existingCompany.setDescription(company.getDescription());
//        existingCompany.setJobs(company.getJobs());

        return companyRepository.save(existingCompany);
    }

    @Override
    public void deleteCompanyById(Long id) {
        if (!companyRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found");
        }
        companyRepository.deleteById(id);
    }

    @Override
    public void updateCompanyRating(ReviewMessage reviewMessage) {
        System.out.println(reviewMessage.getContent());
        Company company = getCompanyById(reviewMessage.getCompanyId());

        BigDecimal averageRating = reviewClient.getAverageRatingForCompany(company.getId());
        company.setRating(averageRating);
        companyRepository.save(company);
    }
}
