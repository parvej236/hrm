package bd.org.quantum.hrm.leave;

import bd.org.quantum.authorizer.Authorizer;
import bd.org.quantum.authorizer.helper.UserContext;
import bd.org.quantum.authorizer.model.UserDetails;
import bd.org.quantum.common.resttemplate.RestTemplateService;
import bd.org.quantum.hrm.attendance.AttendanceDao;
import bd.org.quantum.hrm.attendance.AttendanceService;
import bd.org.quantum.hrm.attendance.AttendanceType;
import bd.org.quantum.hrm.common.Branch;
import bd.org.quantum.hrm.common.Department;
import bd.org.quantum.hrm.employee.Employee;
import bd.org.quantum.hrm.employee.EmployeeRepository;
import bd.org.quantum.hrm.role.Permissions;
import bd.org.quantum.hrm.yr_cl.YrCl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class LeaveService {
    private static final String BRANCH_LIST = "%s/api/branches/";
    private static final String DEPARTMENT_LIST_WITH_SUBSIDIARY = "%s/api/departments/%s/subsidiaries";

    @Value("${resource.api.url}")
    String resourceUrl;

    private final LeaveRepository repository;
    private final EmployeeRepository employeeRepository;
    private final AttendanceDao attendanceDao;
    private final AttendanceService attendanceService;
    private final Authorizer authorizer;
    private final RestTemplateService restService;

    public LeaveService(LeaveRepository repository,
                        EmployeeRepository employeeRepository,
                        AttendanceDao attendanceDao,
                        AttendanceService attendanceService,
                        Authorizer authorizer,
                        RestTemplateService restService) {
        this.repository = repository;
        this.employeeRepository = employeeRepository;
        this.attendanceDao = attendanceDao;
        this.attendanceService = attendanceService;
        this.authorizer = authorizer;
        this.restService = restService;
    }

    public Page<LeaveApplication> search(LeaveSearch leaveSearch) {
        processBranchesAndDepartmentsForSearch(leaveSearch);

        Specification<LeaveApplication> omniSpec = Specification
                .where(StringUtils.isEmpty(leaveSearch.getOmniText()) ? null : LeaveApplicationSpecifications.omniText(leaveSearch.getOmniText()));

        Specification<LeaveApplication> employeeIdSpec = Specification
                .where(leaveSearch.getUserId() == null ? null : LeaveApplicationSpecifications.userIdEqual(leaveSearch.getUserId()));

        Specification<LeaveApplication> leaveStatusSpec = Specification
                .where(leaveSearch.getStage() == null ? null : LeaveApplicationSpecifications.stageEqual(leaveSearch.getStage()));

        Specification<LeaveApplication> branchSpec = Specification
                .where(leaveSearch.getBranches() == null ? null : LeaveApplicationSpecifications.branchesIn(leaveSearch.getBranches()));

        Specification<LeaveApplication> deptSpec = Specification
                .where(leaveSearch.getDepartments() == null ? null : LeaveApplicationSpecifications.deptsIn(leaveSearch.getDepartments()));

        Specification<LeaveApplication> finalSpecifications = omniSpec.and(employeeIdSpec).and(leaveStatusSpec).and(branchSpec).and(deptSpec);

        Page<LeaveApplication> leaveApplications;

        Pageable pageable = PageRequest.of(
                leaveSearch.getPage(),
                leaveSearch.getPageSize(),
                Sort.by(Sort.Order.desc("createdAt"))
        );

        if (leaveSearch.isUnpaged()) {
            List<LeaveApplication> list = repository.findAll(finalSpecifications, pageable.getSort());
            leaveApplications = new PageImpl<>(list);
        } else {
            leaveApplications = repository.findAll(finalSpecifications, pageable);
        }
        return leaveApplications;
    }

    public List<LeaveApplication> getLeaveApplicationsByEmployeeInfo(String employeeInfo) {
        return repository.getLeaveApplicationsByEmployeeInfo(employeeInfo);
    }

    public LeaveApplication getById(Long id) {
        return repository.getById(id);
    }

    public LeaveApplication create(LeaveApplication leave) {
        return repository.save(leave);
    }

    public void validateLeave(LeaveApplication leave, BindingResult result) {
        if (leave.getApplicant() == null || leave.getApplicant().getId() == null || leave.getApplicant().getId() == 0) {
            result.rejectValue("applicant.id", "error.required");
        }
        if (leave.getLeaveReason() == null) {
            result.rejectValue("leaveReason", "error.required");
        }
        if (leave.getLeaveFrom() == null || leave.getLeaveTo() == null || leave.getLeaveDays() <= 0) {
            result.rejectValue("leaveDays", "error.required");
        }
    }

    public String numberToWord(int number) {
        String[] digit = {"Zero", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine"};
        String[] twoDigit = {"", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen"};
        String[] ten = {"", "Ten", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"};

        if (number < 10) {
            return digit[number];
        }

        if (number > 10 && number < 20) {
            return twoDigit[number % 10];
        }

        if (number % 10 == 0 && number < 100) {
            return ten[number / 10];
        }

        if (number < 100) {
            return ten[number / 10] + " " + digit[number % 10];
        }

        if (number % 100 == 0) {
            return digit[number / 100] + " Hundred";
        }

        if (number > 100 && ((number / 10) % 10 == 0)) {
            return digit[number / 100] + " Hundred " + digit[number % 100];
        }

        if (number > 100 && ((number % 10) == 0)) {
            return digit[number / 100] + " Hundred " + ten[(number / 10) % 10];
        }

        if ((number % 100 > 10) && (number % 100 < 20)) {
            return digit[number / 100] + " Hundred " + twoDigit[(number % 10) % 10];
        }

        return digit[number / 100] + " Hundred " + ten[(number % 100) / 10] + " " + digit[number % 10];
    }

    public String getEmployeesLeaveBalanceMessage(long empId) {
        String message = "<table style='background: #f9edbe; border: 1px solid #f0c36d;' class='table table-striped table-bordered mb-2'><tr><th colspan='3'>Remaining Leave:</th></tr><tr>";

        Employee employee = employeeRepository.getById(empId);

        List<YrCl> yearWiseYrList = attendanceDao.getAllYearWiseYr(true);
        YrCl currentYear = null;

        LocalDate today = LocalDate.now();
        for (YrCl yr : yearWiseYrList){
            if (today.compareTo(yr.getDateFrom()) >= 0 && today.compareTo(yr.getDateTo()) <= 0) {
                currentYear = yr;
                break;
            }
        }

        int clBalance = attendanceService.getClBalance(employee, today, currentYear);
        int yrBalance = attendanceService.getYrBalance(employee, today);

        String wpd = !StringUtils.isEmpty(employee.getWpd()) ? employee.getWpd().trim().toUpperCase() : "";
        LocalDate endDate = LocalDate.now();
        LocalDate startDate;

        if (employee.getBranch() != null && employee.getBranch() == 6) {
            startDate = endDate.minusMonths(6).plusDays(1);
        } else {
            startDate = endDate.minusMonths(1).plusDays(1);
        }

        LocalDate forIteration = startDate;
        int countWpd = 0;
        if (wpd.length() > 2) {
            while (forIteration.isBefore(endDate)) {
                if (wpd.equals(forIteration.getDayOfWeek().name())) {
                    countWpd++;
                    forIteration = forIteration.plusDays(7);
                } else {
                    forIteration = forIteration.plusDays(1);
                }
            }
        }
        int consumedWpd = attendanceDao.getConsumedLeave(empId, AttendanceType.WEEKLY_PREPARATION_DAY.getValue(), startDate, endDate);
        int wpdBalance = countWpd - consumedWpd;

        message += clBalance > 0 ? "<td>CL - " + clBalance + "</td>" : "<td>CL - 0</td>";
        message += yrBalance > 0 ? "<td>YR - " + yrBalance + "</td>" : "<td>YR - 0</td>";
        message += wpd.length() > 2 && wpdBalance > 0 ? "<td>WPD - " + wpdBalance + "</td>" :
                (wpd.length() == 0 ? "<td class='text-danger'>Please update wpd!</td>" : "<td>WPD - 0</td>");
        message += "</tr></table>";

        return message;
    }

    public String getWpdNote(LeaveApplication application) {
        String wpdNote = "";
        if (application.getApplicant() == null || StringUtils.isEmpty(application.getApplicant().getWpd())) {
            return wpdNote;
        }

        String wpd = application.getApplicant().getWpd().trim().toUpperCase();
        LocalDate dateFrom = application.getLeaveFrom();
        LocalDate dateTo = application.getLeaveTo();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        String note = "WPD (" + wpd + "): ";
        boolean fromMatches = dateFrom.getDayOfWeek().name().equals(wpd);
        boolean toMatches = dateTo.getDayOfWeek().name().equals(wpd);

        if (dateFrom.equals(dateTo)) {
            if (fromMatches) {
                wpdNote += note + dtf.format(dateFrom);
            }
        } else {
            if (fromMatches && toMatches) {
                wpdNote += note + dtf.format(dateFrom) + " & " + dtf.format(dateTo);
            } else if (fromMatches) {
                wpdNote += note + dtf.format(dateFrom);
            } else if (toMatches) {
                wpdNote += note + dtf.format(dateTo);
            }
        }

        return wpdNote;
    }

    private void processBranchesAndDepartmentsForSearch(LeaveSearch search) {
        List<Long> branchList = new ArrayList<>();
        List<Long> deptList = new ArrayList<>();
        UserDetails user = UserContext.getPrincipal().getUserDetails();

        if (authorizer.hasPermission(Permissions.EMPLOYEE_BRANCH_ACCESS.name(), Permissions.EMPLOYEE_BRANCH_INTER_ACCESS.name())) {
            final String uri = String.format(BRANCH_LIST + user.getBranchId(), resourceUrl);
            List<Branch> branches = restService.getForList(uri, new ParameterizedTypeReference<>() {});
            branchList = branches.stream().map(Branch::getId).collect(Collectors.toList());
            search.setBranches(branchList);
        } else if (authorizer.hasPermission(Permissions.EMPLOYEE_BRANCH_ACCESS.name()) && !authorizer.hasPermission(Permissions.EMPLOYEE_BRANCH_INTER_ACCESS.name())) {
            branchList.add(user.getBranchId());
            search.setBranches(branchList);
        } else {
            search.setBranches(Collections.singletonList(user.getBranchId()));

            if (authorizer.hasPermission(Permissions.EMPLOYEE_DEPARTMENT_INTER_ACCESS.name())) {
                final String uri = String.format(DEPARTMENT_LIST_WITH_SUBSIDIARY, resourceUrl, user.getDepartmentId());
                List<Department> departments = restService.getForList(uri, new ParameterizedTypeReference<>() {});
                deptList = departments.stream().map(Department::getId).collect(Collectors.toList());
                search.setDepartments(deptList);
            } else if (user.getDepartmentId() != null) {
                deptList.add(user.getDepartmentId());
                search.setDepartments(deptList);
            }
        }
    }
}
