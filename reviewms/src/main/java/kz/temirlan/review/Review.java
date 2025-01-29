package kz.temirlan.review;

//import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String content;
    private BigDecimal rating;
    private Long companyId;
    //    @JsonBackReference
//    @ManyToOne
//    @JoinColumn(name = "company_id", nullable = false)
//    private Company company;
}
