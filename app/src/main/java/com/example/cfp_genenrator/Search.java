package com.example.cfp_genenrator;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Scanner;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class Search extends AppCompatActivity  {

    ArrayList<String>Category = new ArrayList<>();


    RecyclerViewAdapter mAdapter;
    List<CFPEvent> cfpEventList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);


        Thread thread1 = new Thread(getEventData);
        thread1.start();

        Thread thread2 = new Thread(getInfo);
        thread2.start();

        Spinner spinner = findViewById(R.id.spinner);
        spinner.setSelection(1,false);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                switch (position){

                    //Login
                    case 0:{
                        Intent intent = new Intent(Search.this,Login.class);
                        startActivity(intent);
                        break;
                    }
                    //Search
                    case 1:
                        break;
                    //Chart
                    case 2: {
                        Intent intent3 = new Intent(Search.this, Chart.class);
                        startActivity(intent3);
                        break;
                    }
                    case 3:{
                        Intent intent3 = new Intent(Search.this, UserList.class);
                        startActivity(intent3);
                        break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // 在沒有選擇項目的情況下觸發的操作
            }
        });
    }


    Runnable getEventData = new Runnable() {
        @Override
        public void run() {
            URL url = null;
            try {
                url = new URL("http://192.168.0.86/mywebsite/index.php?query=geteventdata");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                try {
                    InputStream in = urlConnection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) result.append(line);
                    String jsonString = result.toString();
                    JSONArray jsonArray = new JSONArray(jsonString);

                    cfpEventList = new ArrayList<>();

                    // 迭代 JSON 數組的每一個元素
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonEvent = jsonArray.getJSONObject(i);

                        CFPEvent cfpEvent = new CFPEvent();
                        cfpEvent.Event = jsonEvent.getString("Event");
                        cfpEvent.Name = jsonEvent.getString("Name");
                        cfpEvent.WhenStart = jsonEvent.getString("WhenStart");
                        cfpEvent.WhenEnd = jsonEvent.getString("WhenEnd");
                        cfpEvent.Where = jsonEvent.getString("Where");
                        cfpEvent.Deadline = jsonEvent.getString("Deadline");
                        cfpEvent.Link = jsonEvent.getString("Link");
                        cfpEvent.Category = jsonEvent.getString("Category");
                        cfpEventList.add(cfpEvent);

                        String[] categories = cfpEvent.Category.split(",");
                        List<String> categoryList = Arrays.asList(categories);
                        for(String c : categoryList){
                            if(!Category.contains(c) && !Objects.equals(c, " ")){
                                Category.add(c);
                            }
                        }
                    }


                }
                catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                finally {
                    urlConnection.disconnect();
                    getEventData getEventData = new getEventData(Search.this);
                    getEventData.saveCFPEventList(cfpEventList);
                    getEventData.saveALLCategory(Category);



                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Spinner spinner = findViewById(R.id.spinner);
                            Context context = spinner.getContext();
                            RecyclerView recyclerView = findViewById(R.id.recyclerView);
                            mAdapter = new RecyclerViewAdapter(Category);
                            recyclerView.setLayoutManager(new LinearLayoutManager(Search.this));
                            recyclerView.setAdapter(mAdapter);

                        }
                    });

                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }



    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        /**SearchView設置，以及輸入內容後的行動*/
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String infix = "https://www.readthispaper.com/tw/search/results?q=";
                String[] text = query.split(" ");
                String q="";
                for(int i = 0;i<text.length;i++){
                    q+=text[i];
                    if(i != text.length - 1){
                        q+="+";
                    }
                }
                String postfix = "&p=1";
                String url  = infix + q + postfix;
                Intent intent = new Intent(Search.this,CFPs.class);
                intent.putExtra("SearchUrl",url);
                intent.putExtra("SearchCategory",query);
                startActivity(intent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                /**調用RecyclerView內的Filter方法*/
                mAdapter.getFilter().filter(newText);
                mAdapter.notifyDataSetChanged();
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    Runnable getInfo = new Runnable() {
        @Override
        public void run() {

            DBHelper dbHelper = new DBHelper(Search.this);
            if(dbHelper.getAllUser().size() > 0)return;


            try (InputStream inputStream = getResources().openRawResource(R.raw.flight)) {
                // 繼續處理檔案內容
                Scanner scanner = new Scanner(inputStream);

                // 逐行讀取檔案內容
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    String[] entities = line.split("/");
                    dbHelper.addUser(entities);
                }


                // 關閉Scanner
                scanner.close();
            } catch (IOException e) {
                // 處理 IOException
                e.printStackTrace();
            }
        }
    };
}


