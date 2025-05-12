package bd.org.quantum.hrm.common;

import java.util.LinkedHashMap;
import java.util.Map;

public enum MaritalStatus {
    UNMARRIED("Unmarried"),
    MARRIED("Married"),
    WIDOW("Widow"),
    WIDOWER("Widower"),
    SINGLE("Single"),
    OTHER("Other");

    private String name;

    MaritalStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Map<String, String> getAllMaritalStatus() {
        Map<String, String> maritalStatusMap = new LinkedHashMap<>();
        for (MaritalStatus status : MaritalStatus.values()) {
            maritalStatusMap.put(status.getName(), status.getName());
        }
        return maritalStatusMap;
    }
}
