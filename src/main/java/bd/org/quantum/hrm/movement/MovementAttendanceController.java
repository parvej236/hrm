package bd.org.quantum.hrm.movement;

import bd.org.quantum.authorizer.Authorizer;
import bd.org.quantum.authorizer.helper.UserContext;
import bd.org.quantum.authorizer.model.UserDetails;
import bd.org.quantum.common.utils.SubmitResult;
import bd.org.quantum.hrm.common.Branch;
import bd.org.quantum.hrm.common.Department;
import bd.org.quantum.hrm.common.Routes;
import bd.org.quantum.hrm.designation.Designation;
import bd.org.quantum.hrm.employee.Employee;
import bd.org.quantum.hrm.employee.EmployeeService;
import bd.org.quantum.hrm.role.Permissions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
public class MovementAttendanceController {

    private final MovementAttendanceService service;
    private final EmployeeService employeeService;
    private final MessageSource messageSource;
    private final Authorizer authorizer;

    public MovementAttendanceController(MovementAttendanceService service,
                                        EmployeeService employeeService,
                                        MessageSource messageSource,
                                        Authorizer authorizer) {
        this.service = service;
        this.employeeService = employeeService;
        this.messageSource = messageSource;
        this.authorizer = authorizer;
    }

    @GetMapping(Routes.MOVEMENTS)
    public String index(Model model) {
        MovementSearchCriteria criteria = new MovementSearchCriteria();
        criteria.setStage("PENDING");
        processCriteria(criteria, model);
        return "attendance/movements";
    }

    @PostMapping(Routes.MOVEMENTS)
    public String index(@Valid MovementSearchCriteria criteria, Model model) {
        processCriteria(criteria, model);
        return "attendance/movements";
    }

    @GetMapping(Routes.ADD_MOVEMENT)
    public String entry(Model model) {
        MovementAttendance movement = new MovementAttendance();
        Employee employee = new Employee();
        employee.setDesignation(new Designation());
        movement.setEmployee(employee);

        model.addAttribute("movement", movement);
        referenceData(model);
        return "attendance/movement-attendance-form";
    }

    @PostMapping(Routes.ADD_MOVEMENT)
    public String entry(@Valid MovementAttendance movementForm, BindingResult result, Model model) {
        movementForm.setStage("PENDING");
        service.validateMovement(movementForm, result);
        try {
            if (!result.hasErrors()) {
                MovementAttendance movement = service.create(movementForm);
                return "redirect:" + Routes.UPDATE_MOVEMENT + "?id=" + movement.getId() + "&result=" + String.valueOf(!result.hasErrors()).toUpperCase();
            } else {
                movementForm.getEmployee().setDesignation(new Designation());
                SubmitResult.error(messageSource, "movement.apply.error", model);
            }
        } catch (Exception e) {
            SubmitResult.error(messageSource, "movement.apply.error", model);
        }

        model.addAttribute("movement", movementForm);
        referenceData(model);
        return "attendance/movement-attendance-form";
    }

    @GetMapping(Routes.UPDATE_MOVEMENT)
    public String get(@RequestParam Long id, @RequestParam(defaultValue = "") String result, Model model) {
        MovementAttendance movement = service.getById(id);

        if (result.equals("TRUE")) {
            SubmitResult.success(messageSource, "movement.apply.success", model);
        } else if(result.equals("FALSE")) {
            SubmitResult.error(messageSource, "movement.apply.error", model);
        }

        referenceData(model);
        model.addAttribute("movement", movement);
        return "attendance/movement-attendance-form";
    }

    @PostMapping(Routes.UPDATE_MOVEMENT)
    public String update(@Valid MovementAttendance movement, BindingResult result, Model model) {
        service.validateMovement(movement, result);
        try {
            if (!result.hasErrors()) {
                if (movement.getAction().equals("submit")) {
                    movement.setStage("AUTHORIZE");
                }
                movement = service.create(movement);
                return "redirect:"+ Routes.UPDATE_MOVEMENT+"?id="+movement.getId()+"&result="+String.valueOf(!result.hasErrors()).toUpperCase();
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }

        referenceData(model);
        model.addAttribute("movement", movement);
        return "attendance/movement-attendance-form";
    }

    private void referenceData(Model model) {
        model.addAttribute("hasAuthorizationPermission", authorizer.hasPermission(Permissions.LEAVE_AUTHORIZE.name()));
    }

    private void processCriteria(MovementSearchCriteria criteria, Model model) {
        UserDetails user = UserContext.getPrincipal().getUserDetails();

        if (criteria.getBranch() != null && criteria.getBranch() > 0) {
            criteria.setBranches(Collections.singletonList(criteria.getBranch()));
        } else {
            criteria.setBranches(employeeService.getBranches().stream().map(Branch::getId).collect(Collectors.toList()));
        }

        if (criteria.getDepartment() != null && criteria.getDepartment() > 0) {
            criteria.setDepartments(Collections.singletonList(criteria.getDepartment()));
        } else {
            criteria.setDepartments(employeeService.getDepartmentList(user.getBranchId()).stream().map(Department::getId).collect(Collectors.toList()));
        }

        List<MovementSearchCriteria> movementList = service.getMovementList(criteria);

        model.addAttribute("criteria", criteria);
        model.addAttribute("movementList", movementList);
        model.addAttribute("updateUrl", Routes.UPDATE_MOVEMENT + "?id=");
        model.addAttribute("branchList", employeeService.getBranches());
        model.addAttribute("departmentList", employeeService.getDepartmentList(user.getBranchId()));
    }
}
