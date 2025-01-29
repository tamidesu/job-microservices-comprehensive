package kz.temirlan.company;


import kz.temirlan.company.dto.ReviewMessage;

import java.util.List;

public interface CompanyService {
    List<Company> getAllCompanies();
    Company getCompanyById(Long id);
    Company createCompany(Company company);
    Company updateCompany(Long id, Company company);
    void deleteCompanyById(Long id);
    void updateCompanyRating(ReviewMessage reviewMessage);
}
