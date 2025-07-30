package utils;

import org.apache.commons.csv.*;
import java.io.*;
import java.util.*;

public class CSVUtils {
    public static List<String[]> leerCSV(String archivo) {
        List<String[]> datos = new ArrayList<>();
        try (Reader reader = new FileReader(archivo)) {
            CSVFormat format = CSVFormat.DEFAULT
                .builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .build();

            CSVParser parser = new CSVParser(reader, format);
            for (CSVRecord record : parser) {
                datos.add(new String[]{
                    record.get("username"),
                    record.get("password")
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return datos;
    }
}