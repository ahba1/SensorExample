package com.example.sensorexample.activity;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.ViewGroup;

import com.example.sensorexample.R;
import com.example.sensorexample.SensorApplication;
import com.example.sensorexample.databinding.ActivityMainBinding;
import com.example.sensorexample.sensor.SensorInfo;
import com.example.sensorexample.service.SensorBinder;
import com.example.sensorexample.service.SensorService;
import com.example.sensorexample.ui.SensorUIAdapter;
import com.nikhilpanju.recyclerviewenhanced.RecyclerTouchListener;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import cn.kevin.floatingeditor.EditorCallback;
import cn.kevin.floatingeditor.EditorHolder;
import cn.kevin.floatingeditor.FloatEditorActivity;

@SuppressLint("NonConstantResourceId")
@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class SensorActivity extends AppCompatActivity implements Contract.View {

    private Contract.Presenter presenter;

    private SensorBinder binder;

    private RecyclerTouchListener onTouchListener;

    private ActivityMainBinding binding;

    private void rvInit(){
        SensorUIAdapter adapter = new SensorUIAdapter(SensorInfo.getSensorNames());
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.rv.setLayoutManager(manager);
        binding.rv.setAdapter(adapter);
    }

    private void btnInit(){
        binding.btnCommit.setOnClickListener(v -> {
            String url = binding.etUrl.getText().toString();
            binder.bindUrl(url);
        });
    }

    private void listenerInit(){
        onTouchListener = new RecyclerTouchListener(this, binding.rv);
        onTouchListener.setSwipeOptionViews(R.id.add, R.id.edit, R.id.change)
                .setSwipeable(R.id.rowFG, R.id.rowBG, (viewID, position) -> {
                    switch (viewID){
                        case R.id.add:
                            presenter.startSensor(position);
                            break;
                        case R.id.change:
                            presenter.stopSensor(position);
                            break;
                        case R.id.edit:
                            EditorCallback callback = new EditorCallback() {
                                @Override
                                public void onCancel() {

                                }

                                @Override
                                public void onSubmit(String content) {
                                    int i = Integer.parseInt(content);
                                    presenter.setSamplingPeriodUs(position, i);
                                }

                                @Override
                                public void onAttached(ViewGroup rootView) {

                                }
                            };
                            FloatEditorActivity.openEditor(SensorActivity.this, callback,
                                    new EditorHolder(R.layout.fast_reply_floating_layout,//Custom layout
                                            R.id.tv_cancel, R.id.tv_submit, R.id.et_content));
                            break;
                    }
                });
    }

    public SensorActivity(){
        this.presenter = new SensorPresenter(this);
    }

    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (SensorBinder)service;
            presenter.setBinder(binder);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //视图绑定
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //初始化rv
        rvInit();

        //初始化button
        btnInit();

        //初始化swipe控件监听器
        listenerInit();

        //绑定service
        bindService(new Intent(this, SensorService.class), connection, BIND_AUTO_CREATE);

        binding.tvIp.setText(SensorApplication.ip);
    }

    @Override
    public void setPresenter(Contract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.rv.addOnItemTouchListener(onTouchListener);
        presenter.registerNetworkBoardCast();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        binding.rv.removeOnItemTouchListener(onTouchListener);
        presenter.unregisterNetworkBoardCast();
    }

    @Override
    public void onNetworkChanged(String newIp) {
        binding.tvIp.setText(newIp);
    }
}
