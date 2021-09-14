package com.example.cmd;

import com.opencsv.bean.CsvBindByPosition;
import lombok.Data;

@Data
public class Boo {
    @CsvBindByPosition(position = 0)
    private int id;
    @CsvBindByPosition(position = 1)
    private int x;
    @CsvBindByPosition(position = 2)
    private double y;
    @CsvBindByPosition(position = 3)
    private double z;

}
