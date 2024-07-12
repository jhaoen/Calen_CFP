package com.example.cfp_genenrator;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListViewAdapter extends ArrayAdapter<CFPEvent> {
    private Context context;

    private boolean[] checkBoxStates;

    public static List<CFPEvent>queryInfo = new ArrayList<>();

    public ListViewAdapter(Context context, List<CFPEvent> data) {
        super(context, R.layout.cfpslist_design, data);
        this.context = context;
        checkBoxStates = new boolean[data.size()];
        Arrays.fill(checkBoxStates, false);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.cfpslist_design, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.eventTextview = convertView.findViewById(R.id.Event);
            viewHolder.nameTextView = convertView.findViewById(R.id.Name);
            viewHolder.whenTextView = convertView.findViewById(R.id.When);
            viewHolder.whereTextView = convertView.findViewById(R.id.Where);
            viewHolder.deadlineTextView = convertView.findViewById(R.id.Deadline);
            viewHolder.checkBox = convertView.findViewById(R.id.checkBox);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // 取得當前位置的數據
        CFPEvent event = getItem(position);

        // 設定元素的值
        if (event != null) {
            viewHolder.eventTextview.setText(event.Event);
            viewHolder.nameTextView.setText(event.Name);
            viewHolder.whenTextView.setText(event.WhenStart + "\n-\n" + event.WhenEnd);
            viewHolder.whereTextView.setText(event.Where);
            viewHolder.deadlineTextView.setText(event.Deadline);
        }

        viewHolder.eventTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, WebPage.class);

                if (event != null) {
                    String url = event.Link;
                    intent.putExtra("URL", url);
                }

                view.getContext().startActivity(intent);
            }
        });

        viewHolder.checkBox.setOnCheckedChangeListener(null); // 避免重複註冊監聽器
        viewHolder.checkBox.setChecked(checkBoxStates[position]);

        viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkBoxStates[position] = isChecked;

                if (isChecked) {
                    queryInfo.add(event);
                } else {
                    queryInfo.remove(event);
                }
            }
        });

        return convertView;
    }

    // ViewHolder 內部類的定義
    private static class ViewHolder {
        TextView eventTextview;
        TextView nameTextView;
        TextView whenTextView;
        TextView whereTextView;
        TextView deadlineTextView;
        CheckBox checkBox;
    }

}
