package com.example.sensorexample.activity;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.sensorexample.R;
import com.example.sensorexample.SensorApplication;
import com.example.sensorexample.databinding.ActivityMainBinding;
import com.example.sensorexample.sensor.SensorInfo;
import com.example.sensorexample.service.SensorBinder;
import com.example.sensorexample.service.SensorService;
import com.example.sensorexample.ui.SensorUIAdapter;
import com.nikhilpanju.recyclerviewenhanced.RecyclerTouchListener;
import com.zyao89.view.zloading.ZLoadingDialog;

import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cn.kevin.floatingeditor.EditorCallback;
import cn.kevin.floatingeditor.EditorHolder;
import cn.kevin.floatingeditor.FloatEditorActivity;

import static com.zyao89.view.zloading.Z_TYPE.SINGLE_CIRCLE;

@SuppressLint("NonConstantResourceId")
@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class SensorActivity extends AppCompatActivity implements Contract.View {

    private Contract.Presenter presenter;

    private SensorBinder binder;

    private RecyclerTouchListener onTouchListener;

    private ActivityMainBinding binding;

    private ItemEventHandler itemChangedHandler;

    public final static int WS_OPEN = 513;

    public final static int WS_CLOSED = 614;

    private void rvInit(){
        SensorUIAdapter adapter = new SensorUIAdapter(new ArrayList<>(SensorInfo.getSensorNames()));
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.rv.setLayoutManager(manager);
        binding.rv.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.set(0, 0, 0, 30);
            }
        });
        binding.rv.setAdapter(adapter);
    }

    private void btnInit(){
        binding.btnCommit.setChecked(false);
        binding.btnCommit.setOnCheckedChangeListener((view, isChecked) -> {
            if (isChecked){
                String url = binding.etUrl.getText().toString();
                ZLoadingDialog zLoadingDialog = new ZLoadingDialog(SensorActivity.this);
                zLoadingDialog.setLoadingBuilder(SINGLE_CIRCLE)
                        .setLoadingColor(Color.BLACK)
                        .setHintText("connecting...")
                        .setHintTextSize(16)
                        .setHintTextColor(Color.GRAY)
                        .setDurationTime(0.5)
                        .show();
                binder.bindUrl(url, new SensorBinder.OnBindUrlListener() {
                    @Override
                    public void onSuccess() {
                        zLoadingDialog.cancel();
                    }

                    @Override
                    public void onFailure() {
                        zLoadingDialog.cancel();
                    }

                }, itemChangedHandler);
            }else{
                binder.unBind();
            }
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
        itemChangedHandler = new ItemEventHandler(this);
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

    @Override
    public void onDataTransmitting(String type) {
        int pos = ((SensorUIAdapter) Objects.requireNonNull(binding.rv.getAdapter())).getPosByType(type);
        BaseViewHolder viewHolder = (BaseViewHolder)binding.rv.getChildViewHolder(binding.rv.getChildAt(pos));
        viewHolder.setTextColor(R.id.mainText, Color.BLACK);
    }

    @Override
    public void onDataTransmissionStopped(String type) {
        int pos = ((SensorUIAdapter) Objects.requireNonNull(binding.rv.getAdapter())).getPosByType(type);
        BaseViewHolder viewHolder = (BaseViewHolder)binding.rv.getChildViewHolder(binding.rv.getChildAt(pos));
        viewHolder.setTextColor(R.id.mainText, Color.WHITE);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        View focusView = this.getCurrentFocus();
        if (hideInputWhenTouchOtherView(focusView, event)){
            InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager!=null){
                inputMethodManager.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
            }
        }
        return super.dispatchTouchEvent(event);
    }

    public boolean hideInputWhenTouchOtherView(View focusView, MotionEvent event){
        if (event.getAction() ==  MotionEvent.ACTION_DOWN){

            if (focusView instanceof EditText){
                int[] leftTop = {0, 0};
                //获取输入框当前的location位置
                focusView.getLocationInWindow(leftTop);
                int left = leftTop[0];
                int top = leftTop[1];
                int bottom = top + focusView.getHeight();
                int right = left + focusView.getWidth();
                return !(event.getX() > left && event.getX() < right
                        && event.getY() > top && event.getY() < bottom);
            }
        }
        return false;
    }
    public static class ItemEventHandler extends Handler{

        public SensorActivity context;

        public ItemEventHandler(SensorActivity context){
            this.context = context;
        }

        @Override
        public void handleMessage(Message msg) {
            if(msg.what==WS_OPEN){
                String type = (String)msg.obj;
                context.onDataTransmitting(type);
                Log.v("ws open", type);
            }else if(msg.what==WS_CLOSED){
                String type = (String)msg.obj;
                context.onDataTransmissionStopped(type);
            }
        }
    }
}
