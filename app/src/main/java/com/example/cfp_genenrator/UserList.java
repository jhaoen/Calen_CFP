package com.example.cfp_genenrator;


import android.content.Intent;
import android.os.Bundle;
import android.graphics.Color;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class UserList extends AppCompatActivity {

    String selected_year,selected_month,chart_select_year;

    ListView listView;

    final HashMap<String,String>chinese_month = new HashMap<>();

    static  boolean isFirst;
    UserListAdapter adapter;

    private void initialize(){

        chinese_month.put("一月","01");
        chinese_month.put("二月","02");
        chinese_month.put("三月","03");
        chinese_month.put("四月","04");
        chinese_month.put("五月","05");
        chinese_month.put("六月","06");
        chinese_month.put("七月","07");
        chinese_month.put("八月","08");
        chinese_month.put("九月","09");
        chinese_month.put("十月","10");
        chinese_month.put("十一月","11");
        chinese_month.put("十二月","12");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_list);

        initialize();

        selected_month = "請選擇";
        selected_year = "請選擇";
        chart_select_year = "2023";
        isFirst = true;

        Spinner spinner = findViewById(R.id.spinner4);
        spinner.setSelection(3,false);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                switch (position){

                    //Login
                    case 0:{
                        Intent intent = new Intent(UserList.this,Login.class);
                        startActivity(intent);
                        break;
                    }
                    //Search
                    case 1:{
                        Intent intent = new Intent(UserList.this,Search.class);
                        startActivity(intent);
                        break;
                    }
                    //Chart
                    case 2: {
                        Intent intent3 = new Intent(UserList.this, Chart.class);
                        startActivity(intent3);
                        break;
                    }
                    case 3:{
                       break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // 在沒有選擇項目的情況下觸發的操作
            }
        });

        Spinner year = findViewById(R.id.Year);
        Spinner month = findViewById(R.id.Month);
        Spinner pie_select_year = findViewById(R.id.pie_select_year);
        pie_select_year.setSelection(0);

        listView = findViewById(R.id.list);

        pie_select_year.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                chart_select_year = (String) adapterView.getItemAtPosition(i);
                setPieChart();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        year.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {


                selected_year = (String) adapterView.getItemAtPosition(pos);

                if(!Objects.equals(selected_year, "請選擇")){
                    filt_year_month();
                }



            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        month.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {

                selected_month = (String) adapterView.getItemAtPosition(pos);

                if(!Objects.equals(selected_month, "請選擇")){
                    filt_year_month();
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setPieChart();
    }
    private void filt_year_month(){


        // 全選狀態
        if(Objects.equals(selected_month, "all") || Objects.equals(selected_year, "all")){
            adapter = new UserListAdapter(this, ListViewAdapter.queryInfo);
            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            return;
        }

        ArrayList<CFPEvent>new_list = new ArrayList<>();

        for(int i=0;i<ListViewAdapter.queryInfo.size();i++){

            String[] time = ListViewAdapter.queryInfo.get(i).WhenStart.split("-");
            String year = time[0];
            String month = time[1];
            //濾除年、月非指定選項的
            String M = chinese_month.get(selected_month);


            if(!Objects.equals(year, selected_year) || !Objects.equals(month, M))continue;

            new_list.add(ListViewAdapter.queryInfo.get(i));
        }

        // 重新顯示
        adapter = new UserListAdapter(this, new_list);
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
    private void setPieChart(){

        PieChart pieChart = findViewById(R.id.piechart);
        pieChart.clear();

        // Sample data for the chart
        List<PieEntry> entries = new ArrayList<>();
        HashMap<String,Integer>Month_count = new HashMap<String, Integer>();

        int count = 0;
        for(int i=0;i<ListViewAdapter.queryInfo.size();i++){

            String[] startDate = ListViewAdapter.queryInfo.get(i).WhenStart.split("-");

            String year = startDate[0];
            String month = startDate[1];

            if(!Objects.equals(year, chart_select_year))
                continue;

            count += 1;
            if (Month_count.containsKey(month)) {

                Month_count.put(month, Month_count.get(month) + 1);
            }
            else {
                Month_count.put(month, 1);
            }

        }

        HashMap<Integer,Float>Ratio = new HashMap<Integer,Float>();

        float size = Float.parseFloat(String.valueOf(count));

        String[] months = new String[]{"January","February","March","April","May","June","July","August","September","October","November","December"};

        for(Map.Entry<String,Integer>e : Month_count.entrySet()){


            switch (e.getKey()){

                case "01":{
                    Ratio.put(0,(e.getValue()/size));
                    break;
                }
                case "02":{
                    Ratio.put(1,(e.getValue()/size));
                    break;
                }
                case "03":{
                    Ratio.put(2,(e.getValue()/size));
                    break;
                }
                case "04":{
                    Ratio.put(3,(e.getValue()/size));
                    break;
                }
                case "05":{
                    Ratio.put(4,(e.getValue()/size));
                    break;
                }
                case "06":{
                    Ratio.put(5,(e.getValue()/size));
                    break;
                }
                case "07":{
                    Ratio.put(6,(e.getValue()/size));
                    break;
                }
                case "08":{
                    Ratio.put(7,(e.getValue()/size));
                    break;
                }
                case "09":{
                    Ratio.put(8,(e.getValue()/size));
                    break;
                }
                case "10":{
                    Ratio.put(9,(e.getValue()/size));
                    break;
                }
                case "11":{
                    Ratio.put(10,(e.getValue()/size));
                    break;
                }
                case "12":{
                    Ratio.put(11,(e.getValue()/size));
                    break;
                }
            }
        }

        int[] brightColors = {

                Color.rgb(0, 75, 151),
                Color.rgb(0, 90, 181),
                Color.rgb(151, 203, 255),
                Color.rgb(0, 128, 255),
                Color.rgb(40, 148, 255),
                Color.rgb(0, 61, 121),
                Color.rgb(70, 163, 255),
                Color.rgb(172, 214, 255),
                Color.rgb(102, 179, 255),
                Color.rgb(0, 102, 204),
                Color.rgb(132, 193, 255),
                Color.rgb(196, 225, 255)
        };

        for(int i = 0 ;i < 12 ;i++){

            if(Ratio.containsKey(i))
                entries.add(new PieEntry(Ratio.get(i), months[i]));
        }


        PieDataSet dataSet = new PieDataSet(entries,"");
        dataSet.setColors(brightColors);
        dataSet.setValueTextSize(8f);
        dataSet.setDrawValues(false);

        Description description = new Description();
        description.setText("每月會議分佈"); // 設置標題
        description.setTextSize(17f); // 設置文字大小
        description.setTextColor(Color.BLACK); // 設置文字顏色
        description.setPosition(pieChart.getWidth() - 10, 100);

        Legend legend = pieChart.getLegend();
        legend.setTextSize(10f);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT); // 水平靠左
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);           // 垂直方向
        legend.setDrawInside(false);

        PieData data = new PieData(dataSet);
        pieChart.setDrawEntryLabels(false);
        pieChart.setDescription(description);
        pieChart.setData(data);
        pieChart.invalidate();
    }
}
