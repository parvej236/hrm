package bd.org.quantum.hrm.report;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class ReportData {
    private ReportHeader header;
    private String footer;
    private String orientation;
    private String view;
    private Map<String, Object> result;

    public ReportData() {}

    public ReportData(ReportHeader header, String footer, String orientation, String view, Map<String, Object> result) {
        this.header = header;
        this.footer = footer;
        this.orientation = orientation;
        this.view = view;
        this.result = result;
    }
}
