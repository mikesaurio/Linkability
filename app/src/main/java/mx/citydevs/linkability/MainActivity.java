package mx.citydevs.linkability;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;


public class MainActivity extends ActionBarActivity  {

    TextToSpeech ttobj;
    boolean action = false;
    String speaker = "";
    ArrayList<JobsBean> jobsArray = new ArrayList<JobsBean>();
    public static int MESSAGE_JOB = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        InitData();
        initUI();
    }

    /**
     *
     */
    public void initUI(){
        ttobj=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    ttobj.setLanguage(Locale.US);
                }
            }
        }
        );


        ((Button)findViewById(R.id.btn_whats_new)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new_messages();
            }
        });
        ((Button)findViewById(R.id.btn_call)).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                        callJOb();
                        return true;
            }
        });
        ((Button)findViewById(R.id.btn_next_notice)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next_message();
            }
        });
        ((Button)findViewById(R.id.btn_back_notice)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back_message();
            }
        });
    }

    public void new_messages(){
        if(jobsArray.size()>0){
            speakAndVibrate("You have "+jobsArray.size()+" New Messages", false);
        }else{
            speakAndVibrate("You have no more messages", false);
        }
    }

    public void next_message(){
        if(jobsArray.size()== 0 || jobsArray.size()-1 <= MESSAGE_JOB) {
            speakAndVibrate("You have no more messages", false);
            MESSAGE_JOB = jobsArray.size()-1;
        }else{
            MESSAGE_JOB += 1;

            speakAndVibrate("the Job is"+jobsArray.get(MESSAGE_JOB).getBusiness_name()+
                    " the Schedule is " + jobsArray.get(MESSAGE_JOB).getSchedule()
                    +" the direction is"+ jobsArray.get(MESSAGE_JOB).getDirection(), false);
        }
    }

    public void back_message(){
        if(jobsArray.size() == 0 || -1 >= MESSAGE_JOB) {
            speakAndVibrate("You have no more messages", false);
            MESSAGE_JOB = -1;
        }else{
            MESSAGE_JOB -= 1;
            speakAndVibrate("the Job is"+jobsArray.get(MESSAGE_JOB).getBusiness_name()+
                    " the Schedule is " + jobsArray.get(MESSAGE_JOB).getSchedule()+
                    " the direction is"+ jobsArray.get(MESSAGE_JOB).getDirection()
                    , false);

        }
    }


    public void callJOb(){
        try{
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + jobsArray.get(MESSAGE_JOB).getTelephone()));
            startActivity(intent);
        }catch(Exception e){

        }
    }

    /**
     *
     */
    public void InitData(){
       String jsonString =  createJson();
     if(createJson()!= null){
         try {
             JSONObject json = new JSONObject(jsonString);
             JSONArray jArray = json.getJSONArray("Jobs");
             for(int i=0; i<jArray.length(); i++){
                 JobsBean jb = new JobsBean();
                 JSONObject json_data = jArray.getJSONObject(i);
                 jb.setBusiness_name(json_data.getString("business_name"));
                 jb.setDirection(json_data.getString("direction"));
                 jb.setSchedule(json_data.getString("schedule"));
                 jb.setTelephone(json_data.getString("telephone"));
                 jobsArray.add(jb);
             }
         } catch (JSONException e) {
             e.printStackTrace();
         }
     }else{
         speaker = "";
     }
    }


    /**
     *
     * @return
     */
    public String createJson(){
        JSONArray jsonArray = new JSONArray();
        for(int i = 0; i <3; i++){
            JSONObject student = new JSONObject();
            try {
                student.put("business_name", "Name of Job"+ i+1);
                student.put("direction", "Polanco "+ i+1);
                student.put("schedule", "9 to 18");
                student.put("telephone", "5530265963");
                jsonArray.put(student);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        JSONObject studentsObj = new JSONObject();
        try {
            studentsObj.put("Jobs", jsonArray);
            return studentsObj.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     *
     * @param text
     * @param action
     */
    public void speakAndVibrate(String text, boolean action){
        this.action = action;

        ttobj.speak(text, TextToSpeech.QUEUE_FLUSH, null);

        Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(2000);

        if(action){
            finish();
        }
    }


    /**
     *
     */
    public void onPause(){
        if(ttobj !=null){
            ttobj.stop();
            ttobj.shutdown();
        }
        super.onPause();
    }

}
