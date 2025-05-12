package bd.org.quantum.hrm.attendance;

import bd.org.quantum.authorizer.Authorizer;
import bd.org.quantum.authorizer.helper.UserContext;
import bd.org.quantum.authorizer.model.UserDetails;
import bd.org.quantum.common.utils.SubmitResult;
import bd.org.quantum.hrm.employee.Employee;
import bd.org.quantum.hrm.employee.EmployeeStatus;
import bd.org.quantum.hrm.leave.LeaveType;
import bd.org.quantum.hrm.designation.DesignationService;
import bd.org.quantum.hrm.employee.EmployeeService;
import bd.org.quantum.hrm.leave.LeaveApplication;
import bd.org.quantum.hrm.common.Routes;
import bd.org.quantum.hrm.report.ReportData;
import bd.org.quantum.hrm.role.Permissions;
import bd.org.quantum.hrm.yr_cl.YrCl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
public class AttendanceController {
    private final AttendanceService service;
    private final EmployeeService employeeService;
    private final DesignationService designationService;
    private final Authorizer authorizer;
    private final MessageSource messageSource;

    public AttendanceController(AttendanceService service,
                                EmployeeService employeeService,
                                DesignationService designationService,
                                Authorizer authorizer,
                                MessageSource messageSource) {
        this.service = service;
        this.employeeService = employeeService;
        this.designationService = designationService;
        this.authorizer = authorizer;
        this.messageSource = messageSource;
    }

    @GetMapping(value = Routes.ATTENDANCE_CRITERIA, produces = MediaType.APPLICATION_JSON_VALUE)
    public String criteriaForm(@RequestParam(required = false) boolean isApprove,  Model model) {

        List<LeaveApplication> leaveApplicationList = service.getApplicationList();

        AttendanceCriteria criteria = new AttendanceCriteria();
        model.addAttribute("criteria", criteria);
        model.addAttribute("leaveApplicationList", leaveApplicationList);
        model.addAttribute("isApprove", isApprove);
        referenceData(model);
        return "attendance/attendance-criteria";
    }

    @PostMapping(value = Routes.ATTENDANCE_CRITERIA, produces = MediaType.APPLICATION_JSON_VALUE)
    public String getCriteriaForm(@Valid AttendanceCriteria criteria, @Valid Attendance attend, @Valid boolean isApprove, HttpSession session, BindingResult result, Model model) {

        if (attend.getId() != null && attend.getId() > 0) {

            try {
                if (!result.hasErrors()) {
                    service.update(attend);
                    criteria = (AttendanceCriteria) session.getAttribute("criteria");
                    SubmitResult.success(messageSource, "attendance.update.success", model);
                } else {
                    SubmitResult.error(messageSource, "attendance.update.error", model);
                }
            } catch (Exception e) {
                SubmitResult.error(messageSource, "attendance.update.error", model);
            }
        }

        List<AttendanceStatus> dataList = service.processAttendanceEntryForm(criteria);

        processing(dataList, model);

        criteria.setAttendanceList(dataList);

        session.setAttribute("criteria", criteria);
        model.addAttribute("criteria", criteria);
        model.addAttribute("dataList", dataList);
        model.addAttribute("columnList", service.getDateRange(criteria.getStartDate(), criteria.getEndDate()));
        model.addAttribute("actionUrl", Routes.ADD_ATTENDANCE);
        model.addAttribute("entryUrl", Routes.ATTENDANCE_CRITERIA);
        model.addAttribute("detailsUrl", Routes.ATTENDANCE_DETAILS + "?id=");
        model.addAttribute("isApprove", isApprove);
        referenceData(model);

        return "attendance/attendance-form";
    }

    @PostMapping(value = Routes.ADD_ATTENDANCE, produces = MediaType.APPLICATION_JSON_VALUE)
    public String create(@Valid AttendanceCriteria criteria, @Valid Attendance attend, @Valid boolean isApprove, HttpSession session, BindingResult result, Model model) {

        if (attend.getId() != null && attend.getId() > 0) {
            try {
                if (!result.hasErrors()) {
                    service.update(attend);
                    criteria = (AttendanceCriteria) session.getAttribute("criteria");
                    SubmitResult.success(messageSource, "attendance.update.success", model);
                } else {
                    SubmitResult.error(messageSource, "attendance.update.error", model);
                }
            } catch (Exception e) {
                SubmitResult.error(messageSource, "attendance.update.error", model);
            }
        } else {
            try {
                if (!result.hasErrors()) {
                    service.create(criteria);
                    SubmitResult.success(messageSource, "attendance.submit.success", model);
                } else {
                    SubmitResult.error(messageSource, "attendance.submit.error", model);
                }
            } catch (Exception e) {
                SubmitResult.error(messageSource, "attendance.submit.error", model);
            }
        }

        List<AttendanceStatus> dataList = service.processAttendanceEntryForm(criteria);

        processing(dataList, model);

        criteria.setAttendanceList(dataList);

        session.setAttribute("criteria", criteria);
        model.addAttribute("criteria", criteria);
        model.addAttribute("dataList", dataList);
        model.addAttribute("columnList", service.getDateRange(criteria.getStartDate(), criteria.getEndDate()));
        model.addAttribute("actionUrl", Routes.ADD_ATTENDANCE);
        model.addAttribute("entryUrl", Routes.ATTENDANCE_CRITERIA);
        model.addAttribute("detailsUrl", Routes.ATTENDANCE_DETAILS + "?id=");
        model.addAttribute("isApprove", isApprove);
        referenceData(model);

        return "attendance/attendance-form";
    }

    @GetMapping(value = Routes.ATTENDANCE_POPUP_FORM, produces = MediaType.APPLICATION_JSON_VALUE)
    public String attendancePopupForm(@RequestParam(required = false) Long attendanceId,
                                      @RequestParam(required = false) int cl,
                                      @RequestParam(required = false) int yr,
                                      HttpSession session, Model model) {

        AttendanceCriteria criteria = (AttendanceCriteria) session.getAttribute("criteria");

        Attendance attendance = service.get(attendanceId);
        attendance.setCl(cl);
        attendance.setYr(yr);

        model.addAttribute("criteria", criteria);
        model.addAttribute("attendance", attendance);
        referenceData(model);

        return "attendance/attendance-popup-form";
    }

    @GetMapping(value = Routes.ATTENDANCE_DETAILS, produces = MediaType.APPLICATION_JSON_VALUE)
    public String attendancePopupForm(@RequestParam(required = false) Long yrId, @RequestParam(required = false) Long id, Model model) {
        List<YrCl> yearWiseYrList = service.getYearWiseYrList();
        Employee employee = employeeService.get(id);

        YrCl yearWiseYr;
        if (yrId != null && yrId > 0) {
            yearWiseYr = service.getYearWiseYrById(yrId);
        } else {
            yearWiseYr = yearWiseYrList.get(0);
        }

        service.processAttendanceDetails(yearWiseYr, employee, model);

        model.addAttribute("yrId", yrId);
        model.addAttribute("employee", employee);
        model.addAttribute("yrList", yearWiseYrList);
        model.addAttribute("yearWiseYr", yearWiseYr);
        model.addAttribute("otherLeaveStr", AttendanceType.OTHER_LEAVE_STR);
        model.addAttribute("detailsUrl", Routes.ATTENDANCE_DETAILS + "?id=");
        model.addAttribute("odOtpTrainingStr", AttendanceType.OD_OTP_TRAINING_STR);
        model.addAttribute("encashListUrl", Routes.YR_ENCASH_LIST + "?empId=" + id);
        model.addAttribute("detailsListUrl", Routes.ATTENDANCE_DETAILS_LIST + "?empId=" + id);
        model.addAttribute("hasYrEncashEntryAccess", authorizer.hasAnyPermission(Permissions.EMPLOYEE_HR_ADMIN.name(), Permissions.EMPLOYEE_HR_PERSONNEL.name()));
        referenceData(model);

        return "attendance/attendance-details";
    }

    @GetMapping(value = Routes.ATTENDANCE_DETAILS_LIST, produces = MediaType.APPLICATION_JSON_VALUE)
    public String attendanceList(@RequestParam Long empId, @RequestParam(required = false) String duration, @RequestParam(required = false) String types, Model model) {
        LocalDate dateFrom = null, dateTo = null;
        if (!StringUtils.isEmpty(duration)) {
            String[] durationArr = duration.split("-");
            dateFrom = LocalDate.parse(durationArr[0], DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            dateTo = LocalDate.parse(durationArr[1], DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }
        List<AttendanceSummaryDto> attendanceList = service.getAttendanceList(empId, dateFrom, dateTo, types);

        if (!StringUtils.isEmpty(types)) {
            if (!types.contains(",")) {
                model.addAttribute("typeName", AttendanceType.getAllAttendanceTypes().get(Integer.valueOf(types)));
            } else if (types.split(",").length == 3) {
                model.addAttribute("typeName", "OD/OTP/T");
            } else if (types.split(",").length == 4) {
                model.addAttribute("typeName", "Others");
            }
        } else {
            model.addAttribute("typeName", "All Leave");
        }

        model.addAttribute("attendanceList", attendanceList);
        return "attendance/attendance-list";
    }

    @GetMapping(Routes.DAILY_ATTENDANCE_VIEW)
    public String view(Model model) {
        DailyAttendanceCmd criteria = new DailyAttendanceCmd();
        processAttendance(criteria, model);
        return "attendance/daily-attendance-view";
    }

    @PostMapping(Routes.DAILY_ATTENDANCE_VIEW)
    public String view(@Valid DailyAttendanceCmd criteria, Model model) {
        processAttendance(criteria, model);
        return "attendance/daily-attendance-view";
    }

    @PostMapping(value = Routes.DAILY_ATTENDANCE_VIEW, params = "format=PDF")
    @ResponseBody
    public ReportData getPdf(@RequestBody DailyAttendanceCmd criteria, Model model) {
        return service.generatePdf(criteria);
    }

    private void referenceData(Model model) {
        UserDetails user = UserContext.getPrincipal().getUserDetails();

        model.addAttribute("leaveTypeMap", LeaveType.getLeaveTypes(null));
        model.addAttribute("attendanceTypeMap", AttendanceType.getAttendanceTypeMap());
        model.addAttribute("empStatusMap", EmployeeStatus.getAllStatus());
        model.addAttribute("designationMap", designationService.getDesignationMap());
        model.addAttribute("branchList", employeeService.getBranches());
        model.addAttribute("departmentList", employeeService.getDepartmentList(user.getBranchId()));
        model.addAttribute("hasSubmitAccess", authorizer.hasAnyPermission(Permissions.ATTENDANCE_CREATE.name()));
        model.addAttribute("hasUpdateAccess", authorizer.hasAnyPermission(Permissions.ATTENDANCE_UPDATE.name()));
        model.addAttribute("hasApproveAccess", authorizer.hasAnyPermission(Permissions.ATTENDANCE_APPROVE.name()));
    }

    public void processing(List<AttendanceStatus> dataList, Model model) {

        boolean enableSubmit = false;
        boolean enableApprove = false;
        boolean enableApproveUpdate = false;
        boolean availableSL = false;

        for(AttendanceStatus as : dataList) {
            for(AttendanceDateStatus ads : as.getDateStatusList()) {
                if(ads.getAttendanceType() > 0) {
                    if(ads.getStatus() == 0 && !enableSubmit){
                        enableSubmit = true;
                    }

                    if(ads.getStatus() == 1 && !enableApprove) {
                        enableApprove = true;
                    }

                    if(ads.getStatus() == 2 && !enableApproveUpdate) {
                        enableApproveUpdate = true;
                    }

                    if(enableApprove) {
                        enableApproveUpdate = false;
                    }

                    if(ads.getAttendanceType() == 15 && !availableSL) {
                        availableSL = true;
                    }
                }
            }
        }
        model.addAttribute("enableSubmit", enableSubmit);
        model.addAttribute("enableApprove", enableApprove);
        model.addAttribute("enableApproveUpdate", enableApproveUpdate);
        model.addAttribute("availableSL", availableSL);
    }

    private void processAttendance(DailyAttendanceCmd criteria, Model model) {
        UserDetails user = UserContext.getPrincipal().getUserDetails();
        if (criteria.getAttendanceDate() == null) {
            criteria.setAttendanceDate(LocalDate.now());
        }

        DailyAttendanceCmd summary = service.getAttendanceSummary(criteria);
        Map<String, List<DailyAttendanceCmd>> detailsMap = service.getDailyAttendanceMap(criteria);

        model.addAttribute("criteria", criteria);
        model.addAttribute("summary", summary);
        model.addAttribute("detailsMap", detailsMap);
        model.addAttribute("branchList", employeeService.getBranches());
        model.addAttribute("departmentList", employeeService.getDepartmentList(user.getBranchId()));
    }
}
