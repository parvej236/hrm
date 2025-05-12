package bd.org.quantum.hrm.role;

import bd.org.quantum.authorizer.helper.AbstractResourcePermissionController;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class RoleController extends AbstractResourcePermissionController {
    @Override
    protected Map<String, String> getResourcePermissionMap() {
        Map<String, String> map = new LinkedHashMap<>();
        for (Permissions p : Permissions.values()) {
            map.put(p.name(), p.getTitle());
        }
        return map;
    }

    @Override
    protected Map<String, String> getResources() {
        Map<String, String> map = new LinkedHashMap<>();
        for (Modules p : Modules.values()) {
            map.put(p.name(), p.getTitle());
        }

        return map;
    }
}
