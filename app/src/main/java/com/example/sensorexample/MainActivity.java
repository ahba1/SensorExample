//package com.example.sensorexample;
//
//import androidx.annotation.RequiresApi;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import android.content.ComponentName;
//import android.content.Intent;
//import android.content.ServiceConnection;
//import android.hardware.SensorEvent;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.IBinder;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//
//import com.example.sensorexample.sensor.SensorInfo;
//import com.example.sensorexample.sensor.SensorListenerWrapper;
//import com.example.sensorexample.service.SensorBinder;
//import com.example.sensorexample.service.SensorService;
//import com.example.sensorexample.ui.SensorUIAdapter;
//import com.example.sensorexample.ws.WSHandler;
//import com.nikhilpanju.recyclerviewenhanced.OnActivityTouchListener;
//import com.nikhilpanju.recyclerviewenhanced.RecyclerTouchListener;
//
//import java.util.ArrayList;
//
//@RequiresApi(api = Build.VERSION_CODES.KITKAT)
//public class MainActivity extends AppCompatActivity{
//
//    private RecyclerView recyclerView;
//
//    private EditText et_url;
//
//    private Button commit;
//
//    private Button startService;
//
//    private Button stopService;
//
//    private SensorBinder binder;
//
//    private OnActivityTouchListener touchListener;
//
//    private RecyclerTouchListener onTouchListener;
//
//    private ServiceConnection connection = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            binder = (SensorBinder)service;
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//
//        }
//    };
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        init();
//        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
//        SensorUIAdapter adapter = new SensorUIAdapter(new ArrayList<>(SensorInfo
//                .getSensorNames()));
//        recyclerView.setLayoutManager(manager);
//        recyclerView.setAdapter(adapter);
//        bindService(new Intent(this, SensorService.class), connection, BIND_AUTO_CREATE);
//    }
//.
//
//        recyclerView = findViewById(R.id.rv);
//        et_url = findViewById(R.id.et_url);
//        commit = findViewById(R.id.btn_commit);
//        //startService = findViewById(R.id.btn_start_service);
//        //stopService = findViewById(R.id.btn_stop_service);
//
//        commit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String url = et_url.getText().toString();
//                WSHandler.bind(url);
//            }
//        });
//
//        startService.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, SensorService.class);
//                bindService(intent, connection, BIND_AUTO_CREATE);
//            }
//        });
//
//        stopService.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                unbindService(connection);
//            }
//        });
//
//        onTouchListener = new RecyclerTouchListener(this, recyclerView);
//        onTouchListener.setSwipeOptionViews(R.id.add, R.id.edit, R.id.change)
//                .setSwipeable(R.id.rowFG, R.id.rowBG, new RecyclerTouchListener.OnSwipeOptionsClickListener() {
//                    @Override
//                    public void onSwipeOptionClicked(int viewID, int position) {
//                        switch (viewID){
//                            case R.id.add:
//                                startSensor(position);
//                                break;
//                            case R.id.change:
//                                stopSensor(position);
//                                break;
//                        }
//                    }
//                });
//    }
//
//    private String gen(SensorEvent event){
//        float[] values = event.values;
//        StringBuffer buffer = new StringBuffer();
//        for (float v:values){
//            buffer.append(v).append(",");
//        }
//        return buffer.deleteCharAt(buffer.length()-1).toString();
//    }
//
//    @Override
//    protected void onResume(){
//        super.onResume();
//        recyclerView.addOnItemTouchListener(onTouchListener);
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        recyclerView.removeOnItemTouchListener(onTouchListener);
//    }
//
//    private void stopSensor(int pos){
//        String type = new ArrayList<>(SensorInfo.getSensorNames()).get(pos).toLowerCase();
//        binder.sleep(type);
//        WSHandler.close(type);
//    }
//    private void startSensor(int pos){
//        String type = new ArrayList<>(SensorInfo.getSensorNames()).get(pos).toLowerCase();
//        WSHandler.connect(type);
//        Log.v("type", type);
//        startService(type);
//    }
//
//    private void startService(final String type){
//        binder.active(type, new SensorListenerWrapper() {
//            @Override
//            public void onSensorChanged(String msg) {
//                binder.setSensorMsg(type, msg);
//            }
//        });
//    }
//}