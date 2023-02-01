package org.example;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileReaderInLines {

    public static List<BufferedReader> read(List<String> pathInputFiles) {

        List<BufferedReader> readers = new ArrayList<>();
        for (String el: pathInputFiles) {
            try {
                //создаем BufferedReader с существующего FileReader для построчного считывания
                BufferedReader reader = Files.newBufferedReader(Paths.get(el));
                readers.add(reader);
            } catch (FileNotFoundException e) {
                System.out.println("File " + el + " not found");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return readers;
    }
}
