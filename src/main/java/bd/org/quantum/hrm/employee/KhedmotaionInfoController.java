package bd.org.quantum.hrm.employee;

import bd.org.quantum.authorizer.helper.SecurityCheck;
import bd.org.quantum.hrm.common.Routes;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.rule.Mode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
public class KhedmotaionInfoController {
    private final KhedmotaionInfoService service;

    public KhedmotaionInfoController(KhedmotaionInfoService service) {
        this.service = service;
    }

    @PostMapping(value = Routes.ADD_KHEDMOTAION_INFO)
    @SecurityCheck(permissions = {"EMPLOYEE_UPDATE"})
    @ResponseBody
    public ResponseEntity<Map<String, Object>> create(@Valid @NotNull @RequestBody KhedmotaionInfo khedmotaionInfo, BindingResult result) {
        Map<String, Object> response = new LinkedHashMap<>();
        try {
            if(!result.hasErrors()) {
                service.create(khedmotaionInfo);
                log.debug("Khedmotaion info created successfully");
                response.put("status", "Success");
            } else {
                log.debug("Khedmotaion info created error due to binding error");
                response.put("status", "Failed");
            }
        } catch (Exception e) {
            log.debug("Khedmotaion info created exception..." + e);
            response.put("status", "Failed");
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = Routes.GET_KHEDMOTAION_INFO)
    @ResponseBody
    public KhedmotaionInfo get(@RequestParam Long id) {
        return service.getById(id);
    }

    @PostMapping(value = Routes.DELETE_KHEDMOTAION_INFO)
    @ResponseBody
    public void delete(@RequestParam Long id) {
        service.delete(id);
    }

    @GetMapping(value = Routes.EMPLOYEES_KHEDMOTAION_INFO)
    @SecurityCheck(permissions = {"EMPLOYEE_UPDATE"})
    @ResponseBody
    public ResponseEntity<List<KhedmotaionInfo>> list(@RequestParam Long employeeId) {
        List<KhedmotaionInfo> khedmotaionList = service.getKhedmotaionInfoByEmployee(employeeId);
        return new ResponseEntity<>(khedmotaionList, HttpStatus.OK);
    }
}
