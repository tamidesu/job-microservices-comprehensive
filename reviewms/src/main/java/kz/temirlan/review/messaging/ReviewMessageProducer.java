package kz.temirlan.review.messaging;

import kz.temirlan.review.Review;
import kz.temirlan.review.dto.ReviewMessage;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ReviewMessageProducer {
    private final RabbitTemplate rabbitTemplate;

    public void sendMessage(Review review, Long companyId){
        rabbitTemplate.convertAndSend("companyRatingQueue",
                ReviewMessage.builder()
                        .id(review.getId())
                        .title(review.getTitle())
                        .content(review.getContent())
                        .rating(review.getRating())
                        .companyId(companyId)
                        .build()
        );
    }
}
