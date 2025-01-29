package kz.temirlan.review.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewMessage {
    private Long id;
    private String title;
    private String content;
    private BigDecimal rating;
    private Long companyId;
}
