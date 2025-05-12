package bd.org.quantum.hrm.common;

import bd.org.quantum.authorizer.helper.UserContext;
import bd.org.quantum.authorizer.model.UserDetails;
import bd.org.quantum.hrm.employee.EmployeeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class CommonController {

    private final CommonService commonService;
    private final EmployeeService employeeService;

    public CommonController(CommonService commonService,
                            EmployeeService employeeService) {
        this.commonService = commonService;
        this.employeeService = employeeService;
    }

    @GetMapping("/")
    public String index(Model model) {
        UserDetails userDetails = UserContext.getPrincipal().getUserDetails();
        Long department = userDetails.getDepartmentId() == null ? 0L : userDetails.getDepartmentId();
        model.addAttribute("designationInfo", employeeService.getDesignationStatus(userDetails.getBranchId(), department));
        return "home";
    }

    @GetMapping(Routes.API_MEMBERS_BY_REG_ID_OR_PHONE)
    @ResponseBody
    public List<MemberDto> getMemberByCode(@RequestParam String regIdOrPhone) {
        return commonService.getMemberList(regIdOrPhone);
    }

    @GetMapping(Routes.DEPARTMENT_BY_BRANCH)
    @ResponseBody
    ResponseEntity getDepartmentsByBranch(@PathVariable Long branchId) {
        List<Department> list = employeeService.getDepartmentList(branchId);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping(Routes.API_USERS_BY_USERNAME_NAME_REG_OR_PHONE)
    @ResponseBody
    public List<UserDto> getUserByUserNameRegOrPhone(@RequestParam String userNameRegOrPhone) {
        return commonService.getUsersByName(userNameRegOrPhone.trim());
    }
}