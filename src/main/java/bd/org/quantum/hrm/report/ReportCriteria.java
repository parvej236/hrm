package bd.org.quantum.hrm.report;

import bd.org.quantum.common.utils.DateUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class ReportCriteria {
    public enum DateType {
        BIRTH_DAY("Birth Day"),
        MARRIAGE_DAY("Marriage Day"),
        JOINING_DATE("Joining Date"),
        REGULAR_DATE("Regular Date"),
        PROMOTION_DATE("Promotion Date"),
        RESIGN_DATE("Resign Date");

        private String name;

        DateType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public enum SortingOrder {
        JOINING_DATE("Joining Date"),
        BRANCH("Branch Name"),
        DEPARTMENT("Department Name"),
        DESIGNATION("Designation");

        private String name;

        SortingOrder(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    private String reportType;
    private String dateType;

    @DateTimeFormat(pattern = DateUtils.DD_MM_YYYY)
    @JsonFormat(pattern = DateUtils.DD_MM_YYYY)
    private LocalDate dateFrom;

    @DateTimeFormat(pattern = DateUtils.DD_MM_YYYY)
    @JsonFormat(pattern = DateUtils.DD_MM_YYYY)
    private LocalDate dateTo;
    private Long branch;
    private String branchName;
    private List<Long> branches;
    private Long department;
    private String departmentName;
    private List<Long> departments;
    private Long designation;
    private List<Long> designations;
    private List<String> statuses;
    private boolean withSubsidiary;
    private boolean departmentWithSub;
    private String arbitraryType;
    private String arbitrary;
    private String gender;
    private String religion;
    private String district;
    private String maritalStatus;
    private String salaryLocation;
    private String status;
    private String sortingOrder1;
    private String sortingOrder2;
    private String sortingType1;
    private String sortingType2;

    public List<LocalDate> getDateRange() {
        List<LocalDate> daysRange = new ArrayList<>();

        if(this.getDateFrom() != null && this.getDateTo() != null) {
            daysRange = this.getDateFrom().datesUntil(this.dateTo.plusDays(1)).collect(Collectors.toList());
        }

        return daysRange;
    }
}
