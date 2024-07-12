package com.example.cfp_genenrator;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserListAdapter extends ArrayAdapter<CFPEvent> {

    private Context context;


    public UserListAdapter(Context context, List<CFPEvent> data) {
        super(context, R.layout.user_list, data);
        this.context = context;
    }

    private String previousDays(String startdate){

        final int previousdays = 3;
        String[] time = startdate.split("-");
        String year = time[0];
        String month = time[1];
        String day = time[2];
        int y,m,d;

        if(month == "03" && (day == "01" || day == "02" || day == "03")){
            String date = year + "-" + "02" + "25";
            return date;
        }
        // 前三天為上個月
        if(day.startsWith("0") && Integer.parseInt(day.substring(1)) < previousdays ){

            // 如果是一月
            if(month.startsWith("0") && Integer.parseInt(day.substring(1)) == 1){

                y = Integer.parseInt(year) - 1;
                m = 12;
                d = Integer.parseInt(day) - previousdays + 30;
            }
            else{

                y = Integer.parseInt(year);
                m = Integer.parseInt(month) - 1;
                d = Integer.parseInt(day) - previousdays + 30;
            }
        }
        else{

            y = Integer.parseInt(year);
            m = Integer.parseInt(month);
            d = Integer.parseInt(day) - previousdays;
        }

        String result_month = "";
        String result_day = "";

        // 判斷 month 是否要補零
        if(m / 10 == 0){
            result_month = "0" + String.valueOf(m);
        }
        else{
            result_month = String.valueOf(m);
        }
        // 判斷 day 是否要補零
        if(d / 10 == 0){
            result_day = "0" + String.valueOf(d);
        }
        else{
            result_day = String.valueOf(d);
        }
        String date = String.valueOf(y) + "-" + result_month + "-" + result_day;
        return date;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.userlist_design, parent, false);
        }

        // 取得項目布局中的元素
        TextView eventTextview = convertView.findViewById(R.id.Event);
        TextView whereTextView = convertView.findViewById(R.id.Where);
        TextView period = convertView.findViewById(R.id.period);
        TextView startday = convertView.findViewById(R.id.Startday);
        TextView route = convertView.findViewById(R.id.route);
        ImageButton search_button = convertView.findViewById(R.id.Plane);


        CFPEvent event = getItem(position);

        DBHelper db = new DBHelper(convertView.getContext());
        ArrayList<DisplayInfo>displayinfo = db.getAllUser();

        // 設定元素的值
        if (event != null) {
            eventTextview.setText(event.Event);
            whereTextView.setText(event.Where);
            String period_s = previousDays(event.WhenStart);
            period.setText(period_s+" -\n"+event.WhenStart);
            startday.setText(event.WhenStart);
        }


        String EventName = getItem(position).Event;


        for(DisplayInfo info : displayinfo){
            if(Objects.equals(info.Event, EventName)){
                String Route = "TPE - " + info.arrivecountry;
                route.setText(Route);
                break;
            }
        }

        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 執行機票查詢
                String prefix = "https://www.tw.kayak.com/flights/";
                String route = "TPE-";// + Country
                String startDate = "";
                String endDate = "";
                String postfix = "?sort=bestflight_a";


                for(DisplayInfo info : displayinfo){
                    if(Objects.equals(info.Event, EventName)){
                        route += info.destcode + "/";
                        startDate = previousDays(info.StartDay) + "/";
                        endDate = info.StartDay;
                        break;
                    }
                }

                String url = prefix + route + startDate + endDate + postfix;
                Intent intent = new Intent(view.getContext(),WebPage.class);
                intent.putExtra("URL",url);
                view.getContext().startActivity(intent);

            }
        });
        return convertView;
    }
}
