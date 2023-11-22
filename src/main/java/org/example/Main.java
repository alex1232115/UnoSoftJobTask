package org.example;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;


public class Main {
    public static void main(String[] args) throws IOException {
        long startTiming = System.currentTimeMillis();

//        String fileName = args[0];
        File file = new File("C:\\Users\\alash\\IdeaProjects\\unoSoftTask\\src\\main\\resources\\lng.txt");

        Text text = new Text();
        Set<String> lines = text.parseText(file);
        System.out.println("Количество строк: " + lines.size());
        List<List<String>> groups = text.setGroups(lines);
        System.out.println("Количество групп: " + groups.size());

        int goodGroups = 0;
        for (List<String> list : groups) {
            if (list.size() > 1) {
                goodGroups++;
            }
        }
        text.printToFile(groups, "result.txt", goodGroups);

        long endTiming = System.currentTimeMillis();
        System.out.printf("Время работы программы: %d миллисекунд", (endTiming - startTiming));
    }
}
