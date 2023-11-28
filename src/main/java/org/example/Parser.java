package org.example;

import java.io.*;
import java.util.*;

public class Parser {
    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();

//        String fileName = args[0];
        File file = new File("C:\\Users\\alash\\IdeaProjects\\unoSoftTask\\src\\main\\resources\\lng.txt");
        List<String[]> lines = parseFromFile(file);
        List<List<String[]>> groups = divisionIntoGroups(lines);
        int goodGroups = 0;
        for (List<String[]> list : groups) {
            if (list.size() > 1) {

                goodGroups++;

            }
        }
        System.out.println(goodGroups);

        writeToFile(groups, goodGroups);

        long end = System.currentTimeMillis();
        System.out.printf("Время работы программы: %d миллисекунд", (end - start));
    }

    private static void writeToFile(List<List<String[]>> groups, int goodGroups) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("resultParser.txt"))) {
            writer.write("количество групп с более чем одним элементом: " + goodGroups);
            writer.newLine();
            groups.sort((o1, o2) -> Integer.compare(o2.size(), o1.size()));
            int i = 1; //groups
            for (List<String[]> group : groups) {
                int j = 1; //lines
                writer.write("Группа " + i);
                writer.newLine();
                for (String[] line: group) {
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

        // 1 List - номер группы, 2 List - номер строки, String[] - сами строки
        List<List<String[]>> groups = new ArrayList<>();

        Map<String, int[]> wordGroupColumn = new HashMap<>(); //слово - 2(номер группы) 1(номер слолбца)

        for (String[] line : lines) {
            int colNum = 0; // номер столбца
            List<Integer> groupsThatMatch = new ArrayList<>();//список с номера групп, которые совпали по столбцам

            for (String word : line) {
                if (word.equals("")) { //если слово ""
                    colNum++;
                    continue;
                }
                //TODO Переделать для прохода по колонкам, а не по строкам, тогда по памяти и проходу не будет проблем
                if (wordGroupColumn.containsKey(word)) {
                    if (wordGroupColumn.get(word)[1] == colNum) {
                        // добавление строки в группу
                        int numberOfGroup = wordGroupColumn.get(word)[0];
//                    groups.get(numberOfGroup).add(line);
                        groupsThatMatch.add(numberOfGroup);

                    }
                } else {
                    wordGroupColumn.put(word, new int[] {groups.size(), colNum}); //группу инициализирую -1, чтобы не спутать с первой группой
                }
                colNum++;
            }
            // мердж совпадающих групп или создание одной новой
            if (groupsThatMatch.size() == 0) {

                //заполнение номера группы
//                int groupNum = groups.size();
//                for (String word : line) {
//                    if (!word.equals(""))  {
//                        wordGroupColumn.get(word)[0] = groupNum;
//                    }
//                }
                //создание новой группы
                List<String[]> list = new ArrayList<>();
                list.add(line);
                groups.add(list);
            } else {
                mergeLinesInGroup(line, groupsThatMatch, groups, wordGroupColumn);
            }
        }

//        List<Set<String[]>> result =  new ArrayList<>();
//        groups.stream().forEach(list -> result.add(Set.copyOf(list)));
        return groups;
    }

    private static void mergeLinesInGroup(String[] curLine, List<Integer> groupsThatMatch, List<List<String[]>> groups, Map<String, int[]> wordGroupColumn) {
        int mainGroup = groupsThatMatch.get(0); //merge into it
        List<String[]> newGroup = groups.get(mainGroup);
        newGroup.add(curLine);

        for (String word: curLine) {
            if (!word.equals("")) {
                wordGroupColumn.get(word)[0] = mainGroup;
            }
        }

        for (int i = 1; i < groupsThatMatch.size(); i++) {
            int indexMergedGroup = groupsThatMatch.get(i);
            newGroup.addAll(groups.get(indexMergedGroup));
            //меняю группу в мапе для каждого слова
            for (String[] line: groups.get(indexMergedGroup)) {
                for (String word: line) {
                    if (!word.equals("")) {
                        wordGroupColumn.get(word)[0] = mainGroup;
                    }
                }
            }
            groups.remove(indexMergedGroup);
        }
    }

    private static List<String[]> parseFromFile (File file) throws IOException {
        /* использую для хранения значений стрингу, так как не до конца понятен диапазон
         возможных знаний (такой пример не поместится даже в long 83100000580443402  */
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

        private static boolean isValidLine (String str){
            for (String element : str.split(";")) {
                if (!element.matches("\"\\d*\"")) {
                    return false;
                }
            }
            return true;
        }
    }
