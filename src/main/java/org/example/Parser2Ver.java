package org.example;

import java.io.*;
import java.util.*;

 public class Parser2Ver{
    static int MAX_LENGTH;
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

//        writeToFile(groups, goodGroups);

        long end = System.currentTimeMillis();
        System.out.printf("Время работы программы: %d миллисекунд", (end - start));
    }

//    private static void writeToFile(List<List<String[]>> groups, int goodGroups) throws IOException {
//        try (BufferedWriter writer = new BufferedWriter(new FileWriter("resultParser.txt"))) {
//            writer.write("количество групп с более чем одним элементом: " + goodGroups);
//            writer.newLine();
//            groups.sort((o1, o2) -> Integer.compare(o2.size(), o1.size()));
//            int i = 1; //groups
//            for (List<String[]> group : groups) {
//                int j = 1; //lines
//                writer.write("Группа " + i);
//                writer.newLine();
//                for (String[] line: group) {
//                    StringBuilder current = new StringBuilder("строчка " + j + ": ");
//                    for (String word : line) {
//                        current.append("\"").append(word).append("\";");
//                    }
//                    writer.write(String.valueOf(current));
//                    writer.newLine();
//                    j++;
//                }
//                i++;
//            }
//        }
//    }

    //TODO Переделать для прохода по колонкам, а не по строкам, тогда по памяти и проходу не будет проблем
    private static List<List<String[]>> divisionIntoGroups(List<String[]> lines) {
        // 1 List - номер группы, 2 List - номер строки, String[] - сами строки
        List<List<String[]>> groups = new ArrayList<>();
        Map<String, Integer> groupIndex = new HashMap<>();

        for (int col = 0; col < MAX_LENGTH; col++) {
            List<Integer> groupsThatMatch = new ArrayList<>();//номера строк, которые совпали по текущему столбцу
            Map<String, int[]> wordGroupLine = new HashMap<>();

            for (int line = 0; line < lines.size(); line++) {
                if (lines.get(line).length < col || lines.get(line)[col].equals("")) {
                    continue;
                }
                String word = lines.get(line)[col];
                if (wordGroupLine.containsKey(word)) {
                    int numberGroup = wordGroupLine.get(word)[0];
                    groupsThatMatch.add(numberGroup);
                } else {
                    wordGroupLine.put(word, new int[]{groups.size(), line});
                    //создаю новую группу
                    List<String[]> list = new ArrayList<>();
                    list.add(lines.get(line));
                    groups.add(list);
                }
            }
        }
        return groups;
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
                    MAX_LENGTH = Math.max(MAX_LENGTH, curLine.length);
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
