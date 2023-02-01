package org.example;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class MergeSorter {

    private final List<String> filesToDelete = new ArrayList<>();

    public List<String> getFilesToDelete() {
        return filesToDelete;
    }

    public BufferedReader merge(BufferedReader reader1, BufferedReader reader2, TypeData typeData,
                                       boolean sortingDirection) throws IOException {
        String intermediateFile = UUID.randomUUID() + ".txt";
        Path path = Paths.get(intermediateFile);
        BufferedWriter writer = Files.newBufferedWriter(path);
        reader1.mark(1000);
        reader2.mark(1000);
        String el1 = reader1.readLine();
        String el2 = reader2.readLine();
        if (el1 == null || isNotNumeric(el1))
            el1 = null;
        if (el2 == null || isNotNumeric(el2))
            el2 = null;
        while (el1 != null && el2 != null) {
            if (Objects.equals(typeData, TypeData.INTEGER)) {
                if (compareIntElements(el1, el2, sortingDirection)) {
                    writer.write(el1 + "\n");
                    reader2.reset();
                }
                else {
                    writer.write(el2 + "\n");
                    reader1.reset();
                }
            }
            if (Objects.equals(typeData, TypeData.STRING)) {
                if (compareStringElements(el1, el2, sortingDirection)) {
                    writer.write(el1  + "\n");
                    reader2.reset();
                }
                else {
                    writer.write(el2  + "\n");
                    reader1.reset();
                }
            }
            reader1.mark(1000);
            reader2.mark(1000);
            String previousEl1 = el1;
            String previousEl2 = el2;
            el1 = reader1.readLine();
            el2 = reader2.readLine();
            if (el1 == null || el2 == null)
                break;
            if (Objects.equals(typeData, TypeData.INTEGER)) {
                if (isNotNumeric(el1) || compareIntElements(el1, previousEl1, sortingDirection)) {
                    el1 = null;
                }
                if (isNotNumeric(el2) || compareIntElements(el2, previousEl2, sortingDirection)) {
                    el2 = null;
                }
            }
            if (Objects.equals(typeData, TypeData.STRING)) {
                if (compareStringElements(el1, previousEl1, sortingDirection)) {
                    el1 = null;
                }
                if (compareStringElements(el2, previousEl2, sortingDirection)) {
                    el2 = null;
                }
            }
        }
        if (el1 == null) {
            validateInputData(reader2, typeData, sortingDirection, writer, el2);
        }
        validateInputData(reader1, typeData, sortingDirection, writer, el1);
        filesToDelete.add(intermediateFile);
        reader1.close();
        reader2.close();
        writer.close();
        return Files.newBufferedReader(path);
    }

    private void validateInputData(BufferedReader reader1, TypeData typeData, boolean sortingDirection, BufferedWriter writer, String el) throws IOException {
        String previousEl1 = el;
        while (el != null) {
            if (Objects.equals(typeData, TypeData.INTEGER)) {
                if (isNotNumeric(el) || isNotNumeric(previousEl1) || compareIntElements(el, previousEl1, sortingDirection)) {
                    break;
                }
            }
            if (Objects.equals(typeData, TypeData.STRING)) {
                if (compareStringElements(el, previousEl1, sortingDirection)) {
                    break;
                }
            }
            writer.write(el  + "\n");
            previousEl1 = el;
            el = reader1.readLine();
        }
    }

    private boolean compareIntElements(String el1, String el2, boolean sortingDirection) {
        if (sortingDirection)
            return Integer.parseInt(el1) < Integer.parseInt(el2);
        return Integer.parseInt(el1) > Integer.parseInt(el2);
    }

    private boolean compareStringElements(String el1, String el2, boolean sortingDirection) {
        if (sortingDirection)
            return el1.compareTo(el2) < 0;
        return el1.compareTo(el2) > 0;
    }

    private boolean isNotNumeric(String str) {
        try {
            Integer.parseInt(str);
            return false;
        } catch(NumberFormatException e){
            System.out.println("Data with the wrong type was found in some input files the data was partially sorted");
            return true;
        }
    }
}
