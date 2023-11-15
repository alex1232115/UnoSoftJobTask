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
    Set<List<String>> lines = new HashSet<>();

    public void parseText(File file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String str = reader.readLine();
            while (str != null) {
                if (isValidLine(str)) {
                    List<String> curLineWords = Arrays.asList((str.replace("\"", "").split(";")));
                    lines.add(curLineWords);
                }
                str = reader.readLine();
            }
        }
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
        List<List<String>> groups = new ArrayList<>();

        Map<String, Integer> digitAndGroup = new HashMap<>();
        Map<String, Integer> digitAndColumn = new HashMap<>();

        int globalGroupIndex = 0;

        for (List<String> curLineWords : lines) {
            int column = 0;
            boolean match = false;

            for (String word : curLineWords) {
                if (word.equals("")) {
                    column++;
                    continue;
                }

                if (digitAndColumn.containsKey(word)) {
                    if (!match && digitAndColumn.get(word) == column) {
                        int localGroupIndex = digitAndGroup.get(word);
                        groups.get(localGroupIndex).add(String.valueOf(curLineWords));
                        for (String s : curLineWords) {
                            digitAndGroup.put(s, localGroupIndex);
                        }
                        match = true;
                    }
                } else {
                    digitAndColumn.put(word, column);
                }

                column++;
            }

            if (!match) {
                for (String s : curLineWords) {
                    digitAndGroup.put(s, globalGroupIndex);
                }
                List<String> list = new ArrayList<>();
                list.add(String.valueOf(curLineWords));
                groups.add(globalGroupIndex, list);
                globalGroupIndex++;
            }
        }
        System.out.println(groups);
    }
}

