package bd.org.quantum.hrm.yr_cl;

import bd.org.quantum.authorizer.helper.SecurityCheck;
import bd.org.quantum.common.utils.SearchForm;
import bd.org.quantum.common.utils.SubmitResult;
import bd.org.quantum.hrm.common.Routes;
import lombok.extern.slf4j.Slf4j;
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
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Controller
public class YrClController {

    private final YrClService service;
    private final MessageSource messageSource;

    public YrClController(YrClService service,
                          MessageSource messageSource) {
        this.service = service;
        this.messageSource = messageSource;
    }

    @GetMapping(Routes.YR_CLS)
    public String search(Model model) {
        model.addAttribute("formTitle", "Year wise Yr List");
        model.addAttribute("searchUrl", Routes.YR_CL_SEARCH);
        model.addAttribute("editUrl", Routes.YR_CL_UPDATE);
        model.addAttribute("entryUrl", Routes.YR_CL_ENTRY);
        return "yr_cl/yr-list";
    }

    @GetMapping(Routes.YR_CL_SEARCH)
    @ResponseBody
    public Page<YrCl> YrClList(SearchForm searchForm) {
        return service.search(searchForm);
    }

    @GetMapping(Routes.YR_CL_ENTRY)
    public String create(Model model) {
        model.addAttribute("yrcl", new YrCl());
        model.addAttribute("formTitle", "Year Wise Yr Entry");
        model.addAttribute("checkUrl", Routes.YR_CL_CHECK);
        model.addAttribute("entryLink", Routes.YR_CL_ENTRY);
        return "yr_cl/yr-form";
    }

    @GetMapping(Routes.YR_CL_UPDATE)
    @SecurityCheck(permissions = {"YR_CL"})
    public String update(@RequestParam Long id, @RequestParam(required = false) boolean result,
                         @RequestParam(defaultValue = "2") int flag, Model model) {

        model.addAttribute("yrcl", service.getYrClById(id));
        model.addAttribute("checkUrl", Routes.YR_CL_CHECK);
        model.addAttribute("entryLink", Routes.YR_CL_ENTRY);
        model.addAttribute("formTitle", "Year Wise YR Update");
        if (result && flag == 1) {
            SubmitResult.success(messageSource, "yrcl.update.success", model);
        } else if (flag != 2){
            SubmitResult.success(messageSource, "yrcl.create.success", model);
        }
        return "yr_cl/yr-form";
    }

    @GetMapping(Routes.YR_CL_CHECK)
    @ResponseBody
    public Map<String, Object> checkExistingYrCl(@RequestParam Integer year) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("data", service.checkExistingYrCl(year));
        return result;
    }

    @PostMapping(Routes.YR_CL_ENTRY)
    public String create(@Valid YrCl yrclForm, BindingResult result, Model model) {
        service.validateYrCl(yrclForm, result);
        try {
            if (!result.hasErrors()) {
                int flag = yrclForm.getId() == null || yrclForm.getId() == 0 ? 0 : 1;
                yrclForm = service.create(yrclForm);
                model.addAttribute("yrcl", yrclForm);
                return "redirect:" + Routes.YR_CL_UPDATE + "?id=" + yrclForm.getId() + "&flag=" + flag + "&result=" + String.valueOf(!result.hasErrors()).toUpperCase();
            } else {
                SubmitResult.error(messageSource, "yrcl.create.error", model);
            }
        } catch (Exception e) {
            SubmitResult.error(messageSource, "yrcl.create.error", model);
        }
        return "redirect:/yr-cl-entry";
    }
}
