package bd.org.quantum.hrm.common;

import bd.org.quantum.common.resttemplate.RestTemplateService;
import bd.org.quantum.common.utils.UrlUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class CommonService {
    public static final String MEMBER_BY_REG_ID_OR_PHONE = "%s/api/get-member-information-by-reg-code-phone-or-uniq-id?regIdOrPhone=%s";
    public static final String RELATION_LIST = "/api/relations/%s";
    private static final String BRANCH_LIST = "%s/api/branches/%s";
    private static final String DEPARTMENT_WITH_SUBSIDIARIES = "%s/api/departments/{deftId}/subsidiaries";
    public static final String USER_BY_NAME_REG_OR_PHONE = "/api/user-info-by-name-reg-or-phone";

    @Value("${member.api.url}")
    private String memberApiUrl;

    @Value("${resource.api.url}")
    String resourceApiUrl;

    @Value("${user.api.url}")
    private String userApiUrl;

    private final RestTemplateService restService;

    public CommonService(RestTemplateService restService) {
        this.restService = restService;
    }

    public List<MemberDto> getMemberList(String regIdOrPhone) {
        List<MemberDto> memberList = new ArrayList<>();

        final String uri = String.format(MEMBER_BY_REG_ID_OR_PHONE, memberApiUrl, regIdOrPhone);
        try {
            memberList = restService.getForList(uri, new ParameterizedTypeReference<>() {});
        } catch (Exception e) {
            log.debug("Error: ", e);
        }

        return memberList;
    }

    public Map<String, String> getRelations(String type) {
        final String uri = UrlUtils.getUrl(resourceApiUrl, String.format(RELATION_LIST, type));
        return restService.getForObject(uri, Map.class);
    }

    public List<Branch> getBranches(Long branchId) {
        final String uri = String.format(BRANCH_LIST, resourceApiUrl, branchId);
        return restService.getForList(uri, new ParameterizedTypeReference<>() {});
    }

    public List<Department> getDepartmentsSubsidiaries(long deftId) {
        final String uri = String.format(DEPARTMENT_WITH_SUBSIDIARIES, resourceApiUrl);
        return restService.getForList(uri, new ParameterizedTypeReference<>(){}, deftId);
    }

    public List<UserDto> getUsersByName(String userNameRegOrPhone) {
        List<UserDto> userList = new ArrayList<>();

        final String uri = UrlUtils.getUrl(userApiUrl, USER_BY_NAME_REG_OR_PHONE + "?userNameRegOrPhone=" + userNameRegOrPhone);
        try {
            userList = restService.getForList(uri, new ParameterizedTypeReference<>() {});
        } catch (Exception e) {
            log.debug("Error: ", e);
        }

        return userList;
    }
}
