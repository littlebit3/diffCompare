package com.vt;

import com.vt.model.Student;

import java.util.Arrays;
import java.util.List;

public class Test {
    public static void main(String[] args) {
        Student student1 = new Student();
        student1.setId(11111L);
        student1.setName("张三");
        student1.setAge(2);
        student1.setNoCompare("666");
        student1.setTags("1,2,3,4");
        student1.setProjects(Arrays.asList("语文","英语","数学"));
        Student student2 = new Student();
        student2.setId(211111L);
        student2.setName("张三");
        student2.setAge(12);
        student2.setNoCompare("6adwqad66");
        student2.setTags("1,2,4,3");
        student2.setProjects(Arrays.asList("数学","语文","英语"));

        DiffCompare.findDiffFields(student1, student2);
        List<String> differentList1 = student1.getDifferentList();
        DiffCompare.findDiffFields(student2, student1);
        List<String> differentList2 = student2.getDifferentList();
        System.out.println("end");
    }
}
