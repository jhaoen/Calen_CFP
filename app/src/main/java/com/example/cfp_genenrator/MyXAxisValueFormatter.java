package com.example.cfp_genenrator;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.ValueFormatter;

public class MyXAxisValueFormatter extends ValueFormatter {


    private final String[] months = new String[]{"一月", "二月", "三月", "四月", "五月","六月","七月","八月","九月","十月","十一月","十二月"};

    public MyXAxisValueFormatter() {

    }

    @Override
    public String getAxisLabel(float value, AxisBase axis) {
        int index = Math.round(value);

        if (index >= 0 && index < months.length) {
            return months[index];
        } else {
            return "";
        }
    }
}

