package com.example.cfp_genenrator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;


public class Login extends Activity {

    String UserName;
    String UserEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        CFPs.islogin = true;
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();

        EditText username = findViewById(R.id.username);
        EditText email = findViewById(R.id.Email);
        Spinner spinner = findViewById(R.id.spinner2);
        Button btn = findViewById(R.id.button);
        spinner.setSelection(0,false);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                switch (position){

                    //Login
                    case 0:{

                        break;
                    }
                    //Search
                    case 1:{
                        Intent intent = new Intent(Login.this,Search.class);
                        startActivity(intent);
                        break;
                    }
                    //Chart
                    case 2:{
                        Intent intent3 = new Intent(Login.this,Chart.class);
                        startActivity(intent3);
                        break;
                    }
                    //My List
                    case  3:{
                        Intent intent4 = new Intent(Login.this,UserList.class);
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

        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable editable) {
                UserName = editable.toString();
            }
        });
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable editable) {
                UserEmail = editable.toString();
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                edit.putString("UserName",UserName);
                edit.putString("UserEmail",UserEmail);
                edit.commit();
                setResult(RESULT_OK);
                Intent intent = new Intent(Login.this,Search.class);
                startActivity(intent);
                //finish();
            }
        });
    }


}
