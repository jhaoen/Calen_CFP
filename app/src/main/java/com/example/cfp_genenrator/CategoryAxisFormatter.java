package com.example.cfp_genenrator;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.List;
import java.util.Set;

public class CategoryAxisFormatter extends ValueFormatter {

    private final List<String> categories;

    public CategoryAxisFormatter(List<String> categories) {
        this.categories = categories;
    }

    @Override
    public String getAxisLabel(float value, AxisBase axis) {
        int index = (int) value;
        if (index >= 0 && index < categories.size()) {
            return categories.get(index);
        }
        return "";
    }
}

