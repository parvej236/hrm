package bd.org.quantum.hrm.common;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Utils {
    public static Map<String, String> getJsonStringToMap(String json){
        return getJsonStringToMap(json, "#", getMemberParamMap());
    }

    public static Map<String, String> getMemberParamMap(){
        Map<String, String> map = new LinkedHashMap<String, String>();

        map.put("{","");
        map.put("}","");
        map.put(",id",",\"id");
        map.put(",sms",",\"sms");
        map.put(",mail",",\"mail");
        map.put(",\",","\",");
        map.put(",\"", "#\"");
        map.put("\":", "\"#");

        return map;
    }

    public static Map<String, String> getJsonStringToMap(String json, String delimiter, Map<String, String> paramMap){
        Map<String, String> map = new LinkedHashMap<String, String>();

        // this method has been used for cleaning json data
        json = prepareContent(json, paramMap);

        List<String> list = Arrays.asList(json.split(delimiter));
        Iterator<String> it = list.iterator();

        while(it.hasNext()) {
            map.put(removeDoubleQuote(it.next()), removeDoubleQuote(it.next()));
        }

        return map;
    }

    public static String prepareContent(String content, Map<String, String> paramMap){
        for(Map.Entry entry: paramMap.entrySet()){
            content = content.replace(entry.getKey().toString(), entry.getValue().toString());
        }
        return content;
    }

    public static String removeDoubleQuote(String inputStr){
        return inputStr.replace("\"",  "");
    }

    public static String splitCamelCase(String s) {
        return s.replaceAll(
                String.format("%s|%s|%s",
                        "(?<=[A-Z])(?=[A-Z][a-z])",
                        "(?<=[^A-Z])(?=[A-Z])",
                        "(?<=[A-Za-z])(?=[^A-Za-z])"
                ),
                " "
        );
    }

    public static String[] getMonthArray(boolean isFull) {
        if (isFull) {
            return new String[]{"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        } else {
            return new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        }
    }

    public static Map<Integer, String> getMonthMap(boolean isFull, boolean withPleaseSelect){
        String[] monthArray = getMonthArray(isFull);
        Map<Integer, String> monthMap = new LinkedHashMap<>();
        if(withPleaseSelect){ monthMap.put(0, "-Please Select"); }
        monthMap.putAll(IntStream.range(0, monthArray.length).boxed().collect(Collectors.toMap(index -> index, index -> monthArray[index])));
        return monthMap;
    }
}