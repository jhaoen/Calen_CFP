package com.example.cfp_genenrator;

import static android.widget.AdapterView.*;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Chart extends AppCompatActivity {

    Map<String, String> categoriesData = new HashMap<>();
    Map<String, String> eventsData = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chart);
        Spinner spinner = findViewById(R.id.spinner4);
        spinner.setSelection(2,false);
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                switch (position){

                    //Login
                    case 0:{
                        Intent intent = new Intent(Chart.this,Login.class);
                        startActivity(intent);
                        break;
                    }
                    //Search
                    case 1:
                        Intent intent2 = new Intent(Chart.this,Search.class);
                        startActivity(intent2);
                        break;
                    //Chart
                    case 2: {
                        break;
                    }
                    //My List
                    case  3:{
                        Intent intent4 = new Intent(Chart.this,UserList.class);
                        startActivity(intent4);
                        break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Thread thread2 = new Thread(chart);
        thread2.start();
    }

    private Runnable chart = new Runnable() {
        @Override
        public void run() {
            try {
                // 修正的 URL
                URL url = new URL("http://192.168.0.86/mywebsite/index.php?query=getchartdata");

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                // 获取从 PHP 文件返回的数据
                InputStream inputStream = new BufferedInputStream(httpURLConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }

                String responseFromPHP = stringBuilder.toString();

                // 处理从 PHP 文件返回的数据
                JSONObject jsonResponse = new JSONObject(responseFromPHP);

                // 提取 "categories" 数组
                JSONArray categoriesArray = jsonResponse.getJSONArray("categories");
                for (int i = 0; i < categoriesArray.length(); i++) {
                    JSONObject categoryObject = categoriesArray.getJSONObject(i);
                    String categoryId = categoryObject.getString("category_id");
                    String categoryName = categoryObject.getString("name");
                    String categoryAmount = categoryObject.getString("amount");
                    categoriesData.put(categoryName,categoryAmount);
                }

                // 提取 "events" 数组
                JSONArray eventsArray = jsonResponse.getJSONArray("events");
                for (int i = 0; i < eventsArray.length(); i++) {
                    JSONObject eventObject = eventsArray.getJSONObject(i);
                    String eventId = eventObject.getString("event_id");
                    String eventName = eventObject.getString("name");
                    String eventAmount = eventObject.getString("amount");
                    eventsData.put(eventName,eventAmount);
                }

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            finally{
                BarChart barChart = findViewById(R.id.barchart);
                barChart.clear();
                Description description = new Description();
                description.setEnabled(false);
                barChart.setDescription(description);
                List<BarEntry> entries = new ArrayList<>();

                int i = 0;
                for (Map.Entry<String, String> entry : categoriesData.entrySet()) {
                    float value = Float.parseFloat(entry.getValue());
                    entries.add(new BarEntry(i++, value));
                }
                // 创建一个 BarDataSet 对象
                BarDataSet dataSet = new BarDataSet(entries, "Category");

                // 设置一些样式，如颜色
                dataSet.setColor(Color.GRAY);

                BarData barData = new BarData(dataSet);

                // 设置 x 轴标签
                XAxis xAxis = barChart.getXAxis();
                xAxis.setGranularity(1f); // 设置标签间隔，单位为 1f 表示每个标签之间的间隔为 1
                xAxis.setLabelRotationAngle(15f); // 设置标签旋转角度，以便更好地适应横坐标标签
                List<String> list = new ArrayList<>(categoriesData.keySet());
                xAxis.setValueFormatter(new CategoryAxisFormatter(list)); // 设置自定义的轴标签格式化器

                // 将 BarData 设置到 BarChart 中
                barChart.setData(barData);
                barChart.notifyDataSetChanged();

                // chart2
                BarChart barChart1 = findViewById(R.id.barchart2);
                barChart1.clear();

                Description description1 = new Description();
                description1.setEnabled(false);
                barChart1.setDescription(description);
                List<BarEntry> entries1 = new ArrayList<>();

                i = 0;
                for (Map.Entry<String, String> entry : eventsData.entrySet()) {
                    float value = Float.parseFloat(entry.getValue());
                    entries1.add(new BarEntry(i++, value));
                }
                // 创建一个 BarDataSet 对象
                BarDataSet dataSet1 = new BarDataSet(entries1, "Event");

                // 设置一些样式，如颜色
                dataSet1.setColor(Color.GREEN);

                BarData barData1 = new BarData(dataSet1);

                // 设置 x 轴标签
                XAxis xAxis1 = barChart1.getXAxis();
                xAxis1.setGranularity(0.9f); // 设置标签间隔，单位为 1f 表示每个标签之间的间隔为 1
                xAxis1.setLabelRotationAngle(10f); // 设置标签旋转角度，以便更好地适应横坐标标签
                List<String> list1 = new ArrayList<>(eventsData.keySet());
                xAxis1.setValueFormatter(new CategoryAxisFormatter(list1)); // 设置自定义的轴标签格式化器

                // 将 BarData 设置到 BarChart 中
                barChart1.setData(barData1);
                barChart1.notifyDataSetChanged();
            }
        }
    };

}
