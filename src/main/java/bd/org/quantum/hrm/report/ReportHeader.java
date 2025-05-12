package bd.org.quantum.hrm.report;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportHeader {
    private String organization;
    private String department;
    private String title;
    private String subTitle;
    private String duration;

    public ReportHeader() {
        this.organization = "Quantum Foundation";
    }

    public ReportHeader(String department, String title, String subTitle, String duration) {
        this.department = department;
        this.title = title;
        this.subTitle = subTitle;
        this.duration = duration;
    }

    public ReportHeader(String title, String subTitle, String duration) {
        this();
        this.title = title;
        this.subTitle = subTitle;
        this.duration = duration;
    }
}
