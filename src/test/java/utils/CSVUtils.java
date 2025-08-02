package utils;

import org.apache.commons.csv.*;
import java.io.*;
import java.util.*;

public class CSVUtils {
public static List<String[]> leerCSV(String archivo) {
    List<String[]> datos = new ArrayList<>();
    // Usar try-with-resources para asegurar el cierre de los recursos
    try (Reader reader = new FileReader(archivo);
         CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT
             .builder()
             .setHeader()
             .setSkipHeaderRecord(true)
             .build())) {
        
        for (CSVRecord record : parser) {
            datos.add(new String[] {
                record.get("username"),
                record.get("password")
            });
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
    return datos;
}

public static List<String[]> registerLeerCSV(String archivo) {
    List<String[]> datos = new ArrayList<>();
    // Usar try-with-resources para asegurar el cierre de los recursos
    try (Reader reader = new FileReader(archivo);
         CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT
             .builder()
             .setHeader()
             .setSkipHeaderRecord(true)
             .build())) {
        
        for (CSVRecord record : parser) {
            datos.add(new String[] {
                record.get("nombre"),
                    record.get("apellido"),
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