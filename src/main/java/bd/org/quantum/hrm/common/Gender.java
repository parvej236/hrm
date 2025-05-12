package bd.org.quantum.hrm.common;

import java.util.LinkedHashMap;
import java.util.Map;

public enum Gender {
    MALE("Male"),
    FEMALE("Female"),
    OTHERS("Others");

    private String name;

    Gender(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Map<String, String> getAllGender () {
        Map<String, String> genderMap = new LinkedHashMap<>();
        for (Gender gender : Gender.values()) {
            genderMap.put(gender.getName(), gender.getName());
        }
        return genderMap;
    }
}
