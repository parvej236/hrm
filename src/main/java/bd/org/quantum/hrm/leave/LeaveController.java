package bd.org.quantum.hrm.leave;

import bd.org.quantum.authorizer.Authorizer;
import bd.org.quantum.authorizer.helper.SecurityCheck;
import bd.org.quantum.authorizer.helper.UserContext;
import bd.org.quantum.authorizer.model.UserDetails;
import bd.org.quantum.common.utils.SubmitResult;
import bd.org.quantum.hrm.common.CommonService;
import bd.org.quantum.hrm.common.Routes;
import bd.org.quantum.hrm.designation.Designation;
import bd.org.quantum.hrm.employee.Employee;
import bd.org.quantum.hrm.employee.EmployeeStatus;
import bd.org.quantum.hrm.role.Permissions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
public class LeaveController {
    private final LeaveService service;
    private final CommonService commonService;
    private final MessageSource messageSource;
    private final Authorizer authorizer;

    public LeaveController(LeaveService service, CommonService commonService,
                           MessageSource messageSource,
                           Authorizer authorizer) {
        this.service = service;
        this.commonService = commonService;
        this.messageSource = messageSource;
        this.authorizer = authorizer;
    }

    @GetMapping(Routes.LEAVE_APPLY)
    public String applyForm(@RequestParam(name = "id", defaultValue = "0") Long id,
                            @RequestParam(name = "action", required = false) String action,
                            @RequestParam(name = "result", required = false) boolean result, Model model) {

        LeaveApplication leave;
        Employee employee = new Employee();
        employee.setDesignation(new Designation());

        if (id == null || id == 0) {
            leave = new LeaveApplication();
            leave.setApplicant(employee);
            leave.setResponsible(employee);
        } else {
            leave = service.getById(id);
            if (leave.getResponsible() == null) {
                leave.setResponsible(employee);
            }
            model.addAttribute("leaveNote", service.getEmployeesLeaveBalanceMessage(leave.getApplicant().getId()));
            model.addAttribute("wpdNote", service.getWpdNote(leave));
        }

        model.addAttribute("leave", leave);
        model.addAttribute("entryLink", Routes.LEAVE_ENTRY);
        referenceData(model);
        if (result) {
            if (action.equals("applied")) {
                SubmitResult.success(messageSource, "leave.apply.success", model);
            }
            if (action.equals("update")) {
                SubmitResult.success(messageSource, "leave.update.success", model);
            }
            if (action.equals("submit")) {
                SubmitResult.success(messageSource, "leave.authorize.success", model);
            }
            if (action.equals("approved")) {
                SubmitResult.success(messageSource, "leave.approve.success", model);
            }
            if (action.equals("confirm")) {
                SubmitResult.success(messageSource, "leave.confirm.success", model);
            }
        }
        return "leave/leave-apply-form";
    }

    @PostMapping(Routes.LEAVE_ENTRY)
    public String leaveEntry(@Valid LeaveApplication leave, BindingResult result, Model model) {
        service.validateLeave(leave, result);
        try {
            if (!result.hasErrors()) {
                UserDetails user = UserContext.getPrincipal().getUserDetails();
                String action = leave.getAction();

                if (leave.getResponsible().getId() == null || leave.getResponsible().getId() == 0) {
                    leave.setResponsible(null);
                }

                if (action.equals("apply")) {
                    leave.setStage(LeaveApplyStage.APPLIED.getTitle());
                    leave.setApplyDate(LocalDate.now());
                    leave.setAppliedBy(user.getId());
                    leave.setAppliedByName(user.getName());
                }
                if (action.equals("update")) {
                    leave.setUpdatedBy(user.getId());
                    leave.setUpdatedByName(user.getName());
                }
                if (action.equals("authorize")) {
                    leave.setStage(LeaveApplyStage.AUTHORIZED.getTitle());
                    leave.setAuthorizedBy(user.getId());
                    leave.setAuthorizedByName(user.getName());
                    leave.setUpdatedBy(user.getId());
                    leave.setUpdatedByName(user.getName());
                }
                if (action.equals("approve")) {
                    leave.setStage(LeaveApplyStage.APPROVED.getTitle());
                    leave.setUpdatedBy(user.getId());
                    leave.setUpdatedByName(user.getName());
                    leave.setApprovedBy(user.getId());
                    leave.setApprovedByName(user.getName());
                }
                if (action.equals("confirm")) {
                    leave.setStage(LeaveApplyStage.CONFIRMED.getTitle());
                    leave.setUpdatedBy(user.getId());
                    leave.setUpdatedByName(user.getName());
                    leave.setConfirmBy(user.getId());
                    leave.setConfirmByName(user.getName());
                }
                if (action.equals("reject")) {
                    leave.setStage(LeaveApplyStage.REJECTED.getTitle());
                    leave.setUpdatedBy(user.getId());
                    leave.setUpdatedByName(user.getName());
                    leave.setApprovedBy(user.getId());
                    leave.setApprovedByName(user.getName());
                }
                leave = service.create(leave);
                model.addAttribute("leave", leave);
                return "redirect:" + Routes.LEAVE_APPLY + "?id=" + leave.getId() + "&action=" + action + "&result=" + String.valueOf(!result.hasErrors()).toUpperCase();
            } else {
                SubmitResult.error(messageSource, "leave.apply.error", model);
            }
        } catch (Exception e) {
            SubmitResult.error(messageSource, "leave.apply.error", model);
        }
        model.addAttribute("leave", leave);
        model.addAttribute("entryLink", Routes.LEAVE_ENTRY);
        referenceData(model);
        return "leave/leave-apply-form";
    }

    @GetMapping(Routes.LEAVE_LIST)
    public String leaveList(Model model) {
        model.addAttribute("entryUrl", Routes.LEAVE_APPLY);
        model.addAttribute("searchUrl", Routes.LEAVE_SEARCH + "?status=0");
        return "leave/leave-list";
    }

    @GetMapping(Routes.LEAVE_SEARCH)
    @ResponseBody
    public Page<LeaveApplication> search(@RequestParam String status, LeaveSearch leaveSearch) {
        if (status.equals("applied")) {
            leaveSearch.setStage("Applied");
        }
        if (status.equals("authorized")) {
            leaveSearch.setStage("Authorized");
        }
        if (status.equals("approved")) {
            leaveSearch.setStage("Approved");
        }
        return service.search(leaveSearch);
    }

    @GetMapping(Routes.LEAVE_AUTHORIZE)
    @SecurityCheck(permissions = {"LEAVE_AUTHORIZE"})
    public String authorizableList(Model model) {
        model.addAttribute("entryUrl", Routes.LEAVE_APPLY);
        model.addAttribute("searchUrl", Routes.LEAVE_SEARCH + "?status=applied");
        return "leave/leave-list";
    }

    @GetMapping(Routes.LEAVE_APPROVE)
    @SecurityCheck(permissions = {"LEAVE_APPROVE"})
    public String approvableList(Model model) {
        model.addAttribute("entryUrl", Routes.LEAVE_APPLY);
        model.addAttribute("searchUrl", Routes.LEAVE_SEARCH + "?status=authorized");
        return "leave/leave-list";
    }

    @GetMapping(Routes.LEAVE_CONFIRM)
    @SecurityCheck(permissions = {"LEAVE_CONFIRM"})
    public String confirmableList(Model model) {
        model.addAttribute("entryUrl", Routes.LEAVE_APPLY);
        model.addAttribute("searchUrl", Routes.LEAVE_SEARCH + "?status=approved");
        return "leave/leave-list";
    }

    @GetMapping(Routes.PRINT_LEAVE_APPLICATION)
    public String printLeaveApplication(@RequestParam Long id, Model model) {
        LeaveApplication leave = service.getById(id);
        if (leave.getResponsible() == null) {
            Employee employee = new Employee();
            employee.setDesignation(new Designation());
            leave.setResponsible(employee);
        }
        model.addAttribute("leave", leave);
        model.addAttribute("leaveDaysInWord", service.numberToWord(leave.getLeaveDays()));
        return "leave/print-leave-application";
    }

    @GetMapping(Routes.LEAVE_BALANCE)
    @ResponseBody
    public ResponseEntity<Map<String, String>> leaveBalance(@RequestParam Long empId) {
        Map<String, String> result = new LinkedHashMap<>();
        result.put("message", service.getEmployeesLeaveBalanceMessage(empId));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping(Routes.GET_LEAVE_APPLICATIONS_BY_EMPLOYEE_INFO)
    @ResponseBody
    public List<LeaveApplication> get(@RequestParam String employeeInfo){
        return service.getLeaveApplicationsByEmployeeInfo(employeeInfo);
    }

    private void referenceData(Model model) {
        model.addAttribute("relation", commonService.getRelations("long"));
        model.addAttribute("leaveTypeMap", LeaveType.getLeaveTypes(null));
        model.addAttribute("hasAuthorizationPermission", authorizer.hasPermission(Permissions.LEAVE_AUTHORIZE.name()));
        model.addAttribute("hasApprovePermission", authorizer.hasPermission(Permissions.LEAVE_APPROVE.name()));
        model.addAttribute("hasConfirmPermission", authorizer.hasPermission(Permissions.LEAVE_CONFIRM.name()));
        model.addAttribute("tempEmpStatuses", Arrays.asList(EmployeeStatus.TEMPORARY_EMPLOYEE_STATUSES));
    }

}
