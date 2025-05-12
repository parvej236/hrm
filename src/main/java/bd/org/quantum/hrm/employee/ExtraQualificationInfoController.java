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
public class ExtraQualificationInfoController {
    private final ExtraQualificationInfoService service;

    public ExtraQualificationInfoController(ExtraQualificationInfoService service) {
        this.service = service;
    }

    @PostMapping(value = Routes.ADD_EXTRA_QUALIFICATION_INFO)
    @SecurityCheck(permissions = {"EMPLOYEE_UPDATE"})
    @ResponseBody
    public ResponseEntity<Map<String, Object>> create(@Valid @NotNull @RequestBody ExtraQualificationInfo extraQualificationInfo, BindingResult result) {
        Map<String, Object> response = new LinkedHashMap<>();
        try {
            if(!result.hasErrors()) {
                service.create(extraQualificationInfo);
                log.debug("Extra Qualification info created successfully");
                response.put("status", "Success");
            } else {
                log.debug("Extra Qualification info created error due to binding error");
                response.put("status", "Failed");
            }
        } catch (Exception e) {
            log.debug("Extra Qualification info created exception..." + e);
            response.put("status", "Failed");
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = Routes.DELETE_EXTRA_QUALIFICATION_INFO)
    @ResponseBody
    public void delete(@RequestParam Long id) {
        service.delete(id);
    }

    @GetMapping(value = Routes.EMPLOYEES_EXTRA_QUALIFICATION_INFO)
    @SecurityCheck(permissions = {"EMPLOYEE_UPDATE"})
    @ResponseBody
    public ResponseEntity<List<ExtraQualificationInfo>> list(@RequestParam Long employeeId) {
        List<ExtraQualificationInfo> qualificationList = service.getExtraQualificationInfoListByEmployee(employeeId);
        return new ResponseEntity<>(qualificationList, HttpStatus.OK);
    }
}
