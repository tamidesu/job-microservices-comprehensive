package kz.temirlan.review;


import kz.temirlan.review.messaging.ReviewMessageProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;
    private final ReviewMessageProducer reviewMessageProducer;

    @Autowired
    public ReviewController(ReviewService reviewService,
      ReviewMessageProducer reviewMessageProducer) {
        this.reviewService = reviewService;
        this.reviewMessageProducer = reviewMessageProducer;
    }

    @GetMapping
    public ResponseEntity<List<Review>> getAllReviews(@RequestParam("companyId") Long companyId) {
        return ResponseEntity.ok( reviewService.getAllReviews(companyId));
    }

    @GetMapping("/average-rating")
    public BigDecimal getAverageRating(@RequestParam("companyId") Long companyId) {
        List<Review> reviews = reviewService.getAllReviews(companyId);

        if (reviews.isEmpty()) {
            return BigDecimal.ZERO;
        }

        return reviews.stream()
                .map(Review::getRating)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(reviews.size()), RoundingMode.HALF_UP);
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<Review> getReview(@RequestParam Long companyId,
                                            @PathVariable Long reviewId) {
        try {
            Review job = reviewService.getReviewById(companyId, reviewId);
            return ResponseEntity.ok(job);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(null);
        }
    }

    @PostMapping
    public ResponseEntity<Review> createReview(@RequestParam Long companyId,
                                        @RequestBody Review review) {
        try {
            reviewMessageProducer.sendMessage(review, companyId);
            return ResponseEntity.ok(reviewService.createReview(companyId, review));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(review);
        }
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<String> deleteReview(@RequestParam Long companyId,
                                               @PathVariable Long reviewId) {
        try{
            reviewService.deleteReview(companyId, reviewId);
            return ResponseEntity.ok("Successfully deleted review");
        }catch (ResponseStatusException e){
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        }
    }
}
