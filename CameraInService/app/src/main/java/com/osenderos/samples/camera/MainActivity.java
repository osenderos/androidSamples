package com.osenderos.samples.camera;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;

public class MainActivity extends Activity
    implements View.OnClickListener {
    private final static int WC=LinearLayout.LayoutParams.WRAP_CONTENT;
        
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        LinearLayout layout=new LinearLayout(this);
        layout.setBackgroundColor(Color.rgb(255,255,255));
        layout.setOrientation(LinearLayout.VERTICAL);
        setContentView(layout);         

        layout.addView(makeButton("Start","start"));
    }
 
    private Button makeButton(String text,String tag) {
        Button button=new Button(this);
        button.setTag(tag);
        button.setText(text);
        button.setOnClickListener(this); 
        button.setLayoutParams(new LinearLayout.LayoutParams(WC,WC));
        return button;        
    }  
    
    public void onClick(View v) {
        String tag=(String)v.getTag();
        if (tag.equals("start")) {
            if (isServiceRunning(
                "com.osenderos.samples.camera.MainService")) return;
            
            Intent intent=new Intent(this,
                com.osenderos.samples.camera.MainService.class);
            startService(intent);

        }
    }
    
    private boolean isServiceRunning(String className) {
        ActivityManager am=(ActivityManager)getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceInfos=
            am.getRunningServices(Integer.MAX_VALUE);
        for (int i=0;i<serviceInfos.size();i++) {
            if (serviceInfos.get(i).service.getClassName().equals(className)) {
                return true;
            }
        }
        return false;
    }
}
