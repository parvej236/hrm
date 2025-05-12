package bd.org.quantum.hrm.employee;

import bd.org.quantum.authorizer.Authorizer;
import bd.org.quantum.authorizer.helper.UserContext;
import bd.org.quantum.authorizer.model.AdditionalDepartments;
import bd.org.quantum.authorizer.model.UserDetails;
import bd.org.quantum.common.resttemplate.RestTemplateService;
import bd.org.quantum.hrm.changeHistory.ChangeHistoryService;
import bd.org.quantum.hrm.common.Branch;
import bd.org.quantum.hrm.common.Department;
import bd.org.quantum.hrm.role.Permissions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EmployeeService {

    private static final String BRANCH_LIST = "%s/api/branches/";
    private static final String DEPARTMENT_LIST_BY_BRANCH = "%s/api/departments/";
    private static final String DEPARTMENT_LIST_WITH_SUBSIDIARY = "%s/api/departments/%s/subsidiaries";

    @Value("${resource.api.url}")
    String resourceUrl;

    private final EmployeeRepository repository;
    private final ChangeHistoryService historyService;
    private final EducationInfoRepository educationRepository;
    private final ExperienceInfoRepository experienceRepository;
    private final ExtraQualificationInfoRepository qualificationRepository;
    private final FamilyInfoRepository familyRepository;
    private final KhedmotaionInfoRepository khedmotaionRepository;
    private final LanguageSkillInfoRepository languageSkillRepository;
    private final PreJoiningInfoRepository preJoiningRepository;
    private final TrainingInfoRepository trainingRepository;
    private final PromotionInfoRepository promotionRepository;
    private final PunishmentInfoRepository punishmentRepository;
    private final StatusInfoRepository statusRepository;
    private final TransferInfoRepository transferRepository;
    private final RestTemplateService restService;
    private final Authorizer authorizer;

    public EmployeeService(EmployeeRepository repository,
                           ChangeHistoryService historyService,
                           EducationInfoRepository educationRepository,
                           ExperienceInfoRepository experienceRepository,
                           ExtraQualificationInfoRepository qualificationRepository,
                           FamilyInfoRepository familyRepository,
                           KhedmotaionInfoRepository khedmotaionRepository,
                           LanguageSkillInfoRepository languageSkillRepository,
                           PreJoiningInfoRepository preJoiningRepository,
                           TrainingInfoRepository trainingRepository,
                           PromotionInfoRepository promotionRepository,
                           PunishmentInfoRepository punishmentRepository,
                           StatusInfoRepository statusRepository,
                           TransferInfoRepository transferRepository,
                           RestTemplateService restService,
                           Authorizer authorizer) {

        this.repository = repository;
        this.historyService = historyService;
        this.educationRepository = educationRepository;
        this.experienceRepository = experienceRepository;
        this.qualificationRepository = qualificationRepository;
        this.familyRepository = familyRepository;
        this.khedmotaionRepository = khedmotaionRepository;
        this.languageSkillRepository = languageSkillRepository;
        this.preJoiningRepository = preJoiningRepository;
        this.trainingRepository = trainingRepository;
        this.promotionRepository = promotionRepository;
        this.punishmentRepository = punishmentRepository;
        this.statusRepository = statusRepository;
        this.transferRepository = transferRepository;
        this.restService = restService;
        this.authorizer = authorizer;
    }

    @Transactional
    public synchronized Employee create(Employee employee) {
        if (StringUtils.isEmpty(employee.getEmployeeId())) employee.setEmployeeId(null);

        if (employee.getAction().equals("save")) {
            employee.setStatus(EmployeeStatus.IRREGULAR.getName());
            employee.setJoinType(1);
            employee.setStage(Employee.Stage.SAVE.getValue());
            employee.setIsDeleted(false);
        } else if (employee.getAction().equals("submit")) {
            employee.setStage(Employee.Stage.SUBMIT.getValue());
        }

        if(employee.getId() != null ){
            EmployeeInfoChangeHistoryDto dto = new EmployeeInfoChangeHistoryDto(employee);
            historyService.saveChangeHistory(dto);
        }

        return this.repository.save(employee);
    }

    public Employee get(Long id) {
        return repository.getById(id);
    }

    public EmployeeProjection getEmployeeByCode(String code) {
        return repository.getByCode(code);
    }

    public List<EmployeeProjection> getDesignationStatus(Long branch, Long department) {
        return repository.getDesignationStatus(branch, department);
    }
    
    /*public List<EmployeeProjection> getEmployeeInfoByRegOrEmployeeId(String regOrEmployeeId) {
        return repository.getEmployeeInfoByRegOrEmployeeId(regOrEmployeeId);
    }*/

    public List<Employee> getEmployeeInfoByRegOrEmployeeId(String regOrEmployeeId) {
        return repository.getEmployeeInfoByRegOrEmployeeId(regOrEmployeeId);
    }

    public List<Employee> getEmployeesByEmployeeInfo(String employeeInfo) {
        return repository.getEmployeesByEmployeeInfo(employeeInfo);
    }

    public List<Employee> getRegularIrregularEmployeesWithNoId() {
        Long loggedInBranch = UserContext.getPrincipal().getUserDetails().getBranchId();
        List<Long> branchIds = getSubsidiaryBranches(loggedInBranch).stream().map(Branch::getId).collect(Collectors.toList());

        return repository.findAllByEmployeeIdIsNullAndStatusInAndBranchInOrderByHiring(Arrays.asList(EmployeeStatus.REGULAR_EMPLOYEE_STATUSES), branchIds);
    }

    public List<Employee> getTemporaryEmployeesWithNoIdByStatus(String status) {
        Long loggedInBranch = UserContext.getPrincipal().getUserDetails().getBranchId();
        List<Long> branchIds = getSubsidiaryBranches(loggedInBranch).stream().map(Branch::getId).collect(Collectors.toList());

        return repository.findAllByTempEmployeeIdIsNullAndStatusAndBranchInOrderByHiring(status, branchIds);
    }

    public String generateEmployeeId(long id, String type) {
        String employeeId = "";
        Employee employee = get(id);

        if (type.equals("regular")) {
            String year = DateTimeFormatter.ofPattern("yy").format(employee.getHiring());
            int nextEmpIdValue = repository.getEmpIdNextValue();
            employeeId = String.format("%05d", nextEmpIdValue);
            employee.setEmployeeId(employeeId);
            employee.setTempEmployeeId(null);
            employeeId = year + employeeId;
        } else {
            if (employee.getStatus() != null && employee.getHiring() != null) {
                String prefix = employee.getStatus().equals(EmployeeStatus.MUSTER_ROLL.getName()) ? "R-" :
                        employee.getStatus().equals(EmployeeStatus.CONTRACTUAL.getName()) ? "C-" :
                                employee.getStatus().equals(EmployeeStatus.PART_TIME.getName()) ? "P-" : "T-";
                int joinYear = Integer.parseInt(employee.getHiring().format(DateTimeFormatter.ofPattern("yy")));
                int lastId = repository.lastTempIdByYearAndStatusIn(joinYear, Arrays.asList(EmployeeStatus.TEMPORARY_EMPLOYEE_STATUSES)) + 1;
                employeeId = prefix + joinYear + String.format("%03d", lastId);
                employee.setTempEmployeeId(employeeId);
            }
        }

        repository.save(employee);
        return employeeId;
    }

    public Page<Employee> search(EmployeeSearch search) {
        processBranchesAndDepartmentsForSearch(search);

        Specification<Employee> omniText = Specification
                .where(StringUtils.isEmpty(search.getOmniText()) ? null : EmployeeSpecifications.omniText(search.getOmniText()));

        Specification<Employee> eIdSpec = Specification
                .where(StringUtils.isEmpty(search.getEmployeeId()) ? null : EmployeeSpecifications.employeeIdEqual(search.getEmployeeId()));

        Specification<Employee> branchSpec = Specification
                .where(search.getBranches() == null ? null : EmployeeSpecifications.branchesIn(search.getBranches()));

        Specification<Employee> deptSpec = Specification
                .where(search.getDepartments() == null ? null : EmployeeSpecifications.departmentsIn(search.getDepartments()));

        Specification<Employee> genderSpec = Specification
                .where(StringUtils.isEmpty(search.getGender()) ? null : EmployeeSpecifications.genderEqual(search.getGender()));

        Specification<Employee> designationSpec = Specification
                .where(StringUtils.isEmpty(search.getDesignation()) ? null : EmployeeSpecifications.designationEqual(search.getDesignation()));

        Specification<Employee> statusSpec = Specification
                .where(StringUtils.isEmpty(search.getEmpStatus()) ? EmployeeSpecifications.empStatusIn(search.getStatuses()) :
                        EmployeeSpecifications.empStatusEqual(search.getEmpStatus()));

        Specification<Employee> finalSpecifications = omniText.and(eIdSpec).and(branchSpec).and(deptSpec).and(genderSpec).and(designationSpec).and(statusSpec);

        Page<Employee> employees;

        Pageable pageable = PageRequest.of(
                search.getPage(),
                search.getPageSize(),
                Sort.by(Sort.Direction.fromString(search.getSortDirection()), search.getSortBy()));

        if (search.isUnpaged()) {
            List<Employee> list = repository.findAll(finalSpecifications, pageable.getSort());
            employees = new PageImpl<>(list);
        } else {
            employees = repository.findAll(finalSpecifications, pageable);
        }
        return employees;
    }

    public Employee checkEmployee(Long memberId) {
        return repository.findByMember(memberId);
    }

    public boolean hasDuplicateEmployeeId(String employeeId, Long id) {
        return repository.existsByEmployeeIdIsNotNullAndEmployeeIdEqualsAndIdNot(employeeId, id);
    }

    public boolean hasDepartmentByBranch(Long branch) {
        return repository.existsByDepartmentIsNotNullAndBranchEquals(branch);
    }

    public void updateImagePath(String path, Long id) {
        repository.updateImagePath(path, id);
    }

    public List<Branch> getBranches() {
        UserDetails userDetails = UserContext.getPrincipal().getUserDetails();
        List<Branch> branchList = new ArrayList<>();
        if (authorizer.hasPermission(Permissions.EMPLOYEE_BRANCH_INTER_ACCESS.name())) {
            branchList = getSubsidiaryBranches(userDetails.getBranchId());
        } else {
            branchList.add(new Branch(userDetails.getBranchId(), userDetails.getBranchName()));
        }
        return branchList;
    }

    public List<Branch> getSubsidiaryBranches(long branchId) {
        final String uri = String.format(BRANCH_LIST + branchId, resourceUrl);
        return restService.getForList(uri, new ParameterizedTypeReference<>() {});
    }

    public  List<Department> getDepartmentList(Long branchId){
        List<Department> departmentList = new ArrayList<>();
        UserDetails userDetails = UserContext.getPrincipal().getUserDetails();

        if (authorizer.hasPermission(Permissions.EMPLOYEE_BRANCH_ACCESS.name())) {
            departmentList = this.getDepartmentsByBranchId(branchId);
        } else if (authorizer.hasPermission(Permissions.EMPLOYEE_DEPARTMENT_INTER_ACCESS.name())) {
            departmentList = this.getDepartmentsWithSubsidiary();
        } else {
            departmentList.add(new Department(userDetails.getDepartmentId(), userDetails.getDepartmentName()));
        }

        if (userDetails.getAdditionalDepartmentList() != null && !userDetails.getAdditionalDepartmentList().isEmpty()) {
            for (AdditionalDepartments dept : userDetails.getAdditionalDepartmentList()) {
                departmentList.add(new Department(dept.getDepartmentId(), dept.getDepartmentName()));
            }
        }

        departmentList.sort(Comparator.comparing(Department::getName));

        return departmentList;
    }

    public List<Department> getDepartmentsByBranchId(Long branchId) {
        final String uri = String.format(DEPARTMENT_LIST_BY_BRANCH + branchId, resourceUrl);
        return restService.getForList(uri, new ParameterizedTypeReference<>() {
        });
    }

    public List<Department> getDepartmentsWithSubsidiary() {
        final String uri = String.format(DEPARTMENT_LIST_WITH_SUBSIDIARY, resourceUrl, UserContext.getPrincipal().getUserDetails().getDepartmentId());
        return restService.getForList(uri, new ParameterizedTypeReference<>() {
        });
    }

    public List<Department> getDepartmentsWithSubsidiary(long deptId) {
        final String uri = String.format(DEPARTMENT_LIST_WITH_SUBSIDIARY, resourceUrl, deptId);
        return restService.getForList(uri, new ParameterizedTypeReference<>() {
        });
    }

    public void validate(Employee employee, BindingResult result) {
        if (employee.getMember() == null || employee.getMember() == 0) {
            result.rejectValue("member", "error.required");
        }

        if (employee.getBranch() == null || employee.getBranch() == 0) {
            result.rejectValue("branch", "error.required");
        }

        if (employee.getAction().equals("submit")) {
            if (employee.getDesignation() == null || employee.getDesignation().getId() == 0) {
                result.rejectValue("designation", "error.required");
            }

            if (employee.getHiring() == null) {
                result.rejectValue("hiring", "error.required");
            }
        } else if (employee.getAction().equals("save") && employee.getDesignation().getId() == 0) {
            employee.setDesignation(null);
        }

        if (!org.thymeleaf.util.StringUtils.isEmpty(employee.getEmployeeId()) && hasDuplicateEmployeeId(employee.getEmployeeId(), employee.getId())) {
            result.rejectValue("employeeId", "error.duplicate.employee.id");
        }
    }

    private void processBranchesAndDepartmentsForSearch(EmployeeSearch search) {
        List<Long> branchList = new ArrayList<>();
        List<Long> deptList = new ArrayList<>();
        UserDetails user = UserContext.getPrincipal().getUserDetails();

        if (authorizer.hasPermission(Permissions.EMPLOYEE_BRANCH_ACCESS.name(), Permissions.EMPLOYEE_BRANCH_INTER_ACCESS.name())) {
            if (search.getBranch() == null ) {
                final String uri = String.format(BRANCH_LIST + user.getBranchId(), resourceUrl);
                List<Branch> branches = restService.getForList(uri, new ParameterizedTypeReference<>() { });
                branchList = branches.stream().map(Branch::getId).collect(Collectors.toList());
            } else {
                branchList.add(search.getBranch());
            }
            search.setBranches(branchList);

            if (search.getDepartment() != null && search.getDepartment() > 0) {
                search.setDepartments(Collections.singletonList(search.getDepartment()));
            }
        } else if (authorizer.hasPermission(Permissions.EMPLOYEE_BRANCH_ACCESS.name()) && !authorizer.hasPermission(Permissions.EMPLOYEE_BRANCH_INTER_ACCESS.name())) {
            branchList.add(user.getBranchId());
            search.setBranches(branchList);

            if (search.getDepartment() != null && search.getDepartment() > 0) {
                search.setDepartments(Collections.singletonList(search.getDepartment()));
            }
        } else {
            boolean hasDepartments = hasDepartmentByBranch(user.getBranchId());
            if (!hasDepartments) {
                branchList.add(user.getBranchId());
                search.setBranches(branchList);
            }

            if (authorizer.hasPermission(Permissions.EMPLOYEE_DEPARTMENT_INTER_ACCESS.name())) {
                final String uri = String.format(DEPARTMENT_LIST_WITH_SUBSIDIARY, resourceUrl, user.getDepartmentId());
                List<Department> departments = restService.getForList(uri, new ParameterizedTypeReference<>() {});
                deptList = departments.stream().map(Department::getId).collect(Collectors.toList());
                search.setDepartments(deptList);
            } else if (user.getDepartmentId() != null) {
                deptList.add(user.getDepartmentId());
                search.setDepartments(deptList);
            } else {
                branchList.add(0L);
                search.setBranches(branchList);
            }
        }
    }
    
    public List<EducationInfo> getEducationInfoListByEmployee(Long employeeId) {
        return educationRepository.findAllByEmployeeIdOrderById(employeeId);
    }

    public List<ExperienceInfo> getExperienceInfoListByEmployee(Long employeeId) {
        return experienceRepository.findAllByEmployeeIdOrderById(employeeId);
    }

    public List<ExtraQualificationInfo> getExtraQualificationInfoListByEmployee(Long employeeId) {
        return qualificationRepository.findAllByEmployeeIdOrderById(employeeId);
    }

    public List<FamilyInfo> getFamilyInfoListByEmployee(Long employeeId) {
        return familyRepository.findAllByEmployeeIdOrderById(employeeId);
    }

    public List<KhedmotaionInfo> getKhedmotaionInfoListByEmployee(Long employeeId) {
        return khedmotaionRepository.findAllByEmployeeIdOrderById(employeeId);
    }

    public List<LanguageSkillInfo> getLanguageSkillInfoListByEmployee(Long employeeId) {
        return languageSkillRepository.findAllByEmployeeIdOrderById(employeeId);
    }

    public List<PreJoiningInfo> getPreJoiningInfoListByEmployee(Long employeeId) {
        return preJoiningRepository.findAllByEmployeeIdOrderById(employeeId);
    }

    public List<TrainingInfo> getTrainingInfoListByEmployee(Long employeeId) {
        return trainingRepository.findAllByEmployeeIdOrderById(employeeId);
    }

    public List<PromotionInfo> getPromotionInfoListByEmployee(Long employeeId) {
        return promotionRepository.findAllByEmployeeIdOrderById(employeeId);
    }

    public List<PunishmentInfo> gerPunishmentInfoListByEmployee(Long employeeId) {
        return punishmentRepository.findAllByEmployeeIdOrderById(employeeId);
    }

    public List<StatusInfo> getStatusInfoListByEmployee(Long employeeId) {
        return statusRepository.findAllByEmployeeIdOrderById(employeeId);
    }

    public List<TransferInfo> getTransferInfoListByEmployee(Long employeeId) {
        return transferRepository.getAllByEmployeeIdOrderById(employeeId);
    }

    public String getStatusesWithDate(long empId) {
        try {
            return repository.statusesWithDate(empId);
        } catch (Exception e) {
            return null;
        }
    }
}
