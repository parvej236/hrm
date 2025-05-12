package bd.org.quantum.hrm.report;

import bd.org.quantum.authorizer.helper.SecurityCheck;
import bd.org.quantum.authorizer.helper.UserContext;
import bd.org.quantum.authorizer.model.UserDetails;
import bd.org.quantum.hrm.designation.DesignationService;
import bd.org.quantum.hrm.employee.EmployeeService;
import bd.org.quantum.hrm.employee.EmployeeStatus;
import bd.org.quantum.hrm.common.Gender;
import bd.org.quantum.hrm.common.MaritalStatus;
import bd.org.quantum.hrm.common.Religion;
import bd.org.quantum.hrm.common.Routes;
import bd.org.quantum.hrm.employee.SalaryLocation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Controller
public class ReportController {

    private final ReportService reportService;
    private final EmployeeService employeeWebService;
    private final DesignationService designationWebService;

    public ReportController(ReportService reportService,
                            EmployeeService employeeWebService,
                            DesignationService designationWebService) {
        this.reportService = reportService;
        this.employeeWebService = employeeWebService;
        this.designationWebService = designationWebService;
    }

    @GetMapping(Routes.EMP_REPORT_CRITERIA)
    @SecurityCheck(permissions = {"EMPLOYEE_REPORT_GENERAL"})
    public String getReportCriteria(Model model){
        referenceData(model);
        return "report/report-criteria";
    }

    @PostMapping(value = Routes.EMP_REPORT_GENERATE, params = "format=HTML")
    public String getMemberReports(@RequestBody ReportCriteria criteria, Model model){
        ReportData reportData = reportService.generateReport(criteria);
        model.addAttribute("reportData", reportData);
        referenceData(model);
        return String.format("report/%s", reportData.getView());
    }

    @PostMapping(value = Routes.EMP_REPORT_GENERATE, params = "format=PDF")
    @ResponseBody
    public ReportData getReports(@RequestBody ReportCriteria criteria, Model model){
        referenceData(model);
        return reportService.generateReport(criteria);
    }

    @GetMapping(Routes.ATT_REPORT_CRITERIA)
    @SecurityCheck(permissions = {"ATTENDANCE_REPORT"})
    public String getAttendanceReportCriteria(Model model){
        referenceData(model);
        return "report/report-criteria-attendance";
    }

    @PostMapping(value = Routes.ATT_REPORT_GENERATE, params = "format=HTML")
    public String getAttendanceReports(@RequestBody ReportCriteria criteria, Model model){
        ReportData reportData = reportService.generateReport(criteria);
        model.addAttribute("reportData", reportData);
        referenceData(model);
        return String.format("report/%s", reportData.getView());
    }

    @PostMapping(value = Routes.ATT_REPORT_GENERATE, params = "format=PDF")
    @ResponseBody
    public ReportData getAttendancePdfReports(@RequestBody ReportCriteria criteria, Model model){
        referenceData(model);
        return reportService.generateReport(criteria);
    }

    @GetMapping(Routes.LEAVE_REPORT_CRITERIA)
    @SecurityCheck(permissions = {"ATTENDANCE_REPORT"})
    public String getLeaveReportCriteria(Model model){
        referenceData(model);
        return "report/leave-report-criteria";
    }

    @PostMapping(value = Routes.LEAVE_REPORT_GENERATE, params = "format=HTML")
    public String getLeaveReports(@RequestBody ReportCriteria criteria, Model model){
        ReportData reportData = reportService.generateReport(criteria);
        model.addAttribute("reportData", reportData);
        referenceData(model);
        return String.format("report/%s", reportData.getView());
    }

    @PostMapping(value = Routes.LEAVE_REPORT_GENERATE, params = "format=PDF")
    @ResponseBody
    public ReportData getLeavePdfReports(@RequestBody ReportCriteria criteria, Model model){
        referenceData(model);
        return reportService.generateReport(criteria);
    }

    private void referenceData(Model model) {
        UserDetails user = UserContext.getPrincipal().getUserDetails();
        model.addAttribute("reportTypes", ReportType.getEmployeeReportTypeMap());
        model.addAttribute("attReportTypes", ReportType.getAttendanceReportTypeMap());
        model.addAttribute("leaveReportType", ReportType.getLeaveReportTypeMap());
        model.addAttribute("branchList", employeeWebService.getBranches());
        model.addAttribute("departmentList", employeeWebService.getDepartmentList(user.getBranchId()));
        model.addAttribute("designationMap", designationWebService.getDesignationMap());
        model.addAttribute("religionMap", Religion.getAllReligion());
        model.addAttribute("genderMap", Gender.getAllGender());
        model.addAttribute("maritalStatusMap", MaritalStatus.getAllMaritalStatus());
        model.addAttribute("districts", reportService.districts());
        model.addAttribute("empStatusMap", EmployeeStatus.getAllStatus());
        model.addAttribute("locationMap", SalaryLocation.getSalaryLocationMap());

        Map<String, String> dateTypeMap = new LinkedHashMap<>();
        for (ReportCriteria.DateType type : ReportCriteria.DateType.values()) {
            dateTypeMap.put(type.name(), type.getName());
        }
        model.addAttribute("dateTypeMap", dateTypeMap);

        Map<String, String> sortingOrderMap = new LinkedHashMap<>();
        for (ReportCriteria.SortingOrder type : ReportCriteria.SortingOrder.values()) {
            sortingOrderMap.put(type.name(), type.getName());
        }
        model.addAttribute("sortingOrderMap", sortingOrderMap);
    }

}
