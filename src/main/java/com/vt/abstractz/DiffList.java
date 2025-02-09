package com.vt.abstractz;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;


@Data
public abstract class DiffList {
    private List<String> differentList = new ArrayList<>();

    public List<String> getDifferentList() {
        return differentList;
    }

    public void addDifference(String fieldName) {
        differentList.add(fieldName);
    }
}
