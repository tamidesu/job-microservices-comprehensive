package kz.temirlan.job.external;

import java.math.BigDecimal;

public record Review (
        Long id,
        String title,
        String content,
        BigDecimal rating) {
}
