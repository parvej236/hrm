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
public class PunishmentInfoController {
    private final PunishmentInfoService service;

    public PunishmentInfoController(PunishmentInfoService service) {
        this.service = service;
    }

    @PostMapping(value = Routes.ADD_PUNISHMENT_INFO)
    @SecurityCheck(permissions = {"EMPLOYEE_UPDATE"})
    @ResponseBody
    public ResponseEntity<Map<String, Object>> create(@Valid @NotNull @RequestBody PunishmentInfo punishmentInfo, BindingResult result) {
        Map<String, Object> response = new LinkedHashMap<>();
        try {
            if(!result.hasErrors()) {
                service.create(punishmentInfo);
                log.debug("Punishment info created successfully");
                response.put("status", "Success");
            } else {
                log.debug("Punishment info created error due to binding error");
                response.put("status", "Failed");
            }
        } catch (Exception e) {
            log.debug("Punishment info created exception..." + e);
            response.put("status", "Failed");
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = Routes.DELETE_PUNISHMENT_INFO)
    @ResponseBody
    public void delete(@RequestParam Long id) {
        service.delete(id);
    }

    @GetMapping(value = Routes.EMPLOYEES_PUNISHMENT_INFO)
    @SecurityCheck(permissions = {"EMPLOYEE_UPDATE"})
    @ResponseBody
    public ResponseEntity<List<PunishmentInfo>> list(@RequestParam Long employeeId) {
        List<PunishmentInfo> punishmentList = service.getPunishmentInfoByEmployee(employeeId);
        return new ResponseEntity<>(punishmentList, HttpStatus.OK);
    }
}
