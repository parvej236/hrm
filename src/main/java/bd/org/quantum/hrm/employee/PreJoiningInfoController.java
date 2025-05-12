package bd.org.quantum.hrm.employee;

import bd.org.quantum.authorizer.helper.SecurityCheck;
import bd.org.quantum.hrm.common.Routes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
public class PreJoiningInfoController {
    private final PreJoiningInfoService service;

    public PreJoiningInfoController(PreJoiningInfoService service) {
        this.service = service;
    }

    @PostMapping(value = Routes.ADD_PRE_JOINING_INFO)
    @SecurityCheck(permissions = {"EMPLOYEE_UPDATE"})
    @ResponseBody
    public ResponseEntity<Map<String, Object>> create(@Valid @NotNull @RequestBody PreJoiningInfo preJoiningInfo, BindingResult result) {
        Map<String, Object> response = new LinkedHashMap<>();
        try {
            if(!result.hasErrors()) {
                service.create(preJoiningInfo);
                log.debug("Pre-Joining info created successfully");
                response.put("status", "Success");
            } else {
                log.debug("Pre-Joining info created error due to binding error");
                response.put("status", "Failed");
            }
        } catch (Exception e) {
            log.debug("Pre-Joining info created exception..." + e);
            response.put("status", "Failed");
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = Routes.GET_PRE_JOINING_INFO)
    @ResponseBody
    public PreJoiningInfo get(@RequestParam Long id) {
        return service.getById(id);
    }

    @PostMapping(value = Routes.DELETE_PRE_JOINING_INFO)
    @ResponseBody
    public void delete(@RequestParam Long id) {
        service.delete(id);
    }

    @GetMapping(value = Routes.EMPLOYEES_PRE_JOINING_INFO)
    @SecurityCheck(permissions = {"EMPLOYEE_UPDATE"})
    @ResponseBody
    public ResponseEntity<List<PreJoiningInfo>> list(@RequestParam Long employeeId) {
        List<PreJoiningInfo> prejoinList = service.getPreJoiningInfoByEmployee(employeeId);
        return new ResponseEntity<>(prejoinList, HttpStatus.OK);
    }
}
