package org.example;
import java.io.*;
import java.util.ArrayList;

public class CSVWriter {
    public void writeLineToTable(String line, String table) {
        File file = new File("database/" + table + ".csv");
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(System.lineSeparator() + line);
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }
    }
    public void writeMultipleLinesToTable(ArrayList<String> lines, String table) {
        File file = new File("database/" + table + ".csv");
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            for (String line : lines) {
                writer.write(System.lineSeparator() + line);
            }
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }
    }
}