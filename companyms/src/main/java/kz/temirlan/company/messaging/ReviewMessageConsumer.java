package kz.temirlan.company.messaging;

import kz.temirlan.company.Company;
import kz.temirlan.company.CompanyService;
import kz.temirlan.company.dto.ReviewMessage;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class ReviewMessageConsumer {
    private final CompanyService companyService;
    private static final Logger logger = LoggerFactory.getLogger(ReviewMessageConsumer.class);

    @RabbitListener(queues = "companyRatingQueue")
    public void consumeMessage(ReviewMessage reviewMessage) {
        if (reviewMessage == null || reviewMessage.getCompanyId() == null) {
            logger.error("Invalid ReviewMessage: {}", reviewMessage);
            return;
        }

        try {
            companyService.updateCompanyRating(reviewMessage);
        } catch (Exception e) {
            logger.error("Error processing ReviewMessage: {}", reviewMessage, e);
            throw e;
        }

        companyService.updateCompanyRating(reviewMessage);
    }
}
