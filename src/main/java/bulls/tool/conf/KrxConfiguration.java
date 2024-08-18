package bulls.tool.conf;


import bulls.exception.ConfigurationException;

import java.util.*;

public class KrxConfiguration extends Configuration {


    public static String newline = System.getProperty("line.separator");

    public Map<String, ArrayList<Map<String, String>>> structMap = new HashMap<>();


    public static String configurationFileName;

    public KrxConfiguration(String configurationFileName) throws ConfigurationException {
        super(configurationFileName);
        // TODO Auto-generated constructor stub
        KrxConfiguration.configurationFileName = configurationFileName;

        String structList = getString("DECLARE_STRUCTURE");

        if (structList == null)
            return;

        String[] structFrag = structList.split(",");


        for (String frag : structFrag) {

            structMap.put(frag, parseStructure(frag));
        }


    }


    public int getStructSize(String struct) {
        return structMap.get(struct).size();
    }


    public ArrayList<Map<String, String>> parseStructure(String frag) {
        Enumeration<String> keys = keys();
        int maxNumericConfIndex = -1;

        Map<Integer, String> structElements = new HashMap();
        int counter = 0;

        while (keys.hasMoreElements()) {

            String key = keys.nextElement();
            String[] keyFragments = key.split("\\.");


            if (keyFragments != null && keyFragments.length > 2 && keyFragments[0].equals(frag)) {
                if (!structElements.containsValue(keyFragments[2])) {
                    structElements.put(counter, keyFragments[2]);
                    counter++;
                }
                int numericIndex = Integer.parseInt(keyFragments[1]);
                if (maxNumericConfIndex < numericIndex) {
                    maxNumericConfIndex = numericIndex;
                }
            }

        }


        int elemCount = structElements.size();
        ArrayList<Map<String, String>> struct = new ArrayList<Map<String, String>>(elemCount);


        this.structMap.put(frag, struct);
        // assume 0-based index


        for (int i = 0; i < maxNumericConfIndex + 1; i++) {

            Map<String, String> element = new HashMap();
            for (int j = 0; j < elemCount; j++) {
                String name = structElements.get(j);
                String s = getString(frag + "." + i + "." + name);
                element.put(name, s);

            }
            struct.add(element);
        }


        return struct;
    }

    private String getKey(String s, String delim) {
        int splitPoint = s.indexOf(delim);
        return s.substring(0, splitPoint);
    }

    private String getValue(String s, String delim) {
        int splitPoint = s.indexOf(delim);
        return s.substring(splitPoint + 1);
    }


    public String getStructString(String structName, int structNumber, String elemName) {
        return structMap.get(structName).get(structNumber).get(elemName);
    }

    public String getStructString(String structName, int structNumber, String elemName, String defaultValue) {
        String value = structMap.get(structName).get(structNumber).get(elemName);
        if (value == null)
            return defaultValue;
        return value;
    }

    public String[] getStructStringArray(String structName, int structNumber, String elemName, String delim) {
        String list = structMap.get(structName).get(structNumber).get(elemName);
        if (list == null)
            return null;
        return list.split(delim);
    }

    public List<String> getStructStringList(String structName, int structNumber, String elemName, String delim) {
        List<String> list = new ArrayList<>();
        String str = structMap.get(structName).get(structNumber).get(elemName);
        if (str == null)
            return list;
        str = str.trim();
        if (str.isEmpty())
            return list;
        String[] arr = str.split(delim);
        Collections.addAll(list, arr);
        return list;
    }

    public Integer getStructInt(String structName, int structNumber, String elemName) {
        return Integer.parseInt(structMap.get(structName).get(structNumber).get(elemName));
    }

    public Integer getStructInt(String structName, int structNumber, String elemName, int defaultValue) {

        String v = structMap.get(structName).get(structNumber).get(elemName);
        if (v == null)
            return defaultValue;
        else
            return Integer.parseInt(v);

    }

    public Boolean getStructBoolean(String structName, int structNumber, String elemName) {
        return Boolean.parseBoolean(structMap.get(structName).get(structNumber).get(elemName));
    }

    public Boolean getStructBoolean(String structName, int structNumber, String elemName, boolean defaultValue) {
        String str = structMap.get(structName).get(structNumber).get(elemName);
        if (str == null)
            return defaultValue;
        return Boolean.parseBoolean(str);
    }

    public Double getStructDouble(String structName, int structNumber, String elemName) {
        return Double.parseDouble(structMap.get(structName).get(structNumber).get(elemName));
    }

    public Long getStructLong(String structName, int structNumber, String elemName, long defaultValue) {
        String value = structMap.get(structName).get(structNumber).get(elemName);
        if (value == null)
            return defaultValue;

        return Long.parseLong(value);
    }

    @SuppressWarnings("unchecked")
    public Enumeration<String> keys() {
        return (Enumeration<String>) properties.propertyNames();
    }

    public String[] getStringList(String key, String delimeter) {
        String list = getString(key);
        if (list == null)
            return null;
        String[] arr = list.split(delimeter);
        for (int i = 0; i < arr.length; ++i) {
            arr[i] = arr[i].trim();
        }
        return arr;
    }

    public Integer[] getIntegerList(String key, String delimeter) {
        String list = getString(key);
        if (list == null)
            return null;

        String[] stringList = list.split(delimeter);

        Integer[] r = new Integer[stringList.length];
        for (int i = 0; i < stringList.length; i++) {
            r[i] = Integer.parseInt(stringList[i]);
        }

        return r;
    }
}
