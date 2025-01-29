package kz.temirlan.job;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    String title;
    String description;
    BigDecimal minSalary;
    BigDecimal maxSalary;
    String location;

    @Column(nullable = false)
    Long companyId;

//    @Version
//    private Long version;
}
