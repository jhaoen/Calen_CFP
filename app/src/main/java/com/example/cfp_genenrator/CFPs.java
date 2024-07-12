package com.example.cfp_genenrator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CFPs extends Activity {

    private static final int LOGIN_REQUEST_CODE = 123;

    public static boolean islogin = false;
    List<CFPEvent> cfpEventList;

    Map<String,String>Event2URL = new HashMap<>();

    Map<String,ArrayList<CFPEvent>>Categories = new HashMap<>();

    String UserName = null;
    String UserEmail = null;

    ListViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.cfps_list);

        Intent intent = getIntent();
        String CategoryText = intent.getStringExtra("SearchCategory");
        String SearchURL = intent.getStringExtra("SearchUrl");

        TextView CategoryTextview = findViewById(R.id.Title);
        CategoryTextview.setText(CategoryText);
        TextView RecommandWebsiteTextview = findViewById(R.id.recommandurl);
        RecommandWebsiteTextview.setText(Html.fromHtml("<u>"+"相關領域論文連結"+"</u>"));
        RecommandWebsiteTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(CFPs.this,WebPage.class);
                intent1.putExtra("URL",SearchURL);
                startActivity(intent1);
            }
        });


        Button button = findViewById(R.id.addtocalender);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                UserName = sharedPreferences.getString("UserName",null);
                UserEmail = sharedPreferences.getString("UserEmail",null);

                if(!islogin){
                    Intent intent = new Intent(CFPs.this, Login.class);
                    startActivityForResult(intent, LOGIN_REQUEST_CODE);
                    islogin = true;
                }
                else{
                    Thread thread = new Thread(postCalenderInfo);
                    thread.start();
                }

            }
        });



        Spinner spinner = findViewById(R.id.spinner3);
        spinner.setSelection(2,false);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                switch (position){

                    //Login
                    case 0:{
                        Intent intent = new Intent(CFPs.this,Login.class);
                        startActivity(intent);
                        break;
                    }
                    //Search
                    case 1:
                        Intent intent2 = new Intent(CFPs.this,Search.class);
                        startActivity(intent2);
                        break;
                    //Chart
                    case 2: {
                        Intent intent3 = new Intent(CFPs.this, Chart.class);
                        startActivity(intent3);
                        break;
                    }
                    //My List
                    case  3:{
                        Intent intent4 = new Intent(CFPs.this,UserList.class);
                        startActivity(intent4);
                        break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // 在沒有選擇項目的情況下觸發的操作
            }
        });

        getEventData getEventData = new getEventData(CFPs.this);
        cfpEventList = getEventData.loadCFPEventList();

        ClassFier();

    }

    private void PutOnCFPs() {

        Intent intent = getIntent();
        String url = intent.getStringExtra("SearchUrl");
        String Selected_Category = intent.getStringExtra("SearchCategory");
        ArrayList<CFPEvent>list = Categories.get(Selected_Category);
        adapter = new ListViewAdapter(this,list);
        ListView listView = findViewById(R.id.listview);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


            }
        });
    }
    private void ClassFier(){

        for(int i=0;i<cfpEventList.size();i++){


            Event2URL.put(cfpEventList.get(i).Event,cfpEventList.get(i).Link);

            String categoryString = cfpEventList.get(i).Category;

            String[] categories = categoryString.split(",");

            List<String> categoryList = Arrays.asList(categories);

            for(String c : categoryList){
                if(Categories.containsKey(c)){
                    ArrayList<CFPEvent> maplist = Categories.get(c);
                    maplist.add(cfpEventList.get(i));
                    Categories.put(c,maplist);
                }
                else{
                    ArrayList<CFPEvent>maplist = new ArrayList<>();
                    maplist.add(cfpEventList.get(i));
                    Categories.put(c,maplist);
                }
            }
        }

        // save Categories in Sharepreference
        getEventData saveHelper = new getEventData(CFPs.this);
        saveHelper.saveCategories(Categories);

        PutOnCFPs();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOGIN_REQUEST_CODE && resultCode == RESULT_OK) {
            Thread thread = new Thread(postCalenderInfo);
            thread.start();
        }
    }
    Runnable postCalenderInfo = new Runnable() {
        @Override
        public void run() {
            try {
                boolean res = postData();
                if (res) {
                    String message = getResources().getString(R.string.success);

                    // 使用 runOnUiThread 來確保 Toast 在主 UI 線程上顯示
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CFPs.this, message, Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    String message = getResources().getString(R.string.fail);

                    // 使用 runOnUiThread 來確保 Toast 在主 UI 線程上顯示
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CFPs.this, message, Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    };
    public boolean postData() throws IOException {

        String urlString = "http://192.168.0.86/mywebsite/index.php?query=google_calender";
        for(int i = 0; i < ListViewAdapter.queryInfo.size() ; i++){

            String EventSumary = ListViewAdapter.queryInfo.get(i).Event;
            String EventName = ListViewAdapter.queryInfo.get(i).Name;
            String URL = urlString + "&event=" + EventSumary + "&name=" + EventName + "&email=" + UserEmail;
            URL url = new URL(URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            int responsecode = connection.getResponseCode();

            connection.disconnect();

            if(responsecode != 200){
                Log.d("responseCode",String.valueOf(responsecode));
                return false;
            }
        }
        return true;
    }

}
