package bd.org.quantum.hrm.report;

import bd.org.quantum.authorizer.Authorizer;
import bd.org.quantum.authorizer.helper.UserContext;
import bd.org.quantum.authorizer.model.UserDetails;
import bd.org.quantum.common.resttemplate.RestTemplateService;
import bd.org.quantum.common.utils.*;
import bd.org.quantum.hrm.attendance.AttendanceCriteria;
import bd.org.quantum.hrm.attendance.AttendanceDao;
import bd.org.quantum.hrm.attendance.AttendanceService;
import bd.org.quantum.hrm.attendance.AttendanceType;
import bd.org.quantum.hrm.common.Branch;
import bd.org.quantum.hrm.common.Constants;
import bd.org.quantum.hrm.common.Department;
import bd.org.quantum.hrm.common.District;
import bd.org.quantum.hrm.employee.EmployeeStatus;
import bd.org.quantum.hrm.role.Permissions;
import bd.org.quantum.hrm.yr_cl.YrCl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ReportService {
    private static final String DISTRICTS = "/api/district-list";
    private static final String BRANCH_LIST = "%s/api/branches/%s";
    private static final String DEPARTMENT_WITH_SUBSIDIARIES = "%s/api/departments/{deftId}/subsidiaries";

    @Value("${resource.api.url}")
    String resourceUrl;

    private final ReportDao reportDao;
    private final ImageService imageService;
    private final AttendanceService attendanceService;
    private final AttendanceDao attendanceDao;
    private final Authorizer authorizer;
    private final RestTemplateService restService;

    public ReportService(ReportDao reportDao,
                         ImageService imageService,
                         AttendanceService attendanceService,
                         AttendanceDao attendanceDao,
                         Authorizer authorizer,
                         RestTemplateService restService) {
        this.reportDao = reportDao;
        this.imageService = imageService;
        this.attendanceService = attendanceService;
        this.attendanceDao = attendanceDao;
        this.authorizer = authorizer;
        this.restService = restService;
    }

    public ReportData generateReport(ReportCriteria criteria) {
        processCriteria(criteria);
        Map<String, Object> result = new LinkedHashMap<>();

        String title = Misc.getReadableName(criteria.getReportType());
        String department = StringUtils.isEmpty(criteria.getBranchName()) || criteria.getBranchName().equals("Select Branch") ?
                UserContext.getPrincipal().getUserDetails().getBranchName() : criteria.getBranchName();
        String subTitle = StringUtils.isEmpty(criteria.getDepartmentName()) ? "" : criteria.getDepartmentName();
        String duration = "";
        String orientation = Constants.PAGE_LANDSCAPE;
        String view = "";

        if (ReportType.EMPLOYEE_LIST.name().equals(criteria.getReportType())) {
            result.put("data", getEmployeeListReport(criteria));
            result.put("statusMap", EmployeeStatus.getAllStatus());
            result.put("reportForPhone", false);
            view = "employee-list";
        } else if (ReportType.EMPLOYEE_PHONE_LIST.name().equals(criteria.getReportType())) {
            result.put("data", getEmployeeListReport(criteria));
            result.put("statusMap", EmployeeStatus.getAllStatus());
            result.put("reportForPhone", true);
            view = "employee-list";
        } else if (ReportType.EMPLOYEE_LIST_DETAILS.name().equals(criteria.getReportType())) {
            LocalDate today = LocalDate.now();
            duration = "For the month of " + Misc.getReadableName(today.getMonth().toString()) + " on " + DateTimeFormatter.ofPattern("dd/MM/yyyy").format(today);
            result.put("data", reportDao.getEmployeeListDetails(criteria));
            result.put("statusMap", EmployeeStatus.getAllStatus());
            view = "employee-list-details";
        } else if (ReportType.EMPLOYEE_STATEMENT_MONTHLY.name().equals(criteria.getReportType())) {
            LocalDate today = LocalDate.now();
            duration = "For the month of " + Misc.getReadableName(today.getMonth().toString()) + " on " + DateTimeFormatter.ofPattern("dd/MM/yyyy").format(today);
            result.put("data", reportDao.getMonthlyEmployeeStatement(criteria));
            view = "employee-statement-monthly";
        } else if (ReportType.EMPLOYEE_STATEMENT_DESIGNATION_WISE.name().equals(criteria.getReportType())) {
            result.put("data", reportDao.getDesignationWiseStatement(criteria));
            view = "employee-statement-designation-wise";
        } else if (ReportType.ATTENDANCE_SHEET.name().equals(criteria.getReportType())) {
            result = generateAttendanceSheetReport(criteria);
            view = "attendance-sheet";
        } else if (ReportType.ATTENDANCE_SUMMARY.name().equals(criteria.getReportType())) {
            result = generateAttendanceSummaryReport(criteria);
            view = "attendance-summary";
        } else if (ReportType.LEAVE_REPORT.name().equals(criteria.getReportType())) {
            result.put("data", generateLeaveReport(criteria));
            view = "leave-report";
        }

        return new ReportData(new ReportHeader(department, title, subTitle, duration), getFooterText(), orientation, view, result);
    }

    public List<EmployeeReportDto> getEmployeeListReport(ReportCriteria criteria) {
        List<EmployeeReportDto> list = reportDao.getEmployeeList(criteria);
        List<EmployeeReportDto> resultList = new ArrayList<>();

        list.forEach(dto -> {
            if (!org.thymeleaf.util.StringUtils.isEmpty(dto.getImagePath())) {
                Image image = imageService.downloadImg(dto.getImagePath());
                dto.setBase64(image.getData());
            }
            resultList.add(dto);
        });
        return resultList;
    }

    private Map<String, Object> generateAttendanceSheetReport(ReportCriteria criteria) {
        Map<String, Object> result = new LinkedHashMap<>();

        List<AttendanceCriteria> attendanceList = reportDao.getAttendanceList(criteria);
        processTotalDaysCount(attendanceList);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMM");
        List<String> formattedDates = criteria.getDateRange().stream()
                .map(date -> date.format(formatter))
                .collect(Collectors.toList());

        result.put("dto", attendanceList);
        result.put("columnList", formattedDates);
        result.put("typeColumns", AttendanceType.getAllShortAttendanceTypes());

        return result;
    }

    private Map<String, Object> generateAttendanceSummaryReport(ReportCriteria criteria) {
        Map<String, Object> result = new LinkedHashMap<>();

        List<AttendanceCriteria> attendanceList = reportDao.getAttendanceList(criteria);
        processTotalDaysCount(attendanceList);
        result.put("dto", attendanceList);
        result.put("typeColumns", AttendanceType.getAllShortAttendanceTypes());

        return result;
    }

    private void processTotalDaysCount(List<AttendanceCriteria> attendanceList) {
        for (AttendanceCriteria command : attendanceList) {
            int totalCount = 0;
            for (int total : command.getTotalAttendance()) {
                totalCount += total;
            }
            command.setTotal(totalCount);
        }
    }

    private List<EmployeeReportDto> generateLeaveReport(ReportCriteria criteria) {
        List<EmployeeReportDto> list = reportDao.getEmployeeList(criteria);
        List<YrCl> getYearWiseYrList = attendanceService.getYearWiseYrList();
        YrCl currentYr = null;
        LocalDate today = LocalDate.now();

        for (YrCl yr : getYearWiseYrList) {
            if (today.isAfter(yr.getDateFrom()) && today.isBefore(yr.getDateTo())) {
                currentYr = yr;
                break;
            }
        }

        for (EmployeeReportDto dto : list) {
            if (!StringUtils.isEmpty(dto.getJoiningDate()) && currentYr != null) {
                dto.setMatureCl(attendanceService.getTotalMaturedCl(today, currentYr, LocalDate.parse(dto.getJoiningDate())));
                dto.setTakenCl(attendanceDao.getConsumedLeave(dto.getId(), AttendanceType.CASUAL_LEAVE.getValue(),
                        currentYr.getDateFrom(), currentYr.getDateTo()));
                dto.setBalanceCl(dto.getMatureCl() - dto.getTakenCl());
            }

            if (!StringUtils.isEmpty(dto.getRegularDate()) && dto.getStatus().equals(EmployeeStatus.REGULAR.getName())) {
                dto.setMatureYr(attendanceService.getTotalYr(LocalDate.parse(dto.getRegularDate()), today));
                dto.setTakenYr(attendanceDao.getConsumedLeave(dto.getId(), AttendanceType.YEARLY_REJUVENATION.getValue(), null, null) + dto.getPreConsumedYr());
                dto.setEncashYr(attendanceDao.getTotalYrEncashDays(dto.getId()));
                dto.setBalanceYr(dto.getMatureYr() - (dto.getTakenYr() + dto.getEncashYr()));
            }
        }

        return list;
    }

    public List<District> districts() {
        final String uri = UrlUtils.getUrl(resourceUrl, DISTRICTS);
        List<District> list = restService.getForList(uri, new ParameterizedTypeReference<>(){});
        return list;
//        return restService.getForList(uri, new ParameterizedTypeReference<>(){});
    }

    private String getFooterText(){
        String currentDateTime = DateUtils.formatToDDMMYYYYHHMMSSA(new Date());
        return String.format("Generated By %s on %s", UserContext.getPrincipal().getUserDetails().getName(), currentDateTime);
    }

    private void processCriteria(ReportCriteria criteria) {
        UserDetails user = UserContext.getPrincipal().getUserDetails();
        List<Long> departmentList = new ArrayList<>();
        List<Long> branchList = new ArrayList<>();

        if (StringUtils.isEmpty(criteria.getStatus()) && (criteria.getStatuses() == null || criteria.getStatuses().isEmpty())) {
            criteria.setStatuses(Arrays.asList(EmployeeStatus.RUNNING_ALL_EMPLOYEE_STATUSES));
        } else if (!StringUtils.isEmpty(criteria.getStatus())) {
            criteria.setStatuses(Collections.singletonList(criteria.getStatus()));
        }

        if (criteria.getDepartment() != null && criteria.getDepartment() > 0 && (criteria.getDepartments() == null || criteria.getDepartments().isEmpty())) {
            departmentList.add(criteria.getDepartment());
            criteria.setDepartments(departmentList);
        }

        if (criteria.isDepartmentWithSub() && authorizer.hasPermission(Permissions.EMPLOYEE_DEPARTMENT_INTER_ACCESS.name())) {
            for (long dept : criteria.getDepartments()) {
                departmentList.addAll(getDepartmentsSubsidiaries(dept).stream().map(Department::getId).collect(Collectors.toList()));
            }
            criteria.setDepartments(departmentList);
        } else if (authorizer.hasPermission(Permissions.EMPLOYEE_BRANCH_ACCESS.name(), Permissions.EMPLOYEE_BRANCH_INTER_ACCESS.name())) {
            if (criteria.getBranch() != null && criteria.getBranch() > 0) {
                if (criteria.isWithSubsidiary()) {
                    List<Branch> branches = getBranches(criteria.getBranch());
                    criteria.setBranches(branches.stream().map(Branch::getId).collect(Collectors.toList()));
                } else {
                    branchList.add(criteria.getBranch());
                    criteria.setBranches(branchList);
                }
            }

            if (criteria.getDepartments() != null && !criteria.getDepartments().isEmpty()) {
                if (criteria.isDepartmentWithSub()) {
                    for (long dept : criteria.getDepartments()) {
                        departmentList.addAll(getDepartmentsSubsidiaries(dept).stream().map(Department::getId).collect(Collectors.toList()));
                    }
                    criteria.setDepartments(departmentList);
                }
            }
        } else if (authorizer.hasPermission(Permissions.EMPLOYEE_BRANCH_ACCESS.name()) && !authorizer.hasPermission(Permissions.EMPLOYEE_BRANCH_INTER_ACCESS.name())) {
            if (criteria.getBranch() != null && criteria.getBranch() > 0) {
                branchList.add(criteria.getBranch());
                criteria.setBranches(branchList);
            } else {
                branchList.add(user.getBranchId());
                criteria.setBranches(branchList);
            }

            if (criteria.getDepartments() != null && !criteria.getDepartments().isEmpty()) {
                if (criteria.isDepartmentWithSub()) {
                    for (long dept : criteria.getDepartments()) {
                        departmentList.addAll(getDepartmentsSubsidiaries(dept).stream().map(Department::getId).collect(Collectors.toList()));
                    }
                    criteria.setDepartments(departmentList);
                }
            }
        } else {
            branchList.add(user.getBranchId());
            criteria.setBranches(branchList);

            if (criteria.getDepartments() != null && !criteria.getDepartments().isEmpty()) {
                if (authorizer.hasPermission(Permissions.EMPLOYEE_DEPARTMENT_INTER_ACCESS.name()) && criteria.isDepartmentWithSub()) {
                    for (long dept : criteria.getDepartments()) {
                        departmentList.addAll(getDepartmentsSubsidiaries(dept).stream().map(Department::getId).collect(Collectors.toList()));
                    }
                }
            } else if (user.getDepartmentId() != null && user.getDepartmentId() > 0) {
                departmentList.add(user.getDepartmentId());
                criteria.setDepartments(departmentList);
            }
        }
    }

    public List<Branch> getBranches(Long branchId) {
        final String uri = String.format(BRANCH_LIST, resourceUrl, branchId);
        return restService.getForList(uri, new ParameterizedTypeReference<>() {});
    }

    public List<Department> getDepartmentsSubsidiaries(long deftId) {
        final String uri = String.format(DEPARTMENT_WITH_SUBSIDIARIES, resourceUrl);
        return restService.getForList(uri, new ParameterizedTypeReference<>(){}, deftId);
    }
}