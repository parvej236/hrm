package bd.org.quantum.hrm.employee;

import bd.org.quantum.authorizer.Authorizer;
import bd.org.quantum.authorizer.helper.SecurityCheck;
import bd.org.quantum.authorizer.helper.UserContext;
import bd.org.quantum.authorizer.model.UserDetails;
import bd.org.quantum.common.utils.Image;
import bd.org.quantum.common.utils.ImageService;
import bd.org.quantum.common.utils.SubmitResult;
import bd.org.quantum.hrm.common.*;
import bd.org.quantum.hrm.common.Routes;
import bd.org.quantum.hrm.designation.DesignationService;
import bd.org.quantum.hrm.role.Permissions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Controller
public class EmployeeController {

    @Value("${member.url}")
    private String memberUrl;

    @Value("${file.path}")
    String filePath;

    private final EmployeeService service;
    private final ImageService imageService;
    private final DesignationService designationService;
    private final Authorizer authorizer;
    private final MessageSource messageSource;

    public EmployeeController(EmployeeService service,
                              ImageService imageService,
                              DesignationService designationService,
                              Authorizer authorizer,
                              MessageSource messageSource) {
        this.service = service;
        this.imageService = imageService;
        this.designationService = designationService;
        this.authorizer = authorizer;
        this.messageSource = messageSource;
    }

    @GetMapping(Routes.ADD_EMPLOYEE)
    @SecurityCheck(permissions = {"EMPLOYEE_CREATE"})
    public String index(Model model) {
        Employee employee = new Employee();
        model.addAttribute("hiringDate", employee.getHiring() == null ? new Date() : employee.getHiring());
        model.addAttribute("employee", employee);
        model.addAttribute("actionUrl", Routes.ADD_EMPLOYEE);
        model.addAttribute("profileUrl", Routes.EMPLOYEE_PROFILE);
        getReferenceData(model);
        return "employee/employee-form";
    }

    @PostMapping(value = Routes.ADD_EMPLOYEE)
    @SecurityCheck(permissions = {"EMPLOYEE_CREATE"})
    public String create(@Valid Employee employee, Model model, BindingResult result) {
        service.validate(employee, result);
        try{
            if (!result.hasErrors()) {
                employee = service.create(employee);
                return "redirect:" + Routes.UPDATE_EMPLOYEE + "?id=" + employee.getId() + "&result=" + String.valueOf(!result.hasErrors()).toUpperCase();
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        model.addAttribute("employee", employee);
        getReferenceData(model);
        return "employee/employee-form";
    }

    @GetMapping(Routes.UPDATE_EMPLOYEE)
    @SecurityCheck(permissions = {"EMPLOYEE_UPDATE"})
    public String update(@RequestParam Long id, @RequestParam(defaultValue = "") String result, Model model) {
        Employee employee = service.get(id);
        getReferenceData(model);
        infoReferenceData(model, id);
        model.addAttribute("employee", employee);

        if (result.equals("TRUE")) {
            SubmitResult.success(messageSource, "employee.create.success", model);
        } else if(result.equals("FALSE")) {
            SubmitResult.error(messageSource, "employee.create.error", model);
        }

        return "employee/employee-form";
    }

    @PostMapping(value = Routes.UPDATE_EMPLOYEE)
    @SecurityCheck(permissions = {"EMPLOYEE_UPDATE"})
    public String update(@Valid Employee employee, Model model, BindingResult result) {
        service.validate(employee, result);
        try{
            if (!result.hasErrors()) {
                employee = service.create(employee);
                return "redirect:" + Routes.UPDATE_EMPLOYEE + "?id=" + employee.getId() + "&result=" + String.valueOf(!result.hasErrors()).toUpperCase();
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        model.addAttribute("employee", employee);
        getReferenceData(model);
        return "employee/employee-form";
    }

    @PostMapping(Routes.EMPLOYEE_IMAGE_UPLOAD)
    @ResponseBody
    public Image uploadImage(@RequestBody Image image, BindingResult result) {
        Image newImage = new Image();
        try {
            if(!result.hasErrors()) {
                image.setPath(filePath);
                newImage = imageService.upload(image, "Image");
                newImage.setObjId(image.getObjId());
                service.updateImagePath(newImage.getPath(), newImage.getObjId());
                log.debug("image uploaded successfully");
            } else {
                log.debug("image uploaded error due to binding error");
            }
        } catch (Exception e) {
            log.debug("image uploaded exception..." + e);
        }

        return newImage;
    }

    @GetMapping(Routes.EMPLOYEE_IMAGE_DOWNLOAD)
    @ResponseBody
    public Image downloadImage(@RequestParam String path) {
        Image image = new Image();

        if (!StringUtils.isEmpty(path)) {
            image = imageService.downloadImg(path);
        }

        return image;
    }

    @GetMapping(Routes.EMPLOYEE_PROFILE)
    @SecurityCheck(permissions = {"EMPLOYEE_VIEW"})
    public String profile(@RequestParam Long id, Model model) {
        Employee employee = service.get(id);
        model.addAttribute("employee", employee);
        model.addAttribute("statuses", service.getStatusesWithDate(id));
        model.addAttribute("age", employee.getDateOfBirth() != null ?
                (LocalDate.now().getYear() - employee.getDateOfBirth().getYear()) : 0);
        model.addAttribute("updateUrl", Routes.UPDATE_EMPLOYEE + "?id=" + id);
        model.addAttribute("detailsUrl", Routes.EMPLOYEE_PROFILE_DETAILS + "?id=" + id);
        model.addAttribute("attDetailsUrl", Routes.ATTENDANCE_DETAILS + "?id=" + id);
        getReferenceData(model);
        infoReferenceData(model, id);
        return "employee/employee-profile";
    }

    @GetMapping(Routes.EMPLOYEE_PROFILE_DETAILS)
    @SecurityCheck(permissions = {"EMPLOYEE_VIEW"})
    public String profileDetails(@RequestParam Long id, Model model) {
        Employee employee = service.get(id);
        model.addAttribute("employee", employee);
        getReferenceData(model);
        infoReferenceData(model, id);
        return "employee/employee-profile-details";
    }

    @GetMapping(Routes.EMPLOYEES)
    @SecurityCheck(permissions = {"EMPLOYEE_VIEW"})
    public String search(EmployeeSearch search, Model model) {
        model.addAttribute("formTitle", "Employee List");
        model.addAttribute("searchUrl", Routes.EMPLOYEE_SEARCH);
        model.addAttribute("profileUrl", Routes.EMPLOYEE_PROFILE);
        model.addAttribute("editUrl", Routes.UPDATE_EMPLOYEE);
        model.addAttribute("detailsUrl", Routes.ATTENDANCE_DETAILS);
        getReferenceData(model);
        return "employee/employee-list";
    }

    @GetMapping(Routes.EMPLOYEE_SEARCH)
    @ResponseBody
    public Page<Employee> members(EmployeeSearch searchForm){
        if (StringUtils.isEmpty(searchForm.getEmpStatus())) {
            searchForm.setStatuses(Arrays.asList(EmployeeStatus.RUNNING_ALL_EMPLOYEE_STATUSES));
        }
        return service.search(searchForm);
    }

    @GetMapping(Routes.SHOW_EMPLOYEE)
    @ResponseBody
    public Employee get(@PathVariable Long id){
        return service.get(id);
    }

    @GetMapping(Routes.API_GET_EMPLOYEE_BY_CODE)
    @ResponseBody
    public ResponseEntity<EmployeeProjection> getEmployeeByCode(@RequestParam String empCode) {
        return new ResponseEntity<>(service.getEmployeeByCode(empCode), HttpStatus.OK);
    }

    @GetMapping(Routes.EMPLOYEE_CHECK)
    @ResponseBody
    public ResponseEntity<Employee> checkEmployee(@PathVariable Long memberId){
        Employee employee = service.checkEmployee(memberId);
        if (employee == null) {
            employee = new Employee();
        }
        return ResponseEntity.ok().body(employee);
    }

    @GetMapping(Routes.ASSIGN_EMPLOYEE_ID)
    @SecurityCheck(permissions = {"EMPLOYEE_CREATE"})
    public String view(Model model) {
        model.addAttribute("regularIrregularWithNoId", service.getRegularIrregularEmployeesWithNoId());
        model.addAttribute("musterRollWithNoId", service.getTemporaryEmployeesWithNoIdByStatus(EmployeeStatus.MUSTER_ROLL.getName()));
        model.addAttribute("contractualWithNoId", service.getTemporaryEmployeesWithNoIdByStatus(EmployeeStatus.CONTRACTUAL.getName()));
        model.addAttribute("partTimeWithNoId", service.getTemporaryEmployeesWithNoIdByStatus(EmployeeStatus.PART_TIME.getName()));
        model.addAttribute("internWithNoId", service.getTemporaryEmployeesWithNoIdByStatus(EmployeeStatus.INTERN.getName()));
        model.addAttribute("probationaryWithNoId", service.getTemporaryEmployeesWithNoIdByStatus(EmployeeStatus.PROBATIONARY.getName()));
        return "employee/employee-id-form";
    }

    @GetMapping(Routes.GENERATE_EMPLOYEE_ID)
    @SecurityCheck(permissions = {"EMPLOYEE_VIEW"})
    @ResponseBody
    public Map<String, Object> generate(@RequestParam long id, @RequestParam String type) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("empId", service.generateEmployeeId(id, type));
        return response;
    }

    @GetMapping(Routes.API_BRANCHES)
    @ResponseBody
    public List<Branch> getBranches() {
        return service.getBranches();
    }

    @GetMapping(Routes.API_DEPARTMENTS)
    @ResponseBody
    public List<Department> getDepartments(@PathVariable Long branchId) {
        return service.getDepartmentList(branchId);
    }

    public void getReferenceData(Model model) {
        UserDetails user = UserContext.getPrincipal().getUserDetails();

        model.addAttribute("weekDaysMap", WeekDays.getAllWeekDay());
        model.addAttribute("designationMap", designationService.getDesignationMap());
        model.addAttribute("religionMap", Religion.getAllReligion());
        model.addAttribute("genderMap", Gender.getAllGender());
        model.addAttribute("maritalStatusMap", MaritalStatus.getAllMaritalStatus());
        model.addAttribute("bloodGroupMap", BloodGroup.getAllBloodGroup());
        model.addAttribute("empStatusMap", EmployeeStatus.getAllStatus());
        model.addAttribute("branchList", service.getBranches());
        model.addAttribute("departmentList", service.getDepartmentList(user.getBranchId()));
        model.addAttribute("tempEmpStatuses", Arrays.asList(EmployeeStatus.TEMPORARY_EMPLOYEE_STATUSES));
        model.addAttribute("hasDetailsViewAccess", authorizer.hasPermission(Permissions.EMPLOYEE_VIEW.name()));

        Map<String, String> typeMap = new LinkedHashMap<>();
        typeMap.put("PROMOTION", "Promotion");
        typeMap.put("CONVERT", "Convert");
        model.addAttribute("typeMap", typeMap);
        model.addAttribute("locationMap", SalaryLocation.getSalaryLocationMap());
    }

    public void infoReferenceData(Model model, Long employeeId) {
        model.addAttribute("educationInfoList", service.getEducationInfoListByEmployee(employeeId));
        model.addAttribute("experienceInfoList", service.getExperienceInfoListByEmployee(employeeId));
        model.addAttribute("extraQualificationInfoList", service.getExtraQualificationInfoListByEmployee(employeeId));
        model.addAttribute("familyInfoList", service.getFamilyInfoListByEmployee(employeeId));
        model.addAttribute("khedmotInfoList", service.getKhedmotaionInfoListByEmployee(employeeId));
        model.addAttribute("langSkillInfoList", service.getLanguageSkillInfoListByEmployee(employeeId));
        model.addAttribute("preJoiningInfoList", service.getPreJoiningInfoListByEmployee(employeeId));
        model.addAttribute("trainingInfoList", service.getTrainingInfoListByEmployee(employeeId));
        model.addAttribute("promotionInfoList", service.getPromotionInfoListByEmployee(employeeId));
        model.addAttribute("punishmentInfoList", service.gerPunishmentInfoListByEmployee(employeeId));
        model.addAttribute("statusInfoList", service.getStatusInfoListByEmployee(employeeId));
        model.addAttribute("transferInfoList", service.getTransferInfoListByEmployee(employeeId));
        model.addAttribute("allChangeHistoryUrl", Routes.ALL_CHANGES + "?dataId=" + employeeId + "&className=" + Employee.class.getSimpleName());
        model.addAttribute("statusInfoChangeHistoryUrl", Routes.ALL_CHANGES + "?dataId=" + employeeId + "&className="  + StatusInfo.class.getSimpleName());
        model.addAttribute("promotionInfoChangeHistoryUrl", Routes.ALL_CHANGES + "?dataId=" + employeeId + "&className="  + PromotionInfo.class.getSimpleName());
        model.addAttribute("transferInfoChangeHistoryUrl", Routes.ALL_CHANGES + "?dataId=" + employeeId + "&className="  + TransferInfo.class.getSimpleName());
        model.addAttribute("preJoiningChangeHistoryUrl", Routes.ALL_CHANGES + "?dataId=" + employeeId + "&className=" +  PreJoiningInfo.class.getSimpleName());
        model.addAttribute("khedmotaionInfoChangeHistoryUrl", Routes.ALL_CHANGES + "?dataId=" + employeeId + "&className="  + KhedmotaionInfo.class.getSimpleName());
        model.addAttribute("academicInfoChangeHistoryUrl", Routes.ALL_CHANGES + "?dataId=" + employeeId + "&className=" + EducationInfo.class.getSimpleName());
        model.addAttribute("experienceInfoChangeHistoryUrl", Routes.ALL_CHANGES + "?dataId=" + employeeId + "&className=" + ExperienceInfo.class.getSimpleName());
        model.addAttribute("trainingInfoChangeHistoryUrl", Routes.ALL_CHANGES + "?dataId=" + employeeId + "&className="  + TrainingInfo.class.getSimpleName());
        model.addAttribute("extraCurricularInfoChangeHistoryUrl", Routes.ALL_CHANGES + "?dataId=" + employeeId + "&className="  + ExtraQualificationInfo.class.getSimpleName());
        model.addAttribute("languageSkillInfoChangeHistoryUrl", Routes.ALL_CHANGES + "?dataId=" + employeeId + "&className="  + LanguageSkillInfo.class.getSimpleName());
        model.addAttribute("familyInfoChangeHistoryUrl", Routes.ALL_CHANGES + "?dataId=" + employeeId + "&className="  + FamilyInfo.class.getSimpleName());
        model.addAttribute("punishmentInfoChangeHistoryUrl", Routes.ALL_CHANGES + "?dataId=" + employeeId + "&className="  + PunishmentInfo.class.getSimpleName());
        model.addAttribute("memberProfileUrl", memberUrl + "/member-profile?id=");
        model.addAttribute("employeeProfileUrl",  "/employee-profile?id=" + employeeId);
    }

    @GetMapping(Routes.GET_EMPLOYEES_BY_EMPLOYEE_INFO)
    @ResponseBody
    public ResponseEntity getEmployeesByEmployeeInfo(String employeeInfo) {
        List<Employee> employeeList = service.getEmployeesByEmployeeInfo(employeeInfo);
        return (employeeList != null && !employeeList.isEmpty())
                ? ResponseEntity.ok().body(employeeList)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body("\"Status\":\"Not Found!\"");
    }

    @GetMapping(Routes.API_EMPLOYEES_BY_EMPLOYEE_INFO)
    @ResponseBody
    public ResponseEntity apiEmployeesByEmployeeInfo(String employeeInfo) {
        List<Employee> employeeList = service.getEmployeesByEmployeeInfo(employeeInfo);
        return (employeeList != null && !employeeList.isEmpty())
                ? ResponseEntity.ok().body(employeeList)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body("\"Status\":\"Not Found!\"");
    }
}
