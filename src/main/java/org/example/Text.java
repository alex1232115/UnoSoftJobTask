package org.example;

import java.io.*;
import java.util.*;

/*
"79748671113";"";"79448126993""
"79748671113";"";"79448126993"
"79748671113"asd;"";"79448126993"
"79855053897"83100000580443402";"200000133000191"
"8383"200000741652251"
"5";"6"
 */

public class Text {
    private List<List<String>> groups = new ArrayList<>();

    private Set<List<String>> lines = new HashSet<>();

    public void parseText(File file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String str = reader.readLine();
            while (str != null) {
                if (isValidLine(str)) {
                    List<String> curLineWords = Arrays.asList(str.split(";"));
                }
                str = reader.readLine();
            }
        }
        System.out.println(lines);
    }

    private boolean isValidLine(String str) {
        for (String element : str.split(";")) {
            if (!element.matches("\"\\d*\"")) {
                System.out.println(element);
                return false;
            }
        }
        return true;
    }

    public void setGroups() {

    }
}
