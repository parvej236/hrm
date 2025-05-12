package bd.org.quantum.hrm.changeHistory;

import bd.org.quantum.hrm.common.Utils;
import bd.org.quantum.hrm.common.Routes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Slf4j
@Controller
public class ChangeHistoryController {
    private final ChangeHistoryService service;

    public ChangeHistoryController(ChangeHistoryService service) {
        this.service = service;
    }

    @GetMapping(Routes.ALL_CHANGES)
    public String getAllChangeHistory(@RequestParam(value = "dataId") long dataId,
                                      @RequestParam(value = "className") String className, Model model){

        List<ChangeHistory> histories = service.getChangeHistoryByDataIdAndClassName(dataId, className);

        if (histories.isEmpty()) {
            model.addAttribute("error", "Nothing Found To Display!!!");

        } else {
            List<Map> listItems = new ArrayList<>();
            Set columnSet = new LinkedHashSet();

            histories.forEach(history -> {
                Map<String, String> map = Utils.getJsonStringToMap(history.getJsonData());
                map.put("actionTime", history.getActionTime().toString());
                map.put("changeBy", history.getChangedByName());
                listItems.add(map);
                columnSet.add("actionTime");
                columnSet.add("changeBy");
                columnSet.addAll(map.keySet());
            });

            Set headerColumns = new LinkedHashSet();
            columnSet.forEach(col -> {
                headerColumns.add(StringUtils.capitalize(Utils.splitCamelCase(col.toString())));
            });

            model.addAttribute("columns", columnSet);
            model.addAttribute("className", className);
            model.addAttribute("listItems", listItems);
            model.addAttribute("headerColumns", headerColumns);
        }

        return "view/all-change-history";
    }

    @GetMapping(Routes.CHANGES_ONLY)
    @ResponseBody
    public ResponseEntity getChangesOnly(@RequestParam(value = "dataId") int dataId,
                                         @RequestParam(value = "className") String className) {

        List<ChangeHistory> histories = service.getChangeHistoryByDataIdAndClassName(dataId, className);
        return (histories != null && !histories.isEmpty())
                ? ResponseEntity.ok().body(histories)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not Found!");
    }
}
