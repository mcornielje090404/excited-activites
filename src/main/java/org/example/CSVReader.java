package org.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class CSVReader {
    public String[] getHeaders(String table) {
        try (Scanner fileReader = new Scanner(new File("database/" + table + ".csv"))) {
            fileReader.useDelimiter(",");
            String line = fileReader.nextLine();
            return line.split(",");
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    public String[] getEntityDataById(String table, String id) {
        String[] headers = this.getHeaders(table);
        int idIndex = Arrays.stream(headers).toList().indexOf("id");

        ArrayList<String[]> tableData = this.readTable(table);

        for (String[] data : tableData) {
            if (data[idIndex].equals(id)) {
                return data;
            }
        }

        return null;
    }

    public ArrayList<String[]> getManyNestedEntitiesById(String table, String parentId, String parentIdColumn) {
        ArrayList<String[]> nestedEntities = new ArrayList<>();
        String[] headers = this.getHeaders(table);
        int idIndex = Arrays.stream(headers).toList().indexOf(parentIdColumn);

        ArrayList<String[]> tableData = this.readTable(table);


        for (String[] data : tableData) {
            if (data[idIndex].equals(parentId)) {
                nestedEntities.add(data);
            }
        }

        return nestedEntities;
    }

    public String[] getEntityDataByColumn(String parentTable, String childTable, String parentId, String parentIdColumn) {
        String[] parentHeaders = this.getHeaders(parentTable);
        int childIdIndex = Arrays.stream(parentHeaders).toList().indexOf(parentIdColumn);

        String[] childHeaders = this.getHeaders(childTable);
        int idIndex = Arrays.stream(childHeaders).toList().indexOf("id");

        String[] parentRow = this.getEntityDataById(parentTable, parentId);

        ArrayList<String[]> tableData = this.readTable(childTable);

        for (String[] data : tableData) {
            if (data[idIndex].equals(parentRow[childIdIndex])) {
                return data;
            }
        }

        return null;
    }

    public ArrayList<String[]> readTable(String table) {
        ArrayList<String[]> tableData = new ArrayList<>();

        try (Scanner fileReader = new Scanner(new File("database/" + table + ".csv"))) {
            fileReader.useDelimiter(",");
            fileReader.nextLine();

            while (fileReader.hasNextLine()) {
                tableData.add(fileReader.nextLine().split(","));
            }
        } catch (FileNotFoundException e) {
            return tableData;
        }

        return tableData;
    }
}
