package org.example;

import java.io.*;
import java.net.URL;


public class Main {
    public static void main(String[] args) throws IOException {
        long startTiming = System.currentTimeMillis();

        Text text = new Text();
        text.parseText(readFile());
        text.setOnGroups();

        long endTiming = System.currentTimeMillis();
        System.out.printf("Время работы программы: %d миллисекунд", (endTiming -  startTiming));
    }

    public static File readFile() {
        // String fileName = args[0];
        URL url = Main.class.getClassLoader().getResource("test.txt");
        File file = new File(url.getFile());
        return file;
    }
}
