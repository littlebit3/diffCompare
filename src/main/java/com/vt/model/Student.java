package com.vt.model;

import com.vt.abstractz.DiffList;
import com.vt.annotation.CompareCollection;
import com.vt.annotation.CompareStringToCollection;
import com.vt.annotation.IgnoreCompare;
import lombok.Data;

import java.util.List;

@Data
public class Student  extends DiffList {

    private String name;
    private Integer age;
    private Long id;

    @IgnoreCompare
    private String noCompare;

    @CompareCollection
    private List<String> projects;

    @CompareStringToCollection
    private String tags;

}
