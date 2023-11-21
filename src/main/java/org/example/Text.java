package org.example;

import java.io.*;
import java.util.*;

public class Text {

    public Set<String> parseText(File file) throws IOException {
        Set<String> lines = new LinkedHashSet<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String str = reader.readLine();
            while (str != null) {
                if (isValidLine(str)) {
                    lines.add(str);
                }
                str = reader.readLine();
            }
        }
        return lines;
    }

    private boolean isValidLine(String str) {
        for (String element : str.split(";")) {
            if (!element.matches("\"\\d*\"")) {
                return false;
            }
        }
        return true;
    }

    public List<List<String>> setGroups(Set<String> lines) {
        List<List<String>> groups = new ArrayList<>();

        Map<String, Integer> digitGroup = new HashMap<>();
        Map<String, Integer> digitColumn = new HashMap<>();

        for (String line : lines) {
            List<String> curLineWords = Arrays.asList(line.split(";", -1));
            int column = 0;
            boolean match = false;
            int indexCurrentGroup = -1;
            for (String word : curLineWords) {
                if (word.equals("\"\"")) {
                    column++;
                    continue;
                }

                if (digitColumn.containsKey(word)) {
                    if (!match && digitColumn.get(word) == column) {
                        indexCurrentGroup = digitGroup.get(word);
                        groups.get(indexCurrentGroup).add(String.valueOf(curLineWords));
                        match = true;

                    } else if (digitColumn.get(word) == column && digitGroup.get(word) != indexCurrentGroup) { //для объединения групп
                        int oldIndex = digitGroup.get(word);
                        groups.get(indexCurrentGroup).add(String.valueOf(groups.get(oldIndex)).replace("[", "")
                                .replace("]", ""));
                        //из группы достается большая строка, которая может быть склеена из нескольких
                        for (String s : groups.remove(oldIndex)) {
                            for (String string : s.replace("[", "").replace("]", "")
                                    .replace("\"", "").split(",")) {
                                digitGroup.put(string, indexCurrentGroup);
                            }
                        }
                    }
                } else {
                    digitColumn.put(word, column);
                }
                column++;
            }

            if (!match) {
                for (String s : curLineWords) {
                    digitGroup.put(s, groups.size());
                }
                List<String> list = new ArrayList<>();
                list.add(String.valueOf(curLineWords));
                groups.add(list);
            } else {
                for (String s : curLineWords) {
                    digitGroup.put(s, indexCurrentGroup);
                }
            }
        }
        return groups;
    }

    public void printToFile(List<List<String>> groups, String fileName, int goodGroups) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write("количество групп с более чем одним элементом: " + goodGroups);
            writer.newLine();
            groups.sort(new GroupOfLinesComparator());
            int i = 1; //groups
            for (List<String> list : groups) {
                int j = 1; //lines
                writer.write("Группа " + i);
                writer.newLine();
                for (String string : list) {
                    String currentString = "строчка " + j + ": " +
                            string.substring(1, string.length() - 1);
                    writer.write(currentString);
                    writer.newLine();
                    j++;
                }
                i++;
            }
        }
    }

    static class GroupOfLinesComparator implements Comparator<List<String>> {
        @Override
        public int compare(List<String> o1, List<String> o2) {
            return Integer.compare(o2.size(), o1.size());
        }
    }
}