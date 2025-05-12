package bd.org.quantum.hrm.biometric;

import bd.org.quantum.authorizer.helper.UserContext;
import bd.org.quantum.authorizer.model.UserDetails;
import bd.org.quantum.hrm.common.Branch;
import bd.org.quantum.hrm.common.Department;
import bd.org.quantum.hrm.common.Routes;
import bd.org.quantum.hrm.employee.EmployeeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class BiometricController {
    private final BiometricService service;
    private final EmployeeService employeeService;

    public BiometricController(BiometricService service,
                               EmployeeService employeeService) {
        this.service = service;
        this.employeeService = employeeService;
    }

    @GetMapping(Routes.BIO_ATTENDANCES)
    public String index(Model model) {
        BioAttendanceSearchCriteria criteria = new BioAttendanceSearchCriteria();
        processCriteria(criteria, model);
        return "attendance/bio-attendance";
    }

    @PostMapping(Routes.BIO_ATTENDANCES)
    public String index(@Valid BioAttendanceSearchCriteria criteria, Model model) {
        processCriteria(criteria, model);
        return "attendance/bio-attendance";
    }

    private void processCriteria(BioAttendanceSearchCriteria criteria, Model model) {
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

        List<BioAttendanceListCmd> attendanceList = service.getBioAttendances(criteria);

        model.addAttribute("criteria", criteria);
        model.addAttribute("attendance", attendanceList);
        model.addAttribute("branchList", employeeService.getBranches());
        model.addAttribute("departmentList", employeeService.getDepartmentList(user.getBranchId()));
    }
}