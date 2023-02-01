package org.example;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Main {

    public static void main(String[] args) throws IOException {
        int index = 0;
        TypeData typeData = null;
        boolean sortingDirection = true;
        while (true) {
            if (index >= args.length) {
                System.out.println("Specify the flags, output and input files");
                System.exit(1);
            }
            String arg = args[index];
            if (args[index].charAt(0) != '-' ) {
                break;
            }
            if (index > 1) {
                System.out.println("Invalid set of flags");
                System.exit(1);
            }
            switch (arg) {
                case ("-i"): {
                    typeData = TypeData.INTEGER;
                    break;
                }
                case ("-s"): {
                    typeData = TypeData.STRING;
                    break;
                }
                case ("-a"): {
                    sortingDirection = true;
                    break;
                }
                case ("-d"): {
                    sortingDirection = false;
                    break;
                }
                default: {
                    System.out.println("Non-existent flag - " + arg);
                    System.exit(1);
                }
            }
            index++;
        }
        if (typeData == null) {
            System.out.println("The parameters must have the \"-i\" or \"-s\" flag");
            System.exit(1);
        }
        String outFile = args[index];
        index++;
        List<String> inputFiles = new ArrayList<>(Arrays.asList(args).subList(index, args.length));
        if (inputFiles.isEmpty()) {
            System.out.println("Specify the input files");
            System.exit(1);
        }
        MergeSorter mergeSorter = new MergeSorter();
        List<BufferedReader> readers = FileReaderInLines.read(inputFiles);
        BufferedReader intermediateReader;
        if (readers.size() == 1) {
            String fileName = UUID.randomUUID() + ".txt";
            File file = new File(fileName);
            while (!file.createNewFile()) {
                fileName = UUID.randomUUID() + ".txt";
                file = new File(fileName);
            }
            BufferedReader reader = Files.newBufferedReader(Paths.get(fileName));
            intermediateReader = mergeSorter.merge(readers.get(0), reader, typeData, sortingDirection);
            deleteFile(file);
            reader.close();
        } else {
            intermediateReader = mergeSorter.merge(readers.get(0), readers.get(1), typeData, sortingDirection);
            for (int i = 2; i < readers.size(); i++) {
                intermediateReader = mergeSorter.merge(intermediateReader, readers.get(i), typeData, sortingDirection);
            }
        }
        BufferedWriter writer = Files.newBufferedWriter(Paths.get(outFile));
        String line = intermediateReader.readLine();
        while (line != null) {
            writer.write(line + "\n");
            line = intermediateReader.readLine();
        }
        writer.close();
        intermediateReader.close();
        for (String fileName: mergeSorter.getFilesToDelete()) {
            deleteFile(new File(fileName));
        }
    }

    private static void deleteFile(File file) {
        if (!file.delete()) {
            System.out.println("Not all intermediate files have been deleted");
        }
    }
}
