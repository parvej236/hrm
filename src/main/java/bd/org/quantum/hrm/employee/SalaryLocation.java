package bd.org.quantum.hrm.employee;

import java.util.LinkedHashMap;
import java.util.Map;

public enum SalaryLocation {
    KENDRO("Kendro"),
    LOCAL("Local");

    private final String name;

    SalaryLocation(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Map<String, String> getSalaryLocationMap() {
        Map<String, String> map = new LinkedHashMap<>();
        for (SalaryLocation location : SalaryLocation.values()) {
            map.put(location.name(), location.getName());
        }
        return map;
    }
}
