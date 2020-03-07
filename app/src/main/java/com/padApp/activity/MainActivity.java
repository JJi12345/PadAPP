package com.padApp.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.*;
import android.provider.SyncStateContract;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.*;
import com.amap.api.maps.utils.SpatialRelationUtil;
import com.amap.api.maps.utils.overlay.MovingPointOverlay;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.*;
import com.github.mikephil.charting.components.Description;

import interfaces.heweather.com.interfacesmodule.bean.Code;
import interfaces.heweather.com.interfacesmodule.bean.Lang;
import interfaces.heweather.com.interfacesmodule.bean.Unit;
import interfaces.heweather.com.interfacesmodule.bean.weather.now.Now;
import interfaces.heweather.com.interfacesmodule.bean.weather.now.NowBase;
import interfaces.heweather.com.interfacesmodule.view.HeConfig;
import interfaces.heweather.com.interfacesmodule.view.HeWeather;
import org.achartengine.GraphicalView;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.*;

import com.padApp.R;
import com.padApp.Service.LocationService;
import com.padApp.application.MainApplication;
import com.padApp.entityes.Criminal;
import com.padApp.entityes.Event;
import com.padApp.entityes.Task;
import com.padApp.utils.GetUseage;
import com.padApp.utils.OkHttpUtil;
import com.padApp.utils.WarnDialog;
import com.padApp.view.DeviceStateView;
import com.padApp.view.MyImageView;
import com.padApp.view.RiskChartView;
import com.padApp.utils.CommonUtil;
public class MainActivity extends AppCompatActivity{
    private TableRow tableRow ;
    private String userId = "130784";
    private String userName = "宋小玲";
    private String deviceNo = "100020003000";
    private MainApplication mainApplication;
    private String TAG = "mainActivity";
    private boolean STATUS = false;
    private  NowBase now;
    private String Result = "";
    private TextView latitude;
    private TextView longitude;
    private String latitude_v;
    private String longitude_v;
    private static final int REQUEST_LOGIN = 1;
    private MapView mapView;
    private AMap aMap;
    private DriveRouteResult mDriveRouteResult;
    private LatLonPoint mStartPoint = new LatLonPoint(39.959698, 116.300278);
    private LatLonPoint mEndPoint = new LatLonPoint(39.130527, 117.176994);
    private LatLng startPoint;
    private LatLng endPoint;
    private ProgressDialog progDialog = null;
    private DrivePath drivePath;
    private List<MovingPointOverlay> smoothMarkerList;
    private List<Marker> markerList;
    private int totalCarNum = 1;
    private int curNum = 0;
    private WarnDialog warnDialog;
    private List<LatLng> mLatLngsOfPath;
    private List<Marker> throughPointMarkerList = new ArrayList<Marker>();
    private Marker startMarker;
    private Marker endMarker;
    private float mWidth = 25; //路线宽度
    private boolean nodeIconVisible = true;
    private List<Marker> stationMarkers = new ArrayList<Marker>();
    private boolean isColorfulline = true;
    private PolylineOptions mPolylineOptions;
    private List<TMC> tmcs;
    private List<LatLonPoint> throughPointList;
    private List<Polyline> allPolyLines = new ArrayList<Polyline>();
    private PolylineOptions mPolylineOptionscolor = null;
    private Handler riskChartHandler;
    RiskChartView mService;
    LinearLayout riskChartLayout;
    private Handler eventTableHandler;
    private CreateUserDialog createUserDialog;
    private ArrayList<TableRow> rows = new ArrayList<>();
    private LocationService locationService;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate(MainActivity)");
        mainApplication = (MainApplication) getApplication();
        if(!mainApplication.isLogin()) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, LoginActivity.class);
            startActivityForResult(intent, REQUEST_LOGIN);
        } else {
            STATUS = true;
            setContentView(R.layout.activity_main);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            ActivityManager meManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            BatteryManager batManager = (BatteryManager) getSystemService(BATTERY_SERVICE);
            mainApplication = (MainApplication) getApplication();
            //bind service
            Intent intent = new Intent(this,LocationService.class);
            bindService(intent, conn, Context.BIND_AUTO_CREATE);

            mainApplication.setDeviceNo(deviceNo);
            mainApplication.setUserId(userId);
            mainApplication.setUserName(userName);
//        sharedPreferences = this.getSharedPreferences("PadApplication", Activity.MODE_PRIVATE);
//        _dataInterface=new DataAnalyser(this);
//        if(!mainApplication.isLogin()) {
//            Intent intent = new Intent();
//            intent.setClass(MainActivity.this, LoginActivity.class);
//            startActivityForResult(intent, REQUEST_LOGIN);
//        } else {
//            STATUS = true;
//            setContentView(R.layout.activity_main);
//            initBase();
//        }
            ButterKnife.bind(this);
            setCriminalId();
            setCriminalInfo();
            setTask();
            setAnomalousEvents();
            setRiskChart();
            try {
                init(savedInstanceState,meManager,batManager);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //返回一个MsgService对象
            locationService = ((LocationService.LocationBinder)service).getService();

        }
    };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void init(Bundle savedInstanceState, ActivityManager meManager, BatteryManager batManager) throws InterruptedException {
        Thread.sleep(2000);
        setMap(savedInstanceState);
        System.out.println("=criminal id="+mainApplication.getPrisonerId());
        System.out.println("=criminal info="+mainApplication.getCriminalInfo());
        System.out.println("=criminal anomalous event="+mainApplication.getEvent_c());
        System.out.println("=criminal task="+mainApplication.getTask_());
        System.out.println("=criminal risk="+mainApplication.getRisk());
//        setDeviceInfo(meManager,batManager);
        AnomalousEventTable();
//        setCriminalInfoTables();
//        topInfoBar();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setDeviceInfo(ActivityManager meManager, BatteryManager batManager){
        String url = "/deviceRunInfo/upload";

        String applicationTag = "POST device state info";
        HashMap<String, String> params = new HashMap<>(1);
        params.put("deviceNo",mainApplication.getDeviceNo());
        Handler location_handler = new Handler();
        Runnable runnable = new Runnable(){
            @Override
            public void run() {
                int cpu = GetUseage.getCPURateDesc_All();
                int  mem = GetUseage.getSysteTotalMemorySize(meManager);
                int battery = batManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
                params.put("cpuUsageRate",String.valueOf(cpu));
                params.put("memoryUsageRate",String.valueOf(mem));
                params.put("umpEnergyRate",String.valueOf(battery));

                DeviceStateView cpuV = findViewById(R.id.device_cpu);
                DeviceStateView memV = findViewById(R.id.device_storage);
                DeviceStateView batteryV = findViewById(R.id.device_ele);
                cpuV.setRealTimeValue(cpu);
                memV.setRealTimeValue(mem);
                batteryV.setRealTimeValue(battery);

//                postInfo(url,params,applicationTag);
                location_handler.postDelayed(this, 5000);
            }
        };
        location_handler.post(runnable);
    }

    //风险图
    private void setRiskChart(){
        riskChartLayout = findViewById(R.id.risk_chart_layout);
        RiskChartView mService=new RiskChartView(this);
        mService.setXYMultipleSeriesDataset("风险图");
        mService.setXYMultipleSeriesRenderer(100, "风险图", "时间（S）", "风险值");
        GraphicalView mView = mService.getGraphicalView();
        riskChartLayout.addView(mView, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));

        riskChartHandler=new Handler();
        riskChartHandler.postDelayed(new Runnable(){
            @Override
            public void  run(){
                setRiskValue();
                double degree = Double.valueOf(mainApplication.getRisk());
                mService.updateChart(degree);
                riskChartHandler.postDelayed(this, 1000);
            }
        },0);
    }

    @SuppressLint("ResourceType")
    private void AnomalousEventTable(){
    /**
     *      "id": "520",
     *     "prisonerId": "04006312",
     *     "prisonerName": "吉古伟机",
     *     "createAt": 1578215783000,
     *     "carNo": "京AWG392",
     *     "dealState": null,
     *     "riskValue": "88",
     *     "misdeclaration": false,
     *     "comment": null
     */ int index = 0;

        TableLayout table = findViewById(R.id.anomalous_table);
        Event event_ = new Event();
        List<Event> events = event_.String2Events(mainApplication.getEvent_c()).subList(0,10);
        for(Event event : events){
            index++;
            TableRow tableRow1 = new TableRow(getApplicationContext());
            rows.add(tableRow1);
            switch (index){
                case 1:
                    tableRow1.setId(R.id.row_1);
                    break;
                case 2:
                    tableRow1.setId(R.id.row_2);
                    break;
                case 3:
                    tableRow1.setId(R.id.row_3);
                    break;
                case 4:
                    tableRow1.setId(R.id.row_4);
                    break;
                case 5:
                    tableRow1.setId(R.id.row_5);
                    break;
                case 6:
                    tableRow1.setId(R.id.row_6);
                    break;
                case 7:
                    tableRow1.setId(R.id.row_7);
                    break;
                case 8:
                    tableRow1.setId(R.id.row_8);
                    break;
                case 9:
                    tableRow1.setId(R.id.row_9);
                    break;
                case 10:
                    tableRow1.setId(R.id.row_10);
                    break;

            }

//            tableRow1.setText(String.valueOf("第 " + childCount + " 个view"));
//            initAnimation(textView1, 1);
            String time = CommonUtil.getTimeFromTimeStamp(event.getTime());
            tableRow1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        Message msg = Message.obtain();
                        msg.obj = (Object) v;
                        tableRow = (TableRow) v;
                        eventTableHandler.sendMessage(msg);
                    }catch (Exception e){
                        System.out.println(e);
                    }
                }
            }
        );
        ArrayList<String> texts = new ArrayList<>();
        String state = event.getState();

        texts.add(time);
        texts.add(event.getPrisonerName());
        texts.add(event.getCarNo());
        texts.add(state);
        texts.add(event.getRiskValue());


        switch (state){
            case "未处理":
                tableRow1.setBackgroundColor(Color.parseColor("#A4AA0202"));
                break;
            case "已处理":
                tableRow1.setBackgroundColor(Color.parseColor("#C8628B34"));
                break;
            case "误报":
                tableRow1.setBackgroundColor(Color.parseColor("D2834E10"));
                break;
        }


        for(int i=0;i<texts.size();i++){
            TextView text = new TextView(getApplicationContext());
            text.setTextColor(Color.parseColor("#AEB0B9"));
//            text.setBackgroundColor(Color.parseColor("#A4AA0202"));
            text.setText(texts.get(i));
            text.setGravity(Gravity.CENTER);

            tableRow1.addView(text);
        }
            table.addView(tableRow1);
//        tableLayout1.addView(row);

        //要想在界面中实现数据添加后刷新，添加数据的代码要在Handler()函数中写。
            eventTableHandler=new Handler(){
            @Override
            public void handleMessage(Message msg){
                super.handleMessage(msg);
                View v = (View) msg.obj;
                showEditDialog(v);
            }
        };
        }
    }
//    //异常事件start
//
    /*异常事件 处理框*/
    private View.OnClickListener dialogListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            System.out.println(v.getId());
            switch (v.getId()) {

                case R.id.btn_save_pop:

                    String description = createUserDialog.accident_des.getText().toString().trim();
                    String state = createUserDialog.state.getText().toString().trim();
                    createUserDialog.dismiss();
                    TextView textView = (TextView) tableRow.getChildAt(3);

                    switch (state){
                        case "设为已处理":
                            tableRow.setBackgroundColor(Color.parseColor("#C8628B34"));
                            textView.setText("已处理");
                            /**
                             * 发送处理结结果
                             */
                            break;
                        case "设为误报":
                            tableRow.setBackgroundColor(Color.parseColor("#D2834E10"));
                            textView.setText("误报");
                            /**
                             * 发送处理结果
                             */
                            break;
                    }
                    /**
                     * * 取消监听
                     *  * 清空tablerow
                     */
//                    System.out.println(description+"——"+state);
                    break;

            case R.id.btn_exit_pop:
                createUserDialog.dismiss();
                break;
        }
        }
    };
    /*异常事件*/
    public void showEditDialog(View view) {
        createUserDialog = new CreateUserDialog(this,R.style.AppTheme, dialogListener);
        createUserDialog.show();
    }

//    //异常事件end
    /**
     * 信息bar
     */
    private void topInfoBar(){

        latitude = findViewById(R.id.latitude);
        longitude = findViewById(R.id.longitude);
        ImageView run_img = findViewById(R.id.run_img);
        ImageView walk_img = findViewById(R.id.walk_img);
        ImageView lie_img = findViewById(R.id.lie_img);

        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(2);
        TextView hight = findViewById(R.id.hight_text);
        TextView step = findViewById(R.id.step_text);
        TextView state = findViewById(R.id.state_text);
        TextView time1 =findViewById(R.id.time_t1);
        TextView time2 = findViewById(R.id.time_t2);
        TextView weather = findViewById(R.id.weather_type);
        TextView temp = findViewById(R.id.temp);
        Handler location_handler = new Handler();
        Runnable runnable = new Runnable(){
            @SuppressLint("SetTextI18n")
            @Override
            public void run(){
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH)+1;
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                time1.setText(year+"/"+month+"/"+day);
                time2.setText(hour+":"+minute    );
                if(locationService!=null){
                    Result = locationService.getLocation();
                }else {
                    Log.d(TAG,"未绑定");
                }

                System.out.println("===result"+Result);
                if (!(Result.isEmpty()&&Result.equals(""))) {
                    //经度:116.40 纬度:39.90
                    JSONObject jsonObj = JSON.parseObject(Result);
                    latitude_v = jsonObj.getString("latitude");
                    longitude_v = jsonObj.getString("longitude");
                    latitude.setText(latitude_v);
                    longitude.setText(longitude_v);
                    getWeatherFromHeAPI(
                            nf.format(Double.valueOf(longitude_v)),
                            nf.format(Double.valueOf(latitude_v))
                    );
                    if (now != null ) {
                        weather.setText(now.getCond_txt());
                        temp.setText(now.getTmp()+"°");
                    }

                    hight.setText(jsonObj.getString("height")+"M");
                    step.setText(jsonObj.getString("stepCounter")+"步");
                    String sta = jsonObj.getString("state");
                    switch (sta){
                        case "walk":
                            state.setText("走");
                            walk_img.setImageDrawable((getResources().getDrawable(R.drawable.walk_a)));
                            lie_img.setImageDrawable((getResources().getDrawable(R.drawable.tang_ina)));
                            run_img.setImageDrawable((getResources().getDrawable(R.drawable.run_ina)));
                            break;
                        case "run":
                            state.setText("跑");
                            run_img.setImageDrawable((getResources().getDrawable(R.drawable.run_a)));
                            walk_img.setImageDrawable((getResources().getDrawable(R.drawable.walk_ina)));
                            lie_img.setImageDrawable((getResources().getDrawable(R.drawable.tang_ina)));
                            break;
                        case "lie":
                            state.setText("躺");
                            lie_img.setImageDrawable((getResources().getDrawable(R.drawable.tang_a)));
                            run_img.setImageDrawable((getResources().getDrawable(R.drawable.run_ina)));
                            walk_img.setImageDrawable((getResources().getDrawable(R.drawable.walk_ina)));
                            break;
                        case "other":
                            state.setText("其他");
                            run_img.setImageDrawable((getResources().getDrawable(R.drawable.run_ina)));
                            walk_img.setImageDrawable((getResources().getDrawable(R.drawable.walk_ina)));
                            lie_img.setImageDrawable((getResources().getDrawable(R.drawable.tang_ina)));
                            break;
                    }

                }
                location_handler.postDelayed(this, 1000);
            }
        };
        location_handler.post(runnable);
    }

    private void setCriminalInfoTables(){
        Criminal c = new Criminal();
        @SuppressLint("HandlerLeak")
        Handler handler1 = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Criminal criminalInfo1 = (Criminal)msg.obj;

                MyImageView imageView = findViewById(R.id.criminal_photo);
                imageView.setImageURL(criminalInfo1.getPhoto());

                TextView name = findViewById(R.id.criminal_name);
                name.setText(criminalInfo1.getName());

                TextView age = findViewById(R.id.criminal_age);
                age.setText(criminalInfo1.getAge());

                TextView gender = findViewById(R.id.criminal_gender);
                gender.setText(criminalInfo1.getGender());

                TextView educationlevel = findViewById(R.id.criminal_edu);
                educationlevel.setText(criminalInfo1.getEducationLevel());

                TextView height = findViewById(R.id.criminal_height);
                height.setText(criminalInfo1.getHeight());

                TextView weight = findViewById(R.id.criminal_weight);
                weight.setText(criminalInfo1.getWeight());

                TextView bfr = findViewById(R.id.criminal_bfr);
                bfr.setText(criminalInfo1.getBfr());

                TextView crimes = findViewById(R.id.criminal_crimes);
                crimes.setText(criminalInfo1.getCrimes());

                TextView ex = findViewById(R.id.criminal_pre_existinge);
                ex.setText(criminalInfo1.getEx());

                TextView note = findViewById(R.id.criminal_note);
                note.setText(criminalInfo1.getNote());
            }
        };
        Message msg = new Message();
        msg.obj = c.String2Criminal(mainApplication.getCriminalInfo());
        handler1.sendMessage(msg);

    }

    /**  /////////////////////////////////////////////////////////////////////////
     *Map
     ///////////////////////////////////////////////////////////////////////// **/
    private void setMap(Bundle savedInstanceState){
        mapView = findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mainApplication = (MainApplication) getApplication();
        Log.i(TAG, "init(MapActivity)");

        if (aMap == null) {
            aMap = mapView.getMap();
        }
        RouteSearch routeSearch = new RouteSearch(this);
        routeSearch.setRouteSearchListener(new RouteSearch.OnRouteSearchListener() {
            @Override
            public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

            }

            @Override
            public void onDriveRouteSearched(DriveRouteResult result, int errorCode) {
                dissmissProgressDialog();
                aMap.clear();// 清理地图上的所有覆盖物
                if (errorCode == AMapException.CODE_AMAP_SUCCESS) {
                    if (result != null && result.getPaths() != null) {
                        if (result.getPaths().size() > 0) {
                            mDriveRouteResult = result;
                            drivePath = mDriveRouteResult.getPaths().get(0);
                            //设置节点marker是否显示
                            setNodeIconVisibility(false);
                            // 是否用颜色展示交通拥堵情况，默认true
                            setIsColorfulline(true);
                            removeFromMap();
                            addToMap();
                            zoomToSpan();
                            markerList = new ArrayList<>();
                            smoothMarkerList = new ArrayList<>();
                            for (int i = 0; i < totalCarNum; i++) {
                                markerList.add(aMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.mipmap.icon_car)).anchor(0.5f, 0.5f)));
                                smoothMarkerList.add(new MovingPointOverlay(aMap, markerList.get(markerList.size() - 1)));
                            }
                            LatLng drivePoint = mLatLngsOfPath.get(0);
                            Pair<Integer, LatLng> pair = SpatialRelationUtil.calShortestDistancePoint(mLatLngsOfPath, drivePoint);
                            mLatLngsOfPath.set(pair.first, drivePoint);
                            for (int i = 0; i  < smoothMarkerList.size(); i++){
                                smoothMarkerList.get(i).setPoints(mLatLngsOfPath.subList(pair.first + i*4, mLatLngsOfPath.size()));
                                smoothMarkerList.get(i).setTotalDuration(1000 - i*10);
                            }
                            for (MovingPointOverlay sm : smoothMarkerList){
                                sm.startSmoothMove();
                            }
                            // 设置  自定义的InfoWindow 适配器
                            aMap.setInfoWindowAdapter(infoWindowAdapter);
                            // 显示 infowindow
                            markerList.get(0).showInfoWindow();
                            String cName = getCriminal().getName();
                            String uName = mainApplication.getUserName();
                            String carNo = "";
                            if(!getTask().equals(null)){
                                carNo = getTask().getCarNo();
                            }
                            // 设置移动的监听事件  返回 距终点的距离  单位 米
                            String finalCName = cName;
                            String finalCarNo = carNo;
                            smoothMarkerList.get(0).setMoveListener(new MovingPointOverlay.MoveListener() {
                                @SuppressLint("SetTextI18n")
                                @Override
                                public void move(double v) {
                                    runOnUiThread(() -> {
                                        if(title != null){

                                            title.setText( "距离终点还有： " + (int) v + "米");
                                            prisoner_icon.setImageResource(R.mipmap.prisoner_6);
                                            prisoner_name.setText(cName);
                                            police_name.setText(uName);
                                            car_no.setText(finalCarNo);


                                        }
                                    });
                                }
                            });
                        } else if (result.getPaths() == null) {
                            Toast.makeText(getApplicationContext(), R.string.no_result, Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.no_result,Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "errorCode：" + errorCode, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {

            }

            @Override
            public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

            }
        });
        RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(mStartPoint, mEndPoint);
        RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo, 2, null, null, "");
        routeSearch.calculateDriveRouteAsyn(query);
    }
    private void showMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
    private void showDialog() {
        if(warnDialog == null) {
            warnDialog = WarnDialog.showDialog(this, "");
        }
        warnDialog.show();
    }
    private void destroyDialog() {
        if(warnDialog != null) {
            warnDialog.dismiss();
        }
    }

    /**
     *  个性化定制的信息窗口视图的类
     *  如果要定制化渲染这个信息窗口，需要重载getInfoWindow(Marker)方法。
     *  如果只是需要替换信息窗口的内容，则需要重载getInfoContents(Marker)方法。
     */
    private TextView title, prisoner_name, police_name, car_no;
    private ImageView prisoner_icon;
    private AMap.InfoWindowAdapter infoWindowAdapter = new AMap.InfoWindowAdapter(){
        // 个性化Marker的InfoWindow 视图
        // 如果这个方法返回null，则将会使用默认的信息窗口风格，内容将会调用getInfoContents(Marker)方法获取
        @Override
        public View getInfoWindow(Marker marker) {
            View infoWindow = getLayoutInflater().inflate(R.layout.custom_info_window,null);
            render(infoWindow);
            return infoWindow;
        }
        // 这个方法只有在getInfoWindow(Marker)返回null 时才会被调用
        // 定制化的view 做这个信息窗口的内容，如果返回null 将以默认内容渲染
        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }
    };
    @SuppressLint("SetTextI18n")
    private void render(View view) {
        //如果想修改自定义Infow中内容，请通过view找到它并修改
        title = view.findViewById(R.id.title);
        prisoner_icon = view.findViewById(R.id.escort_image);
        prisoner_name = view.findViewById(R.id.prisoner_name);
        police_name = view.findViewById(R.id.police_name);
        car_no = view.findViewById(R.id.car_no);
        title.setText("距离终点还有： " + " " + "米");
        prisoner_icon.setImageResource(R.mipmap.escort_dog);
        prisoner_name.setText("");
        police_name.setText("");
        car_no.setText("");
    }

    public void showPreCarInfo(View view){
        if(aMap!=null && markerList.size()!= 0){
            curNum = (curNum + totalCarNum+1)%totalCarNum;
            markerList.get(curNum).showInfoWindow();
        }
    }

    public void showNextCarInfo(View view){
        if(aMap!=null && markerList.size()!= 0){
            curNum = (curNum + totalCarNum-1)%totalCarNum;
            markerList.get(curNum).showInfoWindow();
        }
    }

    /**
     * 隐藏进度框
     */
    private void dissmissProgressDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }

    private void removeFromMap() {
        try {
            if (startMarker != null) {
                startMarker.remove();
            }
            if (endMarker != null) {
                endMarker.remove();
            }
            for (Marker marker : stationMarkers) {
                marker.remove();
            }
            for (Polyline line : allPolyLines) {
                line.remove();
            }
            if (this.throughPointMarkerList != null
                    && this.throughPointMarkerList.size() > 0) {
                for (Marker marker : this.throughPointMarkerList) {
                    marker.remove();
                }
                this.throughPointMarkerList.clear();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void zoomToSpan() {
        if (mStartPoint != null) {
            if (aMap == null){
                return;
            }
            try {
                LatLngBounds bounds = getLatLngBounds();
                aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    private LatLngBounds getLatLngBounds() {
        LatLngBounds.Builder b = LatLngBounds.builder();
        b.include(new com.amap.api.maps.model.LatLng(mStartPoint.getLatitude(), mStartPoint.getLongitude()));
        b.include(new LatLng(mEndPoint.getLatitude(), mEndPoint.getLongitude()));
        return b.build();
    }

    private void addStationMarker(MarkerOptions options) {
        if(options == null) {
            return;
        }
        Marker marker = aMap.addMarker(options);
        if(marker != null) {
            stationMarkers.add(marker);
        }
    }

    /**
     * 路段节点图标控制显示接口。
     * @param visible true为显示节点图标，false为不显示。
     * @since V2.3.1
     */
    private void setNodeIconVisibility(boolean visible) {
        try {
            nodeIconVisible = visible;
            if (stationMarkers != null && stationMarkers.size() > 0) {
                for (Marker stationMarker : stationMarkers) {
                    stationMarker.setVisible(visible);
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void setIsColorfulline(boolean iscolorfulline) {
        this.isColorfulline = iscolorfulline;
    }

    /**
     * 添加驾车路线添加到地图上显示。
     */
    private void addToMap() {
        initPolylineOptions();
        try {
            if (aMap == null) {
                return;
            }
            if (mWidth == 0 || drivePath == null) {
                return;
            }
            mLatLngsOfPath = new ArrayList<LatLng>();
            tmcs = new ArrayList<TMC>();
            List<DriveStep> drivePaths = drivePath.getSteps();
            startPoint = convertToLatLng(mStartPoint);
            endPoint = convertToLatLng(mEndPoint);
            mPolylineOptions.add(startPoint);
            for (DriveStep step : drivePaths) {
                List<LatLonPoint> latlonPoints = step.getPolyline();
                List<TMC> tmclist = step.getTMCs();
                tmcs.addAll(tmclist);
                addDrivingStationMarkers(step, convertToLatLng(latlonPoints.get(0)));
                for (LatLonPoint latlonpoint : latlonPoints) {
                    mPolylineOptions.add(convertToLatLng(latlonpoint));
                    mLatLngsOfPath.add(convertToLatLng(latlonpoint));
                }
            }
            mPolylineOptions.add(endPoint);
            if (startMarker != null) {
                startMarker.remove();
                startMarker = null;
            }
            if (endMarker != null) {
                endMarker.remove();
                endMarker = null;
            }
            addStartAndEndMarker();
            addThroughPointMarker();
            if (isColorfulline && tmcs.size()>0 ) {
                colorWayUpdate(tmcs);
            }else {
                addPolyLine(mPolylineOptions);
            }

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据不同的路段拥堵情况展示不同的颜色
     * @param tmcSection is
     */
    private void colorWayUpdate(List<TMC> tmcSection) {
        if (aMap == null) {
            return;
        }
        if (tmcSection == null || tmcSection.size() <= 0) {
            return;
        }
        TMC segmentTrafficStatus;
        addPolyLine(new PolylineOptions().add(startPoint,
                convertToLatLng(tmcSection.get(0).getPolyline().get(0)))
                .setDottedLine(true));
        String status = "";
        for (int i = 0; i < tmcSection.size(); i++) {
            segmentTrafficStatus = tmcSection.get(i);
            List<LatLonPoint> mployline = segmentTrafficStatus.getPolyline();
            if (status.equals(segmentTrafficStatus.getStatus())) {
                for (int j = 1; j < mployline.size(); j++) {
                    //第一个点和上一段最后一个点重复，这个不重复添加
                    mPolylineOptionscolor.add(convertToLatLng(mployline.get(j)));
                }
            }else {
                if (mPolylineOptionscolor != null) {
                    addPolyLine(mPolylineOptionscolor.color(getColor(status)));
                }
                mPolylineOptionscolor = null;
                mPolylineOptionscolor = new PolylineOptions().width(mWidth);
                status = segmentTrafficStatus.getStatus();
                for (LatLonPoint latLonPoint : mployline) {
                    mPolylineOptionscolor.add(convertToLatLng(latLonPoint));
                }
            }
            if (i == tmcSection.size()-1 && mPolylineOptionscolor != null) {
                addPolyLine(mPolylineOptionscolor.color(getColor(status)));
                addPolyLine(new PolylineOptions().add(
                        convertToLatLng(mployline.get(mployline.size()-1)), endPoint)
                        .setDottedLine(true));
            }
        }
    }

    private int getColor(String status) {
        switch (status) {
            case "畅通":
                return Color.GREEN;
            case "缓行":
                return Color.YELLOW;
            case "拥堵":
                return Color.RED;
            case "严重拥堵":
                return Color.parseColor("#990033");
            default:
                return Color.parseColor("#537edc");
        }
    }

    private void addPolyLine(PolylineOptions options) {
        if(options == null) {
            return;
        }
        Polyline polyline = aMap.addPolyline(options);
        if(polyline != null) {
            allPolyLines.add(polyline);
        }
    }

    private void addThroughPointMarker() {
        if (this.throughPointList != null && this.throughPointList.size() > 0) {
            LatLonPoint latLonPoint;
            for (LatLonPoint lonPoint : this.throughPointList) {
                latLonPoint = lonPoint;
                if (latLonPoint != null) {
                    boolean throughPointMarkerVisible = true;
                    throughPointMarkerList.add(aMap
                            .addMarker((new MarkerOptions())
                                    .position(
                                            new LatLng(latLonPoint
                                                    .getLatitude(), latLonPoint
                                                    .getLongitude()))
                                    .visible(throughPointMarkerVisible)
                                    .icon(getThroughPointBitDes())
                                    .title("\u9014\u7ECF\u70B9")));
                }
            }
        }
    }

    private BitmapDescriptor getThroughPointBitDes() {
        return BitmapDescriptorFactory.fromResource(R.mipmap.amap_through);
    }

    private void addStartAndEndMarker() {
        startMarker = aMap.addMarker((new MarkerOptions())
                .position(startPoint).icon(getStartBitmapDescriptor())
                .title("\u8D77\u70B9"));
        endMarker = aMap.addMarker((new MarkerOptions()).position(endPoint)
                .icon(getEndBitmapDescriptor()).title("\u7EC8\u70B9"));
    }

    /**
     * 给起点Marker设置图标，并返回更换图标的图片。如不用默认图片，需要重写此方法。
     * @return 更换的Marker图片。
     * @since V2.1.0
     */
    private BitmapDescriptor getStartBitmapDescriptor() {
        return BitmapDescriptorFactory.fromResource(R.mipmap.amap_start);
    }
    private BitmapDescriptor getEndBitmapDescriptor() {
        return BitmapDescriptorFactory.fromResource(R.mipmap.amap_end);
    }
    private BitmapDescriptor getDriveBitmapDescriptor() {
        return BitmapDescriptorFactory.fromResource(R.mipmap.amap_car);
    }

    private void addDrivingStationMarkers(DriveStep driveStep, LatLng latLng) {
        addStationMarker(new MarkerOptions()
                .position(latLng)
                .title("\u65B9\u5411:" + driveStep.getAction()
                        + "\n\u9053\u8DEF:" + driveStep.getRoad())
                .snippet(driveStep.getInstruction()).visible(nodeIconVisible)
                .anchor(0.5f, 0.5f).icon(getDriveBitmapDescriptor()));
    }

    private static LatLng convertToLatLng(LatLonPoint latLonPoint) {
        return new LatLng(latLonPoint.getLatitude(), latLonPoint.getLongitude());
    }

    private void initPolylineOptions() {
        mPolylineOptions = null;
        mPolylineOptions = new PolylineOptions();
        mPolylineOptions.color(Color.parseColor("#537edc")).width(18f);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult(Map)");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart(Map)");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy(Map)");
        if(smoothMarkerList.size() > 0) {
            for (MovingPointOverlay movingPointOverlay : smoothMarkerList) {
                if (movingPointOverlay != null) {
                    movingPointOverlay.setMoveListener(null);
                    movingPointOverlay.destroy();
                }
            }
        }
        mapView.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause(Map)");
        mapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume(Map)");
        mapView.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "onRestart(Map)");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }


    /**
     * 上传信息
     * @param url 服务器地址
     * @param params 上传参数
     * @param activityTag 上传活动标签
     */
    private void postInfo(String url, HashMap<String, String> params,String activityTag) {
        OkHttpUtil.getInstance(getBaseContext()).requestAsyn(url, OkHttpUtil.TYPE_POST_FORM, params, new OkHttpUtil.ReqCallBack<String>() {
            @Override
            public void onReqSuccess(String result) {
                String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                Log.i(TAG, activityTag+result+currentDateTimeString);
            }

            @Override
            public void onReqFailed(String errorMsg) {
                String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                Log.e(TAG, activityTag+errorMsg+currentDateTimeString);

            }
        });
    }


    private void setRiskValue(){
        HashMap<String, String> params = new HashMap<>(1);
        params.put("PrisonerId",mainApplication.getPrisonerId());
        String url = "/prisonerData/get";
        String activityTag = "get criminal risk value from service" ;
        OkHttpUtil.getInstance(getBaseContext()).requestAsyn(url, OkHttpUtil.TYPE_GET, params, new OkHttpUtil.ReqCallBack<String>() {
            @Override
            public void onReqSuccess(String result) {
                String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());

                if(!result.isEmpty()){
                    Log.i(TAG,activityTag+ result + currentDateTimeString);
                    if(!result.isEmpty()){
                        JSONObject jsonObj = JSON.parseObject(result);
                        mainApplication.setRisk(jsonObj.getString("riskValue"));
                    }
                }
            }

            @Override
            public void onReqFailed(String errorMsg)
            {
                String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                Log.e(TAG, activityTag+errorMsg+currentDateTimeString);
            }
        });

    }
    private void setCriminalId(){
        HashMap<String, String> params = new HashMap<>(1);

        params.put("userId", mainApplication.getUserId() );
        String url = "/task/getByUser";

        String activityTag = "get criminalId from service";
        OkHttpUtil.getInstance(getBaseContext()).requestAsyn(url, OkHttpUtil.TYPE_GET, params, new OkHttpUtil.ReqCallBack<String>() {
            @Override
            public void onReqSuccess(String result) {
                String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());

                if(!result.isEmpty()){
                    Log.i(TAG,activityTag+ result + currentDateTimeString);
                    String id = result;
                    mainApplication.setPrisonerId(id);
                }
            }

            @Override
            public void onReqFailed(String errorMsg)
            {
                String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                Log.e(TAG, activityTag+errorMsg+currentDateTimeString);
            }
        });
    }
    private void setCriminalInfo(){

        Criminal criminalInfos = new Criminal();
        HashMap<String, String> params = new HashMap<>(1);

        params.put("prisonerId",mainApplication.getPrisonerId());
        String url = "/prisoners/get";
        String activityTag = "get criminal information from service" ;
        OkHttpUtil.getInstance(getBaseContext()).requestAsyn(url, OkHttpUtil.TYPE_GET, params, new OkHttpUtil.ReqCallBack<String>() {
            @Override
            public void onReqSuccess(String result) {
                String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());

                if(!result.isEmpty()){
                    Log.i(TAG,activityTag+ result + currentDateTimeString);
                    if(!result.isEmpty()){
                        JSONObject jsonObj = JSON.parseObject(result);

                        criminalInfos.setPhoto(jsonObj.getString("prisonerPhotoUri"));
                        criminalInfos.setName(jsonObj.getString("prisonerName"));
                        criminalInfos.setAge(jsonObj.getString("age"));
                        criminalInfos.setGender(jsonObj.getString("gender"));
                        criminalInfos.setEducationLevel(jsonObj.getString("educationBackground"));
                        criminalInfos.setHeight(jsonObj.getString("height"));
                        criminalInfos.setWeight(jsonObj.getString("weight"));
                        criminalInfos.setBfr(jsonObj.getString("bodyFatRate"));
                        criminalInfos.setCrimes(jsonObj.getString("crime"));
                        criminalInfos.setEx(jsonObj.getString("criminalRecord"));
                        criminalInfos.setNote(jsonObj.getString("comment"));

                        mainApplication.setCriminalInfo(criminalInfos);
                    }
                }
            }

            @Override
            public void onReqFailed(String errorMsg)
            {
                String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                Log.e(TAG, activityTag+errorMsg+currentDateTimeString);
            }
        });

    }
    private Criminal getCriminal(){
        Criminal criminalInfos = new Criminal();
        criminalInfos = criminalInfos.String2Criminal(mainApplication.getCriminalInfo());
        return criminalInfos;
    }

    private Task getTask(){
        Task task_ = new Task();
        task_ = task_.String2Task(mainApplication.getTask_()) ;
        return task_;
    }
    private void setTask(){
        Task task_ = new Task();
        HashMap<String, String> params = new HashMap<>(1);

        params.put("userName",mainApplication.getUserName());
        String url = "/task/deviceGetTasks";
        String activityTag = "get user task from service";
        OkHttpUtil.getInstance(getBaseContext()).requestAsyn(url, OkHttpUtil.TYPE_GET, params, new OkHttpUtil.ReqCallBack<String>() {
            @Override
            public void onReqSuccess(String result) {
                String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());

                if(!result.isEmpty()){
                    Log.i(TAG,activityTag+ result + currentDateTimeString);
                    JSONObject jsonObj = JSON.parseObject(result);
                    JSONObject jsonT = JSON.parseObject(jsonObj.getString("task"));

                    task_.setUserName(jsonT.getString("userName"));
                    task_.setTaskNo(jsonT.getString("taskNo"));
                    task_.setPrisonerName(jsonT.getString("prisonerName"));
                    task_.setCarNo(jsonT.getString("carNo"));
                    task_.setLevel(jsonT.getString("level"));
                    task_.setDetail(jsonT.getString("detail"));

                    mainApplication.setTask_(task_);
                }
            }

            @Override
            public void onReqFailed(String errorMsg)
            {
                String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                Log.e(TAG, activityTag+errorMsg+currentDateTimeString);
            }
        });

    }
    private void setAnomalousEvents() {
        ArrayList<Event> eventlist = new ArrayList<>();
        HashMap<String, String> params = new HashMap<>(1);
        params.put("prisonerId", mainApplication.getPrisonerId());
        String url ="/exceptions/getException";
        String activityTag = "get anomalous event from service";
        OkHttpUtil.getInstance(getBaseContext()).requestAsyn(url, OkHttpUtil.TYPE_GET, params, new OkHttpUtil.ReqCallBack<String>() {
            @Override
            public void onReqSuccess(String result) {
                String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());

                if(!result.isEmpty()){
                    Log.i(TAG,activityTag+ result + currentDateTimeString);
                    List<Event> list = JSON.parseArray(result, Event.class);
                    for (Event event_ : list) {
                        if (event_.getMisdeclaration()) {
                            event_.setState("误报");
                        } else if (event_.getDealState() == null) {
                            event_.setState("未处理");
                        } else {
                            event_.setState("已处理");
                        }
                        eventlist.add(event_);
                    }
                    mainApplication.setEvents(eventlist);

                }
            }

            @Override
            public void onReqFailed(String errorMsg)
            {
                String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                Log.e(TAG, activityTag+errorMsg+currentDateTimeString);
            }
        });


    }
    /** //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
     *  天气 获取
     * @param longitude_ is
     * @param latitude_ is
     */
    private void getWeatherFromHeAPI(String longitude_, String latitude_){
        /*  在这里填入上面的username和key  */
        HeConfig.init("HE2002151946381825", "1aa9d204d8e5431896914ec51bb2582d");
        HeConfig.switchToFreeServerNode();
        HeWeather.getWeatherNow(MainActivity.this,longitude_+","+latitude_,
                Lang.CHINESE_SIMPLIFIED , Unit.METRIC ,
                new HeWeather.OnResultWeatherNowBeanListener() {
                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "Weather Now onError: ", e);
                    }

                    @Override
                    public void onSuccess(Now dataObject) {
                        /*  下面打印出来获得的json数据  */
                        //先判断返回的status是否正确，当status正确时获取数据，若status不正确，可查看status对应的Code值找到原因
                        if ( Code.OK.getCode().equalsIgnoreCase(dataObject.getStatus()) ){
                            //此时返回数据
                            Log.i(TAG, "//weather//"+"get data successful");
                            /* 此时now就是获得的数据类 , 这是和风SDK的自定义类  */
                            now = dataObject.getNow();

                        } else {
                            //在此查看返回数据失败的原因
                            String status = dataObject.getStatus();
                            Code code = Code.toEnum(status);
                            Log.e(TAG, "//weather//"+"failed code: " + code);
                        }
                    }
                });
    }

}

