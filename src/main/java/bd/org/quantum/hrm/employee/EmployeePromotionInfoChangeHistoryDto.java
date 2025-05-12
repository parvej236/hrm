package bd.org.quantum.hrm.employee;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EmployeePromotionInfoChangeHistoryDto {

    @JsonIgnore
    private long employeeId;
    private String date;
    private String type;
    private String designationFrom;
    private String designationTo;
    private String remarks;

    public EmployeePromotionInfoChangeHistoryDto(PromotionInfo promotion) {
        this.setEmployeeId(promotion.getEmployee().getId());
        this.date = promotion.getDateFrom() != null ? String.valueOf(promotion.getDateFrom()) : "";
        this.type = promotion.getType() != null ? promotion.getType() : "";
        this.designationFrom = promotion.getDesignationFrom() != null ? promotion.getDesignationFrom() : "";
        this.designationTo = promotion.getDesignationTo() != null ? promotion.getDesignationTo() : "";
        this.remarks = promotion.getComments() != null ? promotion.getComments() : "";
    }
}