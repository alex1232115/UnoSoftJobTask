package org.example;

import java.io.*;
import java.util.*;

public class Parser {
    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        System.out.println("Запуск парсера");

        String fileName = args[0];
        File file = new File(fileName);
        List<String[]> lines = parseFromFile(file);
        List<List<String[]>> groups = divisionIntoGroups(lines);
        int goodGroups = countGoodGroups(groups);
        writeToFile(groups, goodGroups);

        long end = System.currentTimeMillis();
        System.out.printf("Время работы программы: %d миллисекунд", (end - start));
    }

    private static int countGoodGroups(List<List<String[]>> groups) {
        int goodGroups = 0;
        for (List<String[]> list : groups) {
            if (list.size() > 1) {
                goodGroups++;
            }
        }
        System.out.println("Количество групп с более чем одним элементом: " + goodGroups);
        return goodGroups;
    }

    private static void writeToFile(List<List<String[]>> groups, int goodGroups) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("result.txt"))) {
            writer.write("количество групп с более чем одним элементом: " + goodGroups);
            writer.newLine();
            groups.sort((o1, o2) -> Integer.compare(o2.size(), o1.size()));
            int i = 1; //groups
            for (List<String[]> group : groups) {
                int j = 1; //lines
                writer.write("Группа " + i);
                writer.newLine();
                for (String[] line : group) {
                    StringBuilder current = new StringBuilder("строчка " + j + ": ");
                    for (String word : line) {
                        current.append("\"").append(word).append("\";");
                    }
                    writer.write(String.valueOf(current));
                    writer.newLine();
                    j++;
                }
                i++;
            }
        }
    }

    private static List<List<String[]>> divisionIntoGroups(List<String[]> lines) {

        Map<Integer, List<String[]>> groups = new HashMap<>(); // номер группы: содержимое группы (строки)

        Map<String, Map<Integer, Integer>> wordGroupColumn = new HashMap<>(); //слово: номер стобца: группа

        int groupIndex = 0; // уникальный индекс с каждой группой

        for (String[] line : lines) {
            int colNum = 0; // номер столбца
            Set<Integer> groupsThatMatch = new HashSet<>();//список с номера групп, которые совпали по столбцам
            int blankWords = 0;
            for (String word : line) {
                if (word.equals("")) {
                    blankWords++;
                    colNum++;
                    continue;
                }

                if (wordGroupColumn.containsKey(word)) {
                    if (wordGroupColumn.get(word).containsKey(colNum)) {
                        // добавление строки в группу
                        int numberOfGroup = wordGroupColumn.get(word).get(colNum);
                        groupsThatMatch.add(numberOfGroup);

                    } else {
                        wordGroupColumn.get(word).put(colNum, groupIndex);
                    }
                } else {
                    // Добавление номера группы и столбца для текущего слова
                    Map<Integer, Integer> map = new HashMap<>();
                    map.put(colNum, groupIndex);
                    wordGroupColumn.put(word, map);
                }
                colNum++;
            }
            if (line.length == blankWords) {
                continue;
            }

            // мердж совпадающих групп или создание одной новой
            if (groupsThatMatch.size() == 0) {
                //создание новой группы
                List<String[]> list = new ArrayList<>();
                list.add(line);
                groups.put(groupIndex, list);
            } else {
                //мердж совпадающих групп
                List<Integer> groupsMatch = new ArrayList<>(groupsThatMatch);
                mergeLinesInGroup(line, groupsMatch, groups, wordGroupColumn);
            }

            groupIndex++;
        }

        //преобразование сета в лист
        List<List<String[]>> result = new ArrayList<>();
        for (Map.Entry<Integer, List<String[]>> group : groups.entrySet()) {
            result.add(group.getValue());
        }
        return result;
    }

    private static void mergeLinesInGroup(String[] curLine, List<Integer> groupsThatMatch, Map<Integer, List<String[]>> groups, Map<String, Map<Integer, Integer>> wordGroupColumn) {
        int mainGroup = groupsThatMatch.get(0); // индекс группы, в которую буду мерджить остальные строки
        List<String[]> newGroup = groups.get(mainGroup);
        newGroup.add(curLine);

        for (int i = 0; i < curLine.length; i++) {
            if (!curLine[i].equals("")) {
                wordGroupColumn.get(curLine[i]).put(i, mainGroup);
            }
        }

        for (int i = 1; i < groupsThatMatch.size(); i++) {
            int indexMergedGroup = groupsThatMatch.get(i);
            groups.get(indexMergedGroup);
            newGroup.addAll(groups.get(indexMergedGroup));
            //меняю группу в мапе для каждого слова
            for (String[] line : groups.get(indexMergedGroup)) {
                for (int j = 0; j < line.length; j++) {
                    if (!line[j].equals("")) {
                        wordGroupColumn.get(line[j]).put(j, mainGroup);
                    }
                }
            }
            groups.remove(indexMergedGroup);
        }
    }

    private static List<String[]> parseFromFile(File file) throws IOException {
        // использую для хранения значений стрингу
        List<String[]> wordsOnEveryLines = new ArrayList<>();
        Set<String> lines = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String str = reader.readLine();
            while (str != null) {
                if (isValidLine(str) && !lines.contains(str)) {
                    lines.add(str);
                    String[] curLine = str.replace("\"", "").split(";");
                    wordsOnEveryLines.add(curLine);
                }
                str = reader.readLine();
            }
        }
        return wordsOnEveryLines;
    }

    private static boolean isValidLine(String str) {
        for (String element : str.split(";")) {
            if (!element.matches("\"\\d*\"")) {
                return false;
            }
        }
        return true;
    }
}
