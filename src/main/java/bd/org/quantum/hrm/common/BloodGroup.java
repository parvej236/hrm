package bd.org.quantum.hrm.common;

import java.util.LinkedHashMap;
import java.util.Map;

public enum BloodGroup {
    A_POSITIVE("A Positive"),
    B_POSITIVE("B Positive"),
    O_POSITIVE("O Positive"),
    AB_POSITIVE("AB Positive"),
    A_NEGATIVE("A Negative"),
    B_NEGATIVE("B Negative"),
    O_NEGATIVE("O Negative"),
    AB_NEGATIVE("AB Negative");

    private String name;

    BloodGroup(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Map<String, String> getAllBloodGroup() {
        Map<String, String> bloodGroupMap = new LinkedHashMap<>();
        for (BloodGroup bg : BloodGroup.values()) {
            bloodGroupMap.put(bg.getName(), bg.getName());
        }
        return bloodGroupMap;
    }
}
