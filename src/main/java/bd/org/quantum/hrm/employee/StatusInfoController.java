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
public class StatusInfoController {
    private final StatusInfoService service;

    public StatusInfoController(StatusInfoService service) {
        this.service = service;
    }

    @PostMapping(value = Routes.ADD_STATUS_INFO)
    @SecurityCheck(permissions = {"EMPLOYEE_UPDATE"})
    @ResponseBody
    public ResponseEntity<Map<String, Object>> create(@Valid @NotNull @RequestBody StatusInfo statusInfo, BindingResult result) {
        Map<String, Object> response = new LinkedHashMap<>();
        try {
            if(!result.hasErrors()) {
                service.create(statusInfo);
                log.debug("Status info created successfully");
                response.put("status", "Success");
            } else {
                log.debug("Status info created error due to binding error");
                response.put("status", "Failed");
            }
        } catch (Exception e) {
            log.debug("Status info created exception..." + e);
            response.put("status", "Failed");
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = Routes.GET_STATUS_INFO)
    @ResponseBody
    public StatusInfo get(@RequestParam Long id) {
        return service.getStatusInfoById(id);
    }

    @PostMapping(value = Routes.DELETE_STATUS_INFO)
    @ResponseBody
    public void delete(@RequestParam Long id) {
        service.delete(id);
    }

    @GetMapping(value = Routes.EMPLOYEES_STATUS_INFO)
    @SecurityCheck(permissions = {"EMPLOYEE_UPDATE"})
    @ResponseBody
    public ResponseEntity<List<StatusInfo>> list(@RequestParam Long employeeId) {
        List<StatusInfo> khedmotaionList = service.getStatusInfoByEmployee(employeeId);
        return new ResponseEntity<>(khedmotaionList, HttpStatus.OK);
    }
}
