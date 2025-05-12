package bd.org.quantum.hrm.common;

import java.util.LinkedHashMap;
import java.util.Map;

public enum Religion {
    ISLAM("Islam"),
    HINDU("Hindu"),
    CHRISTIAN("Christian"),
    BUDDHIST("Buddhist"),
    OTHERS("Others");

    private String name;

    Religion(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Map<String, String> getAllReligion() {
        Map<String, String> religionMap = new LinkedHashMap<>();
        for (Religion religion : Religion.values()) {
            religionMap.put(religion.getName(), religion.getName());
        }
        return religionMap;
    }
}
