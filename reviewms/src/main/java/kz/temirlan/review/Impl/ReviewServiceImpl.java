package kz.temirlan.review.Impl;


import kz.temirlan.review.Review;
import kz.temirlan.review.ReviewRepository;
import kz.temirlan.review.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;

    @Autowired
    public ReviewServiceImpl(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Override
    public List<Review> getAllReviews(Long companyId) {
        return reviewRepository.findByCompanyId(companyId);
    }

    @Override
    public Review getReviewById(Long companyId, Long id) {
          return reviewRepository.findByIdAndCompanyId(id, companyId)
                  .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cannot found review"));
    }

    @Override
    public Review createReview(Long companyId, Review review) {
        if (companyId != null) {
            review.setCompanyId(companyId);
            return reviewRepository.save(review);
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found");
    }

//    @Override
//    public Review updateReview(Long companyId, Long id, Review review) {
//        return null;
//    }

    @Override
    public void deleteReview(Long companyId, Long id) {
        if (companyId == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found");
        }

        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found"));
        if (!review.getCompanyId().equals(companyId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Review does not belong to the specified company");
        }

        reviewRepository.delete(review);
    }
}
