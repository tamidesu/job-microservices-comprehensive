package kz.temirlan.company.client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@FeignClient(name = "REVIEW-SERVICE",
        url = "${review.service.url}")
public interface ReviewClient {
    @GetMapping("/reviews/average-rating")
    BigDecimal getAverageRatingForCompany(@RequestParam("companyId") Long companyId);
}
