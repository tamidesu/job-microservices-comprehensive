package kz.temirlan.review;

import java.util.List;

public interface ReviewService {
    List<Review> getAllReviews(Long companyId);
    Review getReviewById(Long companyId, Long id);
    Review createReview(Long companyId, Review review);
    void deleteReview(Long companyId, Long id);
}
