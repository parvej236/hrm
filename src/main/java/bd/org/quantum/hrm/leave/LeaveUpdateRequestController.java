package bd.org.quantum.hrm.leave;

import bd.org.quantum.authorizer.Authorizer;
import bd.org.quantum.authorizer.helper.SecurityCheck;
import bd.org.quantum.common.utils.SubmitResult;
import bd.org.quantum.hrm.common.Routes;
import bd.org.quantum.hrm.designation.Designation;
import bd.org.quantum.hrm.employee.Employee;
import bd.org.quantum.hrm.role.Permissions;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.util.List;

@Controller
public class LeaveUpdateRequestController {
    private LeaveUpdateRequestService service;
    private MessageSource messageSource;
    private LeaveService leaveService;
    private Authorizer authorizer;

    public LeaveUpdateRequestController(LeaveUpdateRequestService service, MessageSource messageSource,
                                        LeaveService leaveService, Authorizer authorizer) {
        this.service = service;
        this.messageSource = messageSource;
        this.leaveService = leaveService;
        this.authorizer = authorizer;
    }

    @GetMapping(Routes.ADD_LEAVE_UPDATE_REQUEST)
    @SecurityCheck(permissions = {"LEAVE_AUTHORIZE","LEAVE_APPROVE"})
    public String index(Model model) {
        LeaveUpdateRequest request = new LeaveUpdateRequest();
        LeaveApplication application = new LeaveApplication();
        Employee employee = new Employee();
        Designation designation = new Designation();

        employee.setDesignation(designation);
        application.setApplicant(employee);
        request.setApplication(application);

        referenceData(model);
        model.addAttribute("request", request);
        model.addAttribute("existsRequestUrl", Routes.EXISTS_UPDATE_REQUEST_BY_LEAVE);
        return "leave/leave-update-request-form";
    }

    @PostMapping(Routes.ADD_LEAVE_UPDATE_REQUEST)
    @SecurityCheck(permissions = {"LEAVE_AUTHORIZE","LEAVE_APPROVE"})
    public String index(@Valid LeaveUpdateRequest request, BindingResult result, Model model) {
        LeaveUpdateRequest updateRequest;

        try {
            if (!result.hasErrors()) {
                updateRequest = service.create(request);
                return "redirect:" + Routes.MODIFY_LEAVE_UPDATE_REQUEST + "?id=" + updateRequest.getId() + "&action=" + request.getAction() + "&result=" + String.valueOf(!result.hasErrors()).toUpperCase();
            }
        } catch (Exception e) {
            SubmitResult.error(messageSource, "leave.apply.error", model);
        }

        referenceData(model);
        model.addAttribute("request", request);
        model.addAttribute("existsRequestUrl", Routes.EXISTS_UPDATE_REQUEST_BY_LEAVE);
        return "leave/leave-update-request-form";
    }

    @GetMapping(Routes.MODIFY_LEAVE_UPDATE_REQUEST)
    @SecurityCheck(permissions = {"LEAVE_AUTHORIZE","LEAVE_APPROVE"})
    public String update(@RequestParam(name = "id", defaultValue = "0") Long id, @RequestParam(name = "action", required = false) String action,
                         @RequestParam(name = "result", required = false) boolean result, Model model) {
        LeaveUpdateRequest request = new LeaveUpdateRequest();
        if (id > 0) {
            request = service.getById(id);
            LeaveApplication application = leaveService.getById(request.getApplicationId());
            List<LeaveDetailsOld> oldList = service.getAllLeaveDetailsOldByRequest(request.getId());

            request.setOldDetails(oldList);
            request.setApplication(application);
            model.addAttribute("leaveNote", leaveService.getEmployeesLeaveBalanceMessage(application.getApplicant().getId()));
        }

        if (result) {
            if (action.equals("request")) {
                SubmitResult.success(messageSource, "leave.apply.success", model);
            }
            if (action.equals("update")) {
                SubmitResult.success(messageSource, "leave.update.success", model);
            }
            if (action.equals("authorize")) {
                SubmitResult.success(messageSource, "leave.authorize.success", model);
            }
            if (action.equals("approve")) {
                SubmitResult.success(messageSource, "leave.approve.success", model);
            }
            if (action.equals("reject")) {
                SubmitResult.success(messageSource, "leave.confirm.success", model);
            }
        }

        referenceData(model);
        model.addAttribute("request", request);
        model.addAttribute("existsRequestUrl", Routes.EXISTS_UPDATE_REQUEST_BY_LEAVE);
        return "leave/leave-update-request-form";
    }

    @PostMapping(Routes.MODIFY_LEAVE_UPDATE_REQUEST)
    @SecurityCheck(permissions = {"LEAVE_AUTHORIZE","LEAVE_APPROVE"})
    public String update(@Valid LeaveUpdateRequest request, BindingResult result, Model model) {
        LeaveUpdateRequest updateRequest;
        String[] actionArr = request.getAction().split(",");
        request.setAction(actionArr.length > 1 ? actionArr[actionArr.length - 1] : actionArr[0]);

        try {
            if (!result.hasErrors()) {
                updateRequest = service.create(request);
                return "redirect:" + Routes.MODIFY_LEAVE_UPDATE_REQUEST + "?id=" + updateRequest.getId() + "&action=" + request.getAction() + "&result=" + String.valueOf(!result.hasErrors()).toUpperCase();
            }
        } catch (Exception e) {
            SubmitResult.error(messageSource, "leave.apply.error", model);
        }

        referenceData(model);
        model.addAttribute("request", request);
        model.addAttribute("existsRequestUrl", Routes.EXISTS_UPDATE_REQUEST_BY_LEAVE);
        return "leave/leave-update-request-form";
    }

    @GetMapping(Routes.LEAVE_UPDATE_REQUESTS)
    public String leaveList(Model model) {
        model.addAttribute("entryUrl", Routes.MODIFY_LEAVE_UPDATE_REQUEST);
        model.addAttribute("searchUrl", Routes.LEAVE_UPDATE_REQUEST_SEARCH);
        return "leave/leave-update-request-list";
    }

    @GetMapping(Routes.LEAVE_UPDATE_REQUEST_SEARCH)
    @ResponseBody
    public Page<LeaveUpdateRequestDto> search(LeaveUpdateRequestSearch search) {
        return service.search(search);
    }

    private void referenceData(Model model) {
        model.addAttribute("leaveTypes", LeaveType.getLeaveTypes(null));
        model.addAttribute("hasAuthorizationPermission", authorizer.hasPermission(Permissions.LEAVE_AUTHORIZE.name()));
        model.addAttribute("hasApprovePermission", authorizer.hasPermission(Permissions.LEAVE_APPROVE.name()));
        model.addAttribute("hasConfirmPermission", authorizer.hasPermission(Permissions.LEAVE_CONFIRM.name()));
    }
}
