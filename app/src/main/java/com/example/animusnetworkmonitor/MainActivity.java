package com.example.animusnetworkmonitor;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {

    public RecyclerView recyclerView;
    public RecyclerView.Adapter mAdapter;
    public RecyclerView.LayoutManager layoutManager;
    public int myDatasetcount;
    public ArrayList<String> images_ArrayList = new ArrayList<>();
    public ArrayList<String> status_ArrayList = new ArrayList<>();
    public ArrayList<String> ip_ArrayList = new ArrayList<>();
    public ArrayList<String> desc_ArrayList = new ArrayList<>();
    public String ipAddress;
    public SQLiteDatabase database;
    public MyHelper helper=new MyHelper(this);

    public TextView textView;
//*********************Print function To update TextView3 ***************************
    public void print(String s){
        try{textView.append(s);}
        catch (Error|Exception e){
            //print("MainActivity41: "+e);
            e.printStackTrace();
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView3);
        database = helper.getWritableDatabase();
        TextView textView = findViewById(R.id.textView3);
        final ProgressBar pb = (ProgressBar) findViewById(R.id.pb);
        //***********************Get Current IP****************************
        WifiManager wm = (WifiManager) this.getSystemService(WIFI_SERVICE);
        if(wm==null)
            print("49 wm Khaali hai\n");
        ipAddress = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        if(ipAddress.equals("0.0.0.0"))
        {
            Toast.makeText(this,"Turn ON the Wifi beach!\nand\n Restart Application",Toast.LENGTH_LONG).show();
            return;
        }
        print(""+ipAddress);

        //******************************************DATABASE**********************************************

        //clear existing data to get new data every time app opens
       database.delete("DEVICES","",new String[]{});
//        //insert
//        helper.insertData("jam","uyfybkjb","52.1",database);
//        getData();
        //******************************************DATABASE**********************************************
        //*******************************Async call to getData
        final MainActivity mainActivity = this;
        getDataNetwork asyncTask=new getDataNetwork(ipAddress,pb,textView,this,mainActivity,helper,database);
        asyncTask.execute();


        //***************************button************************************
        Button button = findViewById(R.id.getip_button);
        button.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View v) {
                                          Intent intent =  getBaseContext().getPackageManager().getLaunchIntentForPackage(mainActivity.getPackageName());
                                          intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                          startActivity(intent);
                                      }
                                  }
        );
        //***************************button************************************
    }//onCreate

    //******************************************DATABASE**********************************************
    void getData(){
        //getData
        Cursor cursor = database.rawQuery("SELECT _ID,IP,DESCRIPTION,STATUS FROM DEVICES",new String[]{});

        if(cursor!=null)
            cursor.moveToFirst();

        StringBuilder stringBuilder = new StringBuilder();
        //textView.append("Row Count:"+cursor.getCount()+"\n");
        do {
            int id = cursor.getInt(0);
            String ip = cursor.getString(1);
            String desc = cursor.getString(2);
            String status = cursor.getString(3);
            stringBuilder.append("\nID:").append(id).append(" IP").append(ip).append(" DESC:").append(desc).append(" STATUS:").append(status);
            images_ArrayList.add("https://cdn.pixabay.com/photo/2020/03/19/04/58/coconut-trees-4946270_960_720.jpg");
            ip_ArrayList.add(ip);
            status_ArrayList.add(status);
            desc_ArrayList.add(desc);
            myDatasetcount++;
        }while (cursor.moveToNext());
        cursor.close();
        //textView.append(stringBuilder.toString());
        initImageBitmaps();
    }

    int updateData(ContentValues values,String whereClause,String clauseValue){
        //update
        int l=database.update("DEVICES",values,whereClause,new String[]{clauseValue});
        getData();
        return l;
    }
    //******************************************DATABASE**********************************************
    //Dont delete this method updateListArray()
    public void updateListArray(ArrayList<String> ipAddress,ArrayList<String> status){
        this.ip_ArrayList=ipAddress;
        this.status_ArrayList =status;
        //getData();
//        print("IP:"+ip_ArrayList.toString()+"\nStatus:"+status.toString());
    }

    public void initImageBitmaps(){

//        images_ArrayList.add("https://images.unsplash.com/photo-1583681196525-cfe27381ab09?ixlib=rb-1.2.1&auto=format&fit=crop&w=668&q=80");
//        images_ArrayList.add("https://cdn.pixabay.com/photo/2020/03/19/04/58/coconut-trees-4946270_960_720.jpg");
//        images_ArrayList.add("https://images.unsplash.com/photo-1583681196525-cfe27381ab09?ixlib=rb-1.2.1&auto=format&fit=crop&w=668&q=80");
//        images_ArrayList.add("https://images.pexels.com/photos/1493226/pexels-photo-1493226.jpeg?auto=compress&cs=tinysrgb&h=650&w=940");

        initRecyclerView();
    }

     public void initRecyclerView(){

        recyclerView = (RecyclerView) findViewById(R.id.ip_RecyclerView);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        MyAdapter adapter = new MyAdapter(this,ip_ArrayList, desc_ArrayList,status_ArrayList, images_ArrayList,myDatasetcount);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        keepIpStatusCheck();
    }

    private void keepIpStatusCheck() {
        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                print("IP MAJAMA");
            }
        },10000);
    }

}
