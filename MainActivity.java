package com.computerexpertzjamaica.j3mobile;

import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    dbhelper databasehelper = new dbhelper(this);
    Route routedata = new Route(this);

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button  mButton = findViewById(R.id.button2);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //get current date
                Date c = Calendar.getInstance().getTime();
                System.out.println("Current time => " + c);

                SimpleDateFormat df = new SimpleDateFormat();
                String formattedDate = df.format(c);

                // Create sample data
                UserMaster userdata = new UserMaster();
                userdata.fullname = "Jermaine Gray";
                userdata.company = "CPXZ";
                userdata.department = "Sales";
                userdata.emailaddress = "jgray@cpxz.us";
                userdata.password = "Pa#55word";
                userdata.pin = 1234;
                userdata.isactive = true;
                userdata.isreset = false;


                // Add sample post to the database
                databasehelper.addUser(userdata);

                //Route routeuser = new Route(this)
                routedata.fullname = "Jermaine Gray";
                routedata.routeno = "CPXZ";
                routedata.employeeid = "10000";



            }
        });



        /*// Get all posts from database
        List<User> posts = helper.addUser(userdata);
        for (Post post : posts) {
            // do something
        }*/

    }


}
