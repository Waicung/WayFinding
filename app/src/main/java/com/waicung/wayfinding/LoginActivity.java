package com.waicung.wayfinding;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.waicung.wayfinding.config.ConfigHandler;
import com.waicung.wayfinding.webclient.LoginAsyncTask;

import java.util.concurrent.ExecutionException;

/**
 * The Activity for user login
 * Back to  MainActivity if success
 */

public class LoginActivity extends AppCompatActivity {
    Button login_btn, cancel_btn;
    EditText et_user, et_password;
    TextView status;
    String api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        login_btn = (Button)findViewById(R.id.button_login);
        cancel_btn = (Button)findViewById(R.id.button_cancel);
        et_user = (EditText)findViewById(R.id.editText_user);
        et_password = (EditText)findViewById(R.id.editText_password);
        status = (TextView)findViewById(R.id.textView_status);

        ConfigHandler config = new ConfigHandler(this, "config");
        api = config.getApi("Login");

        login_btn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                String result="";
                String username = et_user.getText().toString();
                String password = et_password.getText().toString();
                //log in
                try {
                    result = new LoginAsyncTask(LoginActivity.this).execute(username, password).get();

                }catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                //check if successful
                if(result == getString(R.string.login_success)){
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    LoginActivity.this.startActivity(intent);
                }
                if(result == getString(R.string.connection_error)){

                }
                finish();

            }
        });

        cancel_btn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                finish();
            }
        });



    }


}
