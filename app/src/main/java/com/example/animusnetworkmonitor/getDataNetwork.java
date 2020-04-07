package com.example.animusnetworkmonitor;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.InetAddress;
import java.util.ArrayList;

public class getDataNetwork extends AsyncTask<String,String, Bitmap> {

public String ipAddress;
public String host;
public int myDatasetcount;
public TextView textView;
public ProgressBar pb;
public Context context;
    public ArrayList<String> desc_ArrayList = new ArrayList<>();
public ArrayList<String> status = new ArrayList<>();
public ArrayList<String> ip_ArrayList = new ArrayList<>();
public MainActivity m ;
public MyHelper helper;
SQLiteDatabase database;

    public getDataNetwork(String ipAddress, ProgressBar pb, TextView textView, Context context, MainActivity mainActivity, MyHelper helper, SQLiteDatabase database) {
        this.textView=textView;
        this.ipAddress=ipAddress;
        this.pb=pb;
        this.context=context;
        m=mainActivity;
        this.helper=helper;
        this.database=database;
    }

   // public MainActivity mainActivity = new MainActivity();
    public void print(String s){
//        TextView textView = mainActivity.findViewById(R.id.textView3);
        textView.append(s);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // Visible the progress bar and text view
        pb.setVisibility(View.VISIBLE);

    }

//    public void get_ip(String ipAddress, TextView textView){
//
//    }

    /**
     * Override this method to perform a computation on a background thread. The
     * specified parameters are the parameters passed to {@link #execute}
     * by the caller of this task.
     * <p>
     * This will normally run on a background thread. But to better
     * support testing frameworks, it is recommended that this also tolerates
     * direct execution on the foreground thread, as part of the {@link #execute} call.
     * <p>
     * This method can call {@link #publishProgress} to publish updates
     * on the UI thread.
     *
     * @param strings The parameters of the task.
     * @return A result, defined by the subclass of this task.
     * @see #onPreExecute()
     * @see #onPostExecute
     * @see #publishProgress
     */
    @Override
    protected Bitmap doInBackground(String... strings) {
        try {

            //final TextView textView = activity.findViewById(R.id.textView3);

            if (this.ipAddress !=null) {
                int first_point = (this.ipAddress.indexOf('.'));
                int second_point = (this.ipAddress.indexOf('.', first_point + 1));
                int third_point = (this.ipAddress.indexOf('.', second_point + 1));
                host = String.valueOf(this.ipAddress.subSequence(0, third_point));
                //print(" | host: " + host);
            }


            if (host!=null){
                int timeout=100;
                for (int i=1;i<25;i++){
                    //System.out.println(i+" line 67 "+address);
                    final String host1=host + "." + i;
                    //print(" aaja "+host);
                    if (InetAddress.getByName(host1).isReachable(timeout)){
                        //System.out.println(host1 + " is reachable");
                        //print(" lol "+host1);
                        ip_ArrayList.add(host1);
                        status.add("UP");
                        myDatasetcount++;

                                String desc="";
                                SNMPManager snmpManager = new SNMPManager(host1,m);

                                    if (!ipAddress.equals(host1)){
                                        try{
                                            desc=snmpManager.snmp_main();
                                        }
                                        catch (Error|Exception e){
                                            e.printStackTrace();
                                            print("getDataNetwork108 :"+e);
                                            continue;
                                        }
                                    }
                                    else {
                                        desc="Your Device";
                                    }

                        desc_ArrayList.add(desc);
                        helper.insertData(""+host1,desc,"UP",database);
                    }
                }
            }

        }
        catch (Exception | Error e){
            e.printStackTrace();
            print("error 163: "+e);
        }
//        mainActivity.initImageBitmaps();

        return null;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        pb.setVisibility(View.GONE);
        //m.updateListArray(ip_ArrayList,status);
        m.getData();

    }
}
