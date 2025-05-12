package bd.org.quantum.hrm.yr_cl;

import bd.org.quantum.common.utils.DateUtils;
import bd.org.quantum.hrm.common.AuditData;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "year_wise_yr")
public class YrCl extends AuditData {
    private Integer year;

    @Column(name = "yr_day")
    private Integer yr;

    @Column(name = "cl_day")
    private Integer cl;

    @DateTimeFormat(pattern = DateUtils.DD_MM_YYYY)
    @JsonFormat(pattern = DateUtils.DD_MM_YYYY)
    @Column(name = "start_date")
    private LocalDate dateFrom;

    @DateTimeFormat(pattern = DateUtils.DD_MM_YYYY)
    @JsonFormat(pattern = DateUtils.DD_MM_YYYY)
    @Column(name = "end_date")
    private LocalDate dateTo;

    @Column(name = "comments")
    private String remarks;

    public String getDuration(){
        String duration = "";

        if(this.getDateFrom() != null && this.getDateTo()!= null){
            duration = DateUtils.formatToDDMMYYYY(this.getDateFrom()) + "-" + DateUtils.formatToDDMMYYYY(this.getDateTo());
        }

        return duration;
    }
}
