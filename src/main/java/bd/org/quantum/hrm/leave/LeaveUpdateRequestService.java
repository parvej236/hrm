package bd.org.quantum.hrm.leave;

import bd.org.quantum.authorizer.Authorizer;
import bd.org.quantum.authorizer.helper.UserContext;
import bd.org.quantum.authorizer.model.UserDetails;
import bd.org.quantum.common.resttemplate.RestTemplateService;
import bd.org.quantum.hrm.attendance.AttendanceDao;
import bd.org.quantum.hrm.common.Branch;
import bd.org.quantum.hrm.common.Department;
import bd.org.quantum.hrm.role.Permissions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LeaveUpdateRequestService {
    private static final String BRANCH_LIST = "%s/api/branches/";
    private static final String DEPARTMENT_LIST_WITH_SUBSIDIARY = "%s/api/departments/%s/subsidiaries";

    @Value("${resource.api.url}")
    String resourceUrl;
    private Authorizer authorizer;
    private AttendanceDao attendanceDao;
    private LeaveRepository leaveRepository;
    private RestTemplateService restService;
    private LeaveUpdateRequestRepository repository;
    private LeaveDetailsOldRepository oldDetailsRepository;

    public LeaveUpdateRequestService(Authorizer authorizer, AttendanceDao attendanceDao, LeaveRepository leaveRepository, RestTemplateService restService,
                                     LeaveUpdateRequestRepository repository, LeaveDetailsOldRepository oldDetailsRepository) {
        this.authorizer = authorizer;
        this.attendanceDao = attendanceDao;
        this.leaveRepository = leaveRepository;
        this.restService = restService;
        this.repository = repository;
        this.oldDetailsRepository = oldDetailsRepository;
    }

    public LeaveUpdateRequest create(LeaveUpdateRequest request) {
        UserDetails user = UserContext.getPrincipal().getUserDetails();
        request.setUpdatedBy(user.getId());
        request.setUpdatedAt(new Date());

        if (request.getAction().equals("request")) {
            request.setRequestedBy(user.getId());
            request.setRequestedByName(user.getName());
            request.setRequestedOn(LocalDate.now());
            request.setStage("Requested");
        } else if (request.getAction().equals("authorize")) {
            request.setAuthorizedBy(user.getId());
            request.setApprovedByName(user.getName());
            request.setApprovedOn(LocalDate.now());
            request.setStage(LeaveApplyStage.AUTHORIZED.getTitle());
        } else  if (request.getAction().equals("approve")) {
            request.setApprovedBy(user.getId());
            request.setApprovedByName(user.getName());
            request.setApprovedOn(LocalDate.now());
            request.setStage(LeaveApplyStage.APPROVED.getTitle());

            LeaveApplication application = leaveRepository.getById(request.getApplicationId());
            application.setLeaveFrom(request.getDateFrom());
            application.setLeaveTo(request.getDateTo());
            application.setLeaveDays(request.getDuration());
            application.getLeaveDetails().clear();
            List<LeaveDetails> detailsList = new ArrayList<>();

            request.getUpdateDetails().forEach(update -> {
                LeaveDetails details = new LeaveDetails();
                details.setLeaveType(update.getLeaveType());
                details.setDateFrom(update.getDateFrom());
                details.setDateTo(update.getDateTo());
                details.setDays(update.getDays());
                details.setRemarks(update.getRemarks());
                detailsList.add(details);
            });

            application.getLeaveDetails().addAll(detailsList);
            leaveRepository.save(application);

        } else  if (request.getAction().equals("reject")) {
            request.setRejectedBy(user.getId());
            request.setRejectedByName(user.getName());
            request.setRejectedOn(LocalDate.now());
            request.setStage(LeaveApplyStage.REJECTED.getTitle());
        }

        return repository.save(request);
    }

    public LeaveUpdateRequest getById(Long id) {
        return repository.getById(id);
    }

    public List<LeaveDetailsOld> getAllLeaveDetailsOldByRequest(Long requestId) {
        return oldDetailsRepository.findAllByRequest(requestId);
    }

    public Page<LeaveUpdateRequestDto> search(LeaveUpdateRequestSearch search) {
        processBranchesAndDepartmentsForSearch(search);

        List<LeaveUpdateRequestDto> requestDtos = attendanceDao.getLeaveUpdateRequestList(search);
        Pageable pageable = PageRequest.of(0, 10);

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), requestDtos.size());

        List<LeaveUpdateRequestDto> subList = requestDtos.subList(start, end);

        return new PageImpl<>(subList, pageable, requestDtos.size());
    }

    private void processBranchesAndDepartmentsForSearch(LeaveUpdateRequestSearch search) {
        List<Long> branchList = new ArrayList<>();
        List<Long> deptList = new ArrayList<>();
        UserDetails user = UserContext.getPrincipal().getUserDetails();

        if (authorizer.hasPermission(Permissions.EMPLOYEE_BRANCH_ACCESS.name(), Permissions.EMPLOYEE_BRANCH_INTER_ACCESS.name())) {
            final String uri = String.format(BRANCH_LIST + user.getBranchId(), resourceUrl);
            List<Branch> branches = restService.getForList(uri, new ParameterizedTypeReference<>() {});
            branchList = branches.stream().map(Branch::getId).collect(Collectors.toList());
            search.setBranches(branchList);
        } else if (authorizer.hasPermission(Permissions.EMPLOYEE_BRANCH_ACCESS.name()) &&
                !authorizer.hasPermission(Permissions.EMPLOYEE_BRANCH_INTER_ACCESS.name())) {
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
