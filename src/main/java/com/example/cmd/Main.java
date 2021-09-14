package com.example.cmd;

import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;


import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {

    private static final String SAMPLE_CSV_SOURCE = "boo.csv";
    private static final String SAMPLE_CSV_OUTPUT = "output_foo.csv"; // Absolute path to specific folder, otherwise CSV file will be created on same level as pom.xml


    private static <T> List<T> parseCSVDataToBeans(final String source, final ClassLoader classLoader, final Class<T> type){
        try (InputStream is = classLoader.getResourceAsStream(source);
             InputStreamReader streamReader = new InputStreamReader(is, StandardCharsets.UTF_8);
             Reader reader = new BufferedReader(streamReader)){
            return new CsvToBeanBuilder(reader).withType(type).build()
                    .parse();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static <T> void writeBeansToCSV(final String destination,final List<T> data ) {
        try(Writer writer = Files.newBufferedWriter(Paths.get(destination))) {
            StatefulBeanToCsv sbc = new StatefulBeanToCsvBuilder(writer)
                    .withSeparator(CSVWriter.DEFAULT_SEPARATOR).withApplyQuotesToAll(false).build();
            sbc.write(data);

        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException ex){
            throw new RuntimeException(ex);
        }

    }

    private static Foo buildFoo(final List<Boo> b){
        double sum = 0;
        for (Boo boo : b) {
            sum = sum + boo.getY()+ boo.getZ();
        }
        int id = b.stream().map(Boo::getId).findFirst().get();
        int x = b.stream().map(Boo::getX).findFirst().get();
        return new Foo(id, x, sum);
    }


  public static void main(String[] args) {
      ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        List<Boo> boos = parseCSVDataToBeans(SAMPLE_CSV_SOURCE, classloader, Boo.class);

        boos.forEach(System.out::println);

      /**
       * Do your magic here in terms of custom logic to producing output data that needs to be stored in CSV file.
       * Just to show case it I will just do a sum of Y and Z filtered out per X of Boo class type.
       */
        List<Foo> f = new ArrayList<>();
        Map<Integer, List<Boo>> groupedByX =  boos.stream().collect(Collectors.groupingBy(Boo::getX));
        groupedByX.values().forEach(b -> f.add(buildFoo(b)));

        writeBeansToCSV(SAMPLE_CSV_OUTPUT, f);

  }
}
