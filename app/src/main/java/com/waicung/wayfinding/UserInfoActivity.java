package com.waicung.wayfinding;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class UserInfoActivity extends AppCompatActivity {
    Button logout_btn;
    TextView tv_user_name;
    DBOpenHelper db = new DBOpenHelper(this);
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        logout_btn = (Button)findViewById(R.id.button_logout);
        tv_user_name = (TextView)findViewById(R.id.user_info_name);
        showInfo();

        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
                Intent intent = new Intent(UserInfoActivity.this, LoginActivity.class);
                UserInfoActivity.this.startActivity(intent);
            }
        });
    }

    private void showInfo() {
        Cursor cursor = db.getDataBy(db.PRIMARY_USER,"1");
        cursor.moveToFirst();
        //get the data by column name
        name = cursor.getString(cursor.getColumnIndex(db.USER_ID));
        //set to display
        tv_user_name.setText(name);
    }

    private void logout() {
        db.asPrimary(name, 0);

    }
}
