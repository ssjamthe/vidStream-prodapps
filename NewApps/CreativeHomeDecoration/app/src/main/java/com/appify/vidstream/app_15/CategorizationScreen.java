package com.appify.vidstream.app_15;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.appify.vidstream.adapter.CategorizationBaseAdapter;
import com.appify.vidstream.adapter.CategoryGridBaseAdapter;
import com.appify.vidstream.adapter.CategoryListBaseAdapter;
import com.appify.vidstream.constants.ApplicationConstants;
import com.appify.vidstream.control.AppController;
import com.appify.vidstream.model.CatZationModel;
import com.appify.vidstream.model.CategoriesModel;
import com.appify.vidstream.utility.AdManager;
import com.appify.vidstream.utility.CheckInternetConnection;
import com.appify.vidstream.utility.SSLManager;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.crash.FirebaseCrash;
import com.inmobi.ads.InMobiAdRequestStatus;
import com.inmobi.ads.InMobiBanner;
import com.inmobi.ads.InMobiInterstitial;
import com.inmobi.sdk.InMobiSdk;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class CategorizationScreen extends AppCompatActivity implements ApplicationConstants {

    //MAke change at 579 gridViewCategoriesText.setNumColumns(2);

    private static final String TAG = CategorizationScreen.class.getSimpleName();
    private InMobiBanner mBannerAd;
    private InMobiInterstitial mInterstitialAd;
    private InterstitialAd interstitial, banner;
    private RelativeLayout inMobiAdView;
    private AdView adView;
    private long minIntervalInterstitial;

    private  boolean flag;
    private ProgressDialog progressDialog;
    private List<CatZationModel> catZationModeList = new ArrayList<CatZationModel>();
    private CategorizationBaseAdapter catZationGridAdapter;
    private List<CategoriesModel> categoriesModeList = new ArrayList<CategoriesModel>();
    private CategoryGridBaseAdapter categoriGridBaseAdapter;
    private CategoryListBaseAdapter categoriListBaseAdapter;

    private CheckInternetConnection cic;
    private Boolean isInternetPresent = false;
    private String deviceID, showBanner, showAdMovingInside, noContentMessage;
    private String appBgImageUrl;
    private int ActivityNo=0, PrevActivityNo, PREVIOUS_SELECTED_CATEGORIZATION_ID;
    private String showInmobiAdWeightage, ActivityName;
    private Date installTime, updateTime;
    private Spinner categorization;
    private MenuItem listGridConvertor;
    private GridView gridViewCategoriesText;
    private LinearLayout mainCategorizationLinearLayout, PersonalizeLayout, adshowview, nocontentcatzlayout;
    private RelativeLayout catzation_Relative_Background;
    private TextView nocontentcatzText;

    private static final String PREFS_NAME = "CATZ_PREF";
    private static final String PREFS_KEY = "CATZ_PREF_PREV_ACTNO";
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private static final String PREFS_CATZ_NAME = "PREV_SELECTED_CATZ_PREF";
    private static final String PREFS_CATZ_KEY = "PREV_CATZ_PREF";
    private SharedPreferences CATZ_preferences;
    private SharedPreferences.Editor CATZ_editor;

    //AdManager
    private Random random = new Random();
    private Double randomNo = random.nextDouble() * 1.0;
    private AdManager adManager;
    private ProgressBar progressBar;

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.categorization_screen);

        //Fresco.initialize(this);
        try {
            InMobiSdk.setLogLevel(InMobiSdk.LogLevel.DEBUG);
            InMobiSdk.init(this, INMOBI_ACCOUNT_ID);
            //Sheared Preferences
            preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            CATZ_preferences = getSharedPreferences(PREFS_CATZ_NAME, Context.MODE_PRIVATE);
            adManager = new AdManager(CategorizationScreen.this);
            SSLManager.handleSSLHandshake(); //For SSL Request
        }catch (Exception e) {
            e.printStackTrace();
            FirebaseCrash.log("Exception in SSL Request:CategorizationScreen.java >"+e);
        }

        //Initialize View
        adshowview = (LinearLayout) findViewById(R.id.adshowview);
        categorization = (Spinner) findViewById(R.id.categorizationSpinner);
        gridViewCategoriesText = (GridView) findViewById(R.id.gridViewCategoriesText);
        inMobiAdView = (RelativeLayout) findViewById(R.id.inMobi_adView);
        progressBar = (ProgressBar) findViewById(R.id.catz_progressbar);
        catzation_Relative_Background = (RelativeLayout) findViewById(R.id.catzation_Relative);
        mainCategorizationLinearLayout = (LinearLayout) findViewById(R.id.CategorizationMainLinearLayout);
        PersonalizeLayout = (LinearLayout) findViewById(R.id.PersonalizeLayout);
        adView = (AdView) CategorizationScreen.this.findViewById(R.id.adView);
        catZationGridAdapter = new CategorizationBaseAdapter(this, catZationModeList);
        categoriGridBaseAdapter = new CategoryGridBaseAdapter(CategorizationScreen.this, categoriesModeList);
        categoriListBaseAdapter = new CategoryListBaseAdapter(CategorizationScreen.this, categoriesModeList);
        mainCategorizationLinearLayout.setVisibility(View.GONE);
        PersonalizeLayout.setVisibility(View.GONE);
        gridViewCategoriesText.setVisibility(View.GONE);
        catZationGridAdapter.clearCatzList();
        nocontentcatzlayout = (LinearLayout) findViewById(R.id.nocontentcatzlayout);
        nocontentcatzText = (TextView) findViewById(R.id.nocontentcatzText);
        nocontentcatzlayout.setVisibility(View.GONE);

        cic = new CheckInternetConnection(getApplicationContext());
        isInternetPresent = cic.isConnectingToInternet();
        if (!isInternetPresent) {
            noInternetPresent();
        }

        //ProgressDialog Call
        progressDialogCall();

        //getIntent
        try {
            ActivityName = getIntent().getStringExtra("ActivityName");
            Log.e("ActivityName>>>", ""+ ActivityName);
            if(ActivityName.equals("FirstLaunch")){
                adManager.createAdMobAds(); //Request for creating Ad.
                adManager.createInMobiInterstitial();
                flag = false; //for List to Grid
                long NewShowTime = new Date().getTime() / 1000;
                Log.e("AdShowTime = ", "" + NewShowTime);
                adManager.setNewTime(NewShowTime);
                //long GetShowTime = adManager.getNewTime(); //Get New Time From AdManager
                ActivityNo = 1;
                PrevActivityNo = 1;
                editor = preferences.edit();
                editor.putInt(PREFS_KEY, PrevActivityNo);
                editor.commit();
                Log.e("ActivityNo>>>", ""+ ActivityNo);
            }else if(ActivityName.equals("NoInternet")){
                adManager.createAdMobAds(); //Request for creating Ad.
                adManager.createInMobiInterstitial();
                flag = getIntent().getExtras().getBoolean("flag");
                System.out.println("Get flag from Intent>>> And set flag = "+ flag +";");
                long NewShowTime = new Date().getTime() / 1000;
                Log.e("AdShowTime = ", "" + NewShowTime);
                adManager.setNewTime(NewShowTime);
                //long GetShowTime = adManager.getNewTime(); //Get New Time From AdManager
                ActivityNo = 1;
                PrevActivityNo = 1;
                editor = preferences.edit();
                editor.putInt(PREFS_KEY, PrevActivityNo);
                editor.commit();
                Log.e("ActivityNo>>>", ""+ ActivityNo);
            }else{
                flag = getIntent().getExtras().getBoolean("flag");
                System.out.println("Get flag from Intent>>> And set flag = "+ flag +";");
                ActivityNo = getIntent().getIntExtra("ActivityNo",0);
                System.out.println("Get Categorization ActivityNo from Intent>>> And set ActivityNo = "+ ActivityNo +";");
                showBanner = getIntent().getStringExtra("showBanner");
                showInmobiAdWeightage = getIntent().getStringExtra("showInmobiAdWeightage");
                minIntervalInterstitial = getIntent().getLongExtra("minIntervalInterstitial",0);
                //for getting Interstitial Ad.
                interstitial = adManager.getAdMobAd();
                System.out.println("Catz AdMob interstitial="+interstitial);
                mInterstitialAd = adManager.getInMobiAd();
                System.out.println("Catz InMobi interstitial="+mInterstitialAd);

                //For showing interstitial ads
                ShowInterstitialBothAds();
                //Toast.makeText(CategorizationScreen.this, "After Launch", Toast.LENGTH_SHORT).show();
            }

        }catch (Exception e){
            e.printStackTrace();
            System.out.println("No Values For Intent>>>And set flag = false;");
            adManager.createAdMobAds(); //Request for creating Ad.
            adManager.createInMobiInterstitial();
            flag = false; //for List to Grid
            long NewShowTime = new Date().getTime() / 1000;
            Log.e("AdShowTime = ", "" + NewShowTime);
            adManager.setNewTime(NewShowTime);
            //long GetShowTime = adManager.getNewTime(); //Get New Time From AdManager
            ActivityNo = 1;
            PrevActivityNo = 1;
            editor = preferences.edit();
            editor.putInt(PREFS_KEY, PrevActivityNo);
            editor.commit();
            Log.e("ActivityNo>>>", ""+ ActivityNo);
            //Toast.makeText(CategorizationScreen.this, "First Launch = "+GetShowTime, Toast.LENGTH_SHORT).show();
            //FirebaseCrash.log("Exception in getIntent:CategorizationScreen.java >"+e);
        }

        //requestiong for categorizations
        LoadCategorizations();

    }
    /*******************************
     * End OnCreate
     **************************/

    private void ShowInterstitialBothAds(){
        try {
            if (ActivityNo != 0) {
                // Prepare the Banner and Interstitial Ad
                double showAdWeight = Double.parseDouble(showInmobiAdWeightage);
                if (showAdWeight > randomNo && (!(mInterstitialAd.equals(null)))) {
                    // InterstitialAd
                    try {
                        long CurrentTime = new Date().getTime() / 1000;
                        long AdShowTime = adManager.getNewTime(); //Get New Time From AdManager
                        long Duration = CurrentTime - AdShowTime;
                        System.out.println("InMobi Catz Duration = " + Duration);
                        //Toast.makeText(CategorizationScreen.this, "Catz Duration = " + Duration, Toast.LENGTH_SHORT).show();
                        if (Duration >= minIntervalInterstitial && PrevActivityNo != ActivityNo) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (mInterstitialAd != null) {
                                        mInterstitialAd.load();
                                        mInterstitialAd.show();
                                    }
                                    System.out.println("Inmobi after interstitial show =" + mInterstitialAd);
                                    //Toast.makeText(CategorizationScreen.this,"Inmobi after interstitial show()",Toast.LENGTH_SHORT).show();
                                    PrevActivityNo = ActivityNo;
                                    editor = preferences.edit();
                                    editor.putInt(PREFS_KEY, PrevActivityNo);
                                    editor.commit();
                                }
                            }, 1);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    //interstitialAd
                    try {
                        // Prepare the Interstitial Ad
                        long CurrentTime = new Date().getTime() / 1000;
                        long AdShowTime = adManager.getNewTime(); //Get New Time From AdManager
                        long Duration = CurrentTime - AdShowTime;
                        System.out.println("Admob Catz Duration = " + Duration);
                        if (Duration >= minIntervalInterstitial && PrevActivityNo != ActivityNo) {
                            if (interstitial.isLoaded()) {
                                interstitial.show();
                                PrevActivityNo = ActivityNo;
                                editor = preferences.edit();
                                editor.putInt(PREFS_KEY, PrevActivityNo);
                                editor.commit();
                            } else {
                                System.out.println("Interstitial ad failed to load");
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            if (!interstitial.isLoaded()) {
                adManager.createAdMobAds(); //Request for creating Ad.
            }
            if (!mInterstitialAd.isReady()) {
                adManager.createInMobiInterstitial(); //Request for creating Ad.
            }
        }catch (Exception e){
            e.printStackTrace();
            try{
                if (!interstitial.isLoaded()) {
                    adManager.createAdMobAds(); //Request for creating Ad.
                }
                if (!mInterstitialAd.isReady()) {
                    adManager.createInMobiInterstitial(); //Request for creating Ad.
                }
            }catch (Exception e1){
                e1.printStackTrace();
            }
        }
    }

    private void LoadCategorizations(){
        // first instalation Date-Time
        String inatallDateAndTime = "";
        PackageInfo packageInfo;
        try {
            PackageManager pm = getApplicationContext().getPackageManager();
            ApplicationInfo appInfo = pm.getApplicationInfo(APP_PACKAGE, 0);
            packageInfo = pm.getPackageInfo(APP_PACKAGE, PackageManager.GET_PERMISSIONS);
            String appFile = appInfo.sourceDir;
            installTime = new Date(packageInfo.firstInstallTime);
            Log.e("Installed>>>", installTime.toString());
            inatallDateAndTime = String.valueOf(installTime.toString());
            updateTime = new Date(packageInfo.lastUpdateTime);
            Log.e("Updated>>>", updateTime.toString());
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            FirebaseCrash.log("Exception in instalation Date-Time and PackageManager:CategorizationScreen.java >"+e);
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                inatallDateAndTime = dateFormat.format(date);
            }catch (Exception exx){
                exx.printStackTrace();
                inatallDateAndTime = "No installTime Found";
            }
        }

        //get IMEI No.
        try {
            final TelephonyManager mTelephony = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            if (mTelephony.getDeviceId() != null) {
                deviceID = mTelephony.getDeviceId();
                Log.e("IMEI>>>", deviceID);
            } else {
                deviceID = Secure.getString(getApplicationContext()
                        .getContentResolver(), Secure.ANDROID_ID);
                Log.e("Serial NO>>>", deviceID);
            }
        }catch (Exception e) {
            e.printStackTrace();
            FirebaseCrash.log("Exception in get IMEI No.:CategorizationScreen.java >"+e);
            deviceID = "00000000000000";
        }

// Categorization
// Spinner setAdapter
        try {
            categorization.setAdapter(catZationGridAdapter);
            @SuppressWarnings("deprecation")
            final String catzationspinurl = URL_IP_ADDRESS + URL_LOADAPP + "?appId=" + URLEncoder.encode(APP_ID) + "&installTimestamp=" + URLEncoder.encode(inatallDateAndTime) + "&deviceId=" + URLEncoder.encode(deviceID.toString());
            System.out.println("catzationspinurl = " + catzationspinurl);
            JsonObjectRequest spinRequest = new JsonObjectRequest(Request.Method.POST, catzationspinurl,
                    null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    // for categorization
                    try {
                        // for banner, mininterval, background.
                        showBanner = response.getString("showBanner");
                        Log.e("showBanner: ", showBanner);
                        showAdMovingInside = response.getString("showAdMovingInside");
                        Log.e("showAdMovingInside: ", showAdMovingInside);
                        showInmobiAdWeightage = response.getString("showInmobiAdWeightage");
                        Log.e("showInmobiAdWeightage: ", showInmobiAdWeightage);
                        minIntervalInterstitial = response.getLong("minIntervalInterstitial");
                        String minIntInterstitial = String.valueOf(minIntervalInterstitial);
                        Log.e("minInterval: ", minIntInterstitial);
                        try{
                            noContentMessage = response.getString("noContentMessage");
                        }catch (Exception excep){
                            System.out.println("Didnt Found noContentMessage and set DefaultNoContentMessage");
                            noContentMessage = DefaultNoContentMessage;
                        }

                        hidePDialog();
                        //Get Categorization

                        String catzresponse = response.toString();
                        Log.e("response >>>> ", ""+catzresponse);

                        if(catzresponse.contains("categorizations")){
                            JSONArray categorizationsArray = response.getJSONArray("categorizations");
                            Log.e("categorizationsArray.length() >>>> ", ""+categorizationsArray.length());
                            if(categorizationsArray.length() == 0){
                                mainCategorizationLinearLayout.setVisibility(View.GONE);
                                nocontentcatzlayout.setVisibility(View.VISIBLE);
                                nocontentcatzText.setText(noContentMessage);
                            }else {
                                for (int i = 0; i < categorizationsArray.length(); i++) {
                                    JSONObject categorizationsObject = categorizationsArray.getJSONObject(i);
                                    CatZationModel spinMod = new CatZationModel();
                                    try {
                                        spinMod.setCatZationName(categorizationsObject.getString("name"));
                                        spinMod.setCatZationId(categorizationsObject.getString("id"));
                                        catZationModeList.add(spinMod);
                                    }catch (Exception ed){
                                        Log.e("Null Categorization >>>> ", ""+ed);
                                        if(categorizationsArray.length() == 1){
                                            mainCategorizationLinearLayout.setVisibility(View.GONE);
                                            nocontentcatzlayout.setVisibility(View.VISIBLE);
                                            nocontentcatzText.setText(noContentMessage);
                                        }
                                    }
                                }
                            }
                        }else{
                            mainCategorizationLinearLayout.setVisibility(View.GONE);
                            nocontentcatzlayout.setVisibility(View.VISIBLE);
                            nocontentcatzText.setText(noContentMessage);
                        }

                        //For single categorization
                        Log.e("catZationModeList.size() >>>> ", ""+catZationModeList.size());
                        if (catZationModeList.size() == 1) {
                            mainCategorizationLinearLayout.setVisibility(View.VISIBLE);
                            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(0, 0);
                            mainCategorizationLinearLayout.setLayoutParams(layoutParams);
                            nocontentcatzlayout.setVisibility(View.GONE);
                        } else if(catZationModeList.size() > 1){
                            mainCategorizationLinearLayout.setVisibility(View.VISIBLE);
                            nocontentcatzlayout.setVisibility(View.GONE);
                        }

                        //Background image
                        appBgImageUrl = response.getString("appBgImageUrl");
                        Log.e("appBgImageUrl", appBgImageUrl);
                        try {
                            //new LoadImage().execute(appBgImageUrl);         //for backgroung image
                            if (appBgImageUrl != null) {
                                Picasso.with(CategorizationScreen.this).load(appBgImageUrl).into(new Target() {
                                    @Override
                                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                        catzation_Relative_Background.setBackgroundDrawable(new BitmapDrawable(bitmap));
                                    }

                                    @Override
                                    public void onBitmapFailed(Drawable errorDrawable) {

                                    }

                                    @Override
                                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                                    }
                                });
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        //For Banner
                        try {
                            if(showBanner.equalsIgnoreCase("false")){
                                adshowview.setVisibility(View.GONE);
                                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                                layoutParams.addRule(RelativeLayout.BELOW, R.id.Categorizationview1);
                                layoutParams.setMargins(3, 3, 3, 3);
                                gridViewCategoriesText.setLayoutParams(layoutParams);
                            }else {
                                adshowview.setVisibility(View.VISIBLE);
                                double showAdWeight = Double.parseDouble(showInmobiAdWeightage);
                                if (showAdWeight > randomNo) {
                                    if (showBanner.equalsIgnoreCase("true")) {
                                        showInMobiBanner();
                                    }
                                } else {
                                    if (showBanner.equalsIgnoreCase("true")) {
                                        showAdBanner();
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        catZationGridAdapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                        hidePDialog();
                        FirebaseCrash.log("Exception in onResponse catzationspinurl:CategorizationScreen.java >" + e);
                        nocontentcatzlayout.setVisibility(View.VISIBLE);
                        nocontentcatzText.setText(DefaultNoContentMessage);
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Spin Error: " + error.getMessage());
                    System.out.println("catzationspinurl errorMessage= " + error.getMessage());

                    try {
                        String ErrorMessage = error.getMessage();
                        String Keyword = "Authentication Failed";
                        if (ErrorMessage != null) {
                            if (ErrorMessage.contains(Keyword)) {
                                authenticationErrorDialog();
                            } else {
                                cantReachedDialog();
                            }
                        } else {
                            cantReachedDialog();
                        }
                        hidePDialog();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put(TOKEN_KEY, TOKEN_VALUE);
                    return headers;
                }
            };
            RequestQueue spinrequestQueue = Volley.newRequestQueue(CategorizationScreen.this);
            spinrequestQueue.add(spinRequest);
        }catch (Exception e) {
            e.printStackTrace();
            hidePDialog();
            /*try {
                Toast.makeText(CategorizationScreen.this, "No data found due to internet connection!", Toast.LENGTH_SHORT);
            }catch (Exception ed){
                ed.printStackTrace();
            }*/
            FirebaseCrash.log("Exception in calling catzationspinurl:CategorizationScreen.java >" + e);
        }

        //set selected spinner position
        try {
            if(catZationModeList.size()!=1)
            {
                if ((PREVIOUS_SELECTED_CATEGORIZATION_ID = CATZ_preferences.getInt(PREFS_CATZ_KEY, 0)) > 0) {
                    int PSELECTED_CATEGORIZATION_ID = (PREVIOUS_SELECTED_CATEGORIZATION_ID = CATZ_preferences.getInt(PREFS_CATZ_KEY, 0)) - 1;
                    categorization.setSelection(PSELECTED_CATEGORIZATION_ID);
                    System.out.println("SELECTED_CATEGORIZATION_ID - 1 = " + PREVIOUS_SELECTED_CATEGORIZATION_ID);
                } else {
                    System.out.println("No PREVIOUS_SELECTED_CATEGORIZATION_ID");
                }
            }else {
                System.out.println("Categorization size is 1.");
            }
        }catch (Exception e){
            e.printStackTrace();
            FirebaseCrash.log("Exception at fetching old categorization:CategorizationScreen.java >"+e);
        }

        //Categorization Spinner Item Selected Listener
        categorization.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View container,
                                       int position, long id) {
                try {
                    // Getting the Container Layout of the ListView
                    LinearLayout linearLayoutParent = (LinearLayout) container;
                    // Getting the inner Linear Layout if it is used :LinearLayout
                    // linearLayoutChild = (LinearLayout)
                    // linearLayoutParent.getChildAt(1); 1 indicate position
                    TextView tvSpinName = (TextView) linearLayoutParent.getChildAt(0);
                    String NOW_SELECTED_CATEGORIZATION_NAME = tvSpinName.getText().toString();

                    TextView tvSpinId = (TextView) linearLayoutParent.getChildAt(1);
                    String NOW_SELECTED_CATEGORIZATION_ID = tvSpinId.getText().toString();

                    if (NOW_SELECTED_CATEGORIZATION_NAME.equalsIgnoreCase(PERSONALIZED)){
                        gridViewCategoriesText.setVisibility(View.GONE);
                        PersonalizeLayout.setVisibility(View.VISIBLE);
                    }
                    else {
                        PersonalizeLayout.setVisibility(View.GONE);
                        gridViewCategoriesText.setVisibility(View.VISIBLE);
                        if (flag == false) {
                            gridViewCategoriesText.setAdapter(categoriGridBaseAdapter);
                            gridViewCategoriesText.setNumColumns(2);
                            listGridConvertor.setIcon(R.drawable.ic_action_view_as_list);
                            flag = false;
                        } else {
                            gridViewCategoriesText.setAdapter(categoriListBaseAdapter);
                            gridViewCategoriesText.setNumColumns(1);
                            listGridConvertor.setIcon(R.drawable.ic_action_view_as_grid);
                            flag = true;
                        }

                        //Requesting and calling method for getting categories
                        LoadCategories(APP_ID, NOW_SELECTED_CATEGORIZATION_ID, deviceID.toString());
                    }

                    int SELECTED_CATEGORIZATION_ID = categorization.getSelectedItemPosition();
                    System.out.println("SELECTED_CATEGORIZATION_ID = " + SELECTED_CATEGORIZATION_ID);
                    PREVIOUS_SELECTED_CATEGORIZATION_ID = SELECTED_CATEGORIZATION_ID + 1;
                    CATZ_editor = CATZ_preferences.edit();
                    CATZ_editor.putInt(PREFS_CATZ_KEY, PREVIOUS_SELECTED_CATEGORIZATION_ID);
                    CATZ_editor.commit();
                    System.out.println("SELECTED_CATEGORIZATION_ID + 1 = " + PREVIOUS_SELECTED_CATEGORIZATION_ID);

                }catch (Exception e) {
                    e.printStackTrace();
                    gridViewCategoriesText.setVisibility(View.GONE);
                    PersonalizeLayout.setVisibility(View.GONE);
                    nocontentcatzlayout.setVisibility(View.VISIBLE);
                    nocontentcatzText.setText(noContentMessage);
                    FirebaseCrash.log("Exception in categorization.setOnItemSelectedListener:CategorizationScreen.java >"+e);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        // GridView
        // Adding request to request queue
        //gridViewCategoriesText.setNumColumns(2);
        //listGrid.setImageResource(R.drawable.ic_action_view_as_list);
        gridViewCategoriesText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                try {
                    // Getting the Container Layout of the ListView
                    LinearLayout linearLayoutParent = (LinearLayout) view;
                    TextView cat_name_text = (TextView) linearLayoutParent.getChildAt(1);
                    String NOW_SELECTED_CATEGORY_NAME = cat_name_text.getText().toString();
                    TextView tvCategory = (TextView) linearLayoutParent.getChildAt(2);

                    PrevActivityNo = ActivityNo;
                    editor = preferences.edit();
                    editor.putInt(PREFS_KEY, PrevActivityNo);
                    editor.commit();
                    PrevActivityNo = preferences.getInt(PREFS_KEY, 0);
                    ActivityNo = ActivityNo + 1;
                    System.out.println("After Add 1 ActivityNo from Intent>>>And set ActivityNo = " + ActivityNo + ";");

                    String SelectedCategoryId = tvCategory.getText().toString();
                    System.out.println("SelectedCategoryId=" + SelectedCategoryId);
                    final ArrayList CatArray = new ArrayList();
                    CatArray.add(SelectedCategoryId);
                    System.out.println("CatArray=" + CatArray);
                    final ArrayList CatNameArray = new ArrayList();
                    CatNameArray.add(NOW_SELECTED_CATEGORY_NAME);
                    System.out.println("CatNameArray=" + CatNameArray);

                    Intent intentcat = new Intent(CategorizationScreen.this, CategoryScreen.class);
                    intentcat.putExtra("categoryID", CatArray);
                    intentcat.putExtra("CategoryName", CatNameArray);
                    intentcat.putExtra("noContentMessage",noContentMessage);
                    intentcat.putExtra("showBanner", showBanner);
                    intentcat.putExtra("showInmobiAdWeightage", showInmobiAdWeightage);
                    intentcat.putExtra("minIntervalInterstitial", minIntervalInterstitial);
                    System.out.println("minIntervalInterstitial=" + minIntervalInterstitial);
                    intentcat.putExtra("showAdMovingInside", showAdMovingInside);
                    intentcat.putExtra("deviceID", deviceID);
                    intentcat.putExtra("back_img", appBgImageUrl);
                    intentcat.putExtra("flag", flag);
                    intentcat.putExtra("ActivityNo", ActivityNo);
                    startActivity(intentcat);
                    CategorizationScreen.this.finish();
                }catch (Exception e) {
                    e.printStackTrace();
                    FirebaseCrash.log("Exception in  gridViewCategoriesText.setOnItemClickListener:CategorizationScreen.java >"+e);
                    /*try{
                        Toast.makeText(CategorizationScreen.this, "Please wait...\nOr try again...", Toast.LENGTH_SHORT).show();
                    }catch (Exception exx){
                        exx.printStackTrace();
                    }*/
                }
            }
        });
    }

    private void LoadCategories(String getappId, String getcategorizationId, String getdeviceId)
    {
        final String setappId = getappId;
        final String setcategorizationId = getcategorizationId;
        final String setdeviceId = getdeviceId;

        progressDialogCall();
        // Creating volley request obj getting Categories
        try {
        @SuppressWarnings("deprecation")
            final String categoriesurl = URL_IP_ADDRESS + URL_LOADCATEGORIES + "?appId=" + URLEncoder.encode(setappId) + "&categorizationId=" + URLEncoder.encode(setcategorizationId) + "&deviceId=" + URLEncoder.encode(setdeviceId);
            System.out.println("categoriesurl = " + categoriesurl);
            JsonObjectRequest categoriesRequest = new JsonObjectRequest(Request.Method.POST, categoriesurl, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject volleyResponse) {
                            // For Categories
                            try {
                                hidePDialog();
                                categoriGridBaseAdapter.clearCategoryGrid();
                                categoriListBaseAdapter.clearCategoryList();

                                JSONArray categoriesArray = volleyResponse.getJSONArray("categories");
                                System.out.println("categoriesArray.length()=" + categoriesArray.length());
                                if(categoriesArray.length() == 0){
                                    gridViewCategoriesText.setVisibility(View.GONE);
                                    nocontentcatzlayout.setVisibility(View.VISIBLE);
                                    nocontentcatzText.setText(noContentMessage);
                                }else {
                                    for (int i = 0; i < categoriesArray.length(); i++) {
                                        JSONObject categoryObject = categoriesArray.getJSONObject(i);
                                        CategoriesModel categoriesModel = new CategoriesModel();
                                        try {
                                            categoriesModel.setCatTitle(categoryObject.getString("name"));
                                            categoriesModel.setCatID(categoryObject.getString("id"));
                                            categoriesModel.setCatImage(categoryObject.getString("image"));
                                            System.out.println("Inside Cat: " + categoryObject.getString("name"));
                                            System.out.println("Inside Cat: " + categoryObject.getString("id"));
                                            System.out.println("Inside Cat: " + categoryObject.getString("image"));
                                            categoriesModeList.add(categoriesModel);
                                        }catch (Exception ed){System.out.println("Empty Category>>>"+ed);}
                                    }
                                }
                                categoriGridBaseAdapter.notifyDataSetChanged();
                                categoriListBaseAdapter.notifyDataSetChanged();
                                catZationGridAdapter.notifyDataSetChanged();
                            } catch (Exception e) {
                                e.printStackTrace();
                                hidePDialog();
                                FirebaseCrash.log("Exception at getting categories:CategorizationScreen.java >" + e);
                                gridViewCategoriesText.setVisibility(View.GONE);
                                nocontentcatzlayout.setVisibility(View.VISIBLE);
                                nocontentcatzText.setText(noContentMessage);
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    VolleyLog.d(TAG, "Cateegory fetching Error: " + volleyError.getMessage());
                    System.out.println("categoriesurl error= " + volleyError.getMessage());

                    try {
                        String ErrorMessage = volleyError.getMessage();
                        String Keyword = "Authentication Failed";
                        if (ErrorMessage != null) {
                            if (ErrorMessage.contains(Keyword)) {
                                authenticationErrorDialog();
                            } else {
                                cantReachedDialog();
                            }
                        } else {
                            cantReachedDialog();
                        }
                        hidePDialog();
                    } catch (Exception e) {
                        hidePDialog();
                        e.printStackTrace();
                    }
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put(TOKEN_KEY, TOKEN_VALUE);
                    return headers;
                }
            };
            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(categoriesRequest);
        }catch (Exception e) {
            e.printStackTrace();
            hidePDialog();
            FirebaseCrash.log("Exception at getting categories:CategorizationScreen.java >"+e);
        }
    }

    private void showAdBanner() {
        try{
            System.out.println("Call showAdBanner in catzation.");
            adView.setVisibility(View.VISIBLE);
            //adView.setAdSize(AdSize.SMART_BANNER);
            banner = new InterstitialAd(CategorizationScreen.this);
            banner.setAdUnitId(ADMOB_BANNER);
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .addTestDevice(APP_NAME).build();
            adView.loadAd(adRequest);
            progressBar.setVisibility(View.GONE);
            banner.loadAd(adRequest);
            banner.setAdListener(new AdListener() {
                public void onAdLoaded() {
                    if (banner.isLoaded()) {
                        progressBar.setVisibility(View.GONE);
                    }else {
                        Log.d("","Banner ad failed to load");
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            FirebaseCrash.log("Exception in showAdBanner():CategorizationScreen.java >"+e);
        }
    }

    private void showInMobiBanner() {
        try {
            System.out.println("Call showInMobiBanner in catzation.");
            mBannerAd = new InMobiBanner(CategorizationScreen.this, INMOBI_BANNER);
            inMobiAdView.setVisibility(View.VISIBLE);
            if (inMobiAdView != null) {

                mBannerAd.setAnimationType(InMobiBanner.AnimationType.ROTATE_HORIZONTAL_AXIS);
                mBannerAd.setListener(new InMobiBanner.BannerAdListener() {
                    @Override
                    public void onAdLoadSucceeded(InMobiBanner inMobiBanner) {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAdLoadFailed(InMobiBanner inMobiBanner,
                                               InMobiAdRequestStatus inMobiAdRequestStatus) {
                        Log.w(TAG, "Banner ad failed to load with error: " +
                                inMobiAdRequestStatus.getMessage());
                        showAdBanner();
                    }

                    @Override
                    public void onAdDisplayed(InMobiBanner inMobiBanner) {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAdDismissed(InMobiBanner inMobiBanner) {
                    }

                    @Override
                    public void onAdInteraction(InMobiBanner inMobiBanner, Map<Object, Object> map) {
                    }

                    @Override
                    public void onUserLeftApplication(InMobiBanner inMobiBanner) {
                    }

                    @Override
                    public void onAdRewardActionCompleted(InMobiBanner inMobiBanner, Map<Object, Object> map) {
                    }
                });

                int width = toPixelUnits(320);
                int height = toPixelUnits(50);
                RelativeLayout.LayoutParams bannerLayoutParams =
                        new RelativeLayout.LayoutParams(width, height);
                bannerLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                bannerLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                inMobiAdView.addView(mBannerAd, bannerLayoutParams);
                mBannerAd.load();
            }
        }catch (Exception e){
            e.printStackTrace();
            FirebaseCrash.log("Exception in showInMobiBanner():CategorizationScreen.java >"+e);
        }
    }

    private int toPixelUnits(int dipUnit) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dipUnit * density);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hidePDialog();
    }

    private void progressDialogCall() {
        progressDialog = new ProgressDialog(CategorizationScreen.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        try {
            progressDialog.show();
        }catch (Exception e){e.printStackTrace();}
    }

    private void hidePDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    private void authenticationErrorDialog(){
        AlertDialog.Builder builderError = new AlertDialog.Builder(
                CategorizationScreen.this);
        builderError
                .setTitle(Html.fromHtml("<font color='" + APP_THEME_COLOR + "'>" + AUTHENTICATION_ERROR + "</font>"))
                .setMessage(UPDATING_APP)
                .setCancelable(false)
                .setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(
                            DialogInterface arg0,
                            int arg1) {
                        CategorizationScreen.this.finish();
                    }
                });
        AlertDialog dialog = builderError.create();
        dialog.show();
    }

    private void cantReachedDialog(){
        AlertDialog.Builder builderError = new AlertDialog.Builder(
                CategorizationScreen.this);
        builderError
                .setTitle(Html.fromHtml("<font color='" + APP_THEME_COLOR + "'>" + CANTBEREACHED + "</font>"))
                .setMessage(TRYAGAIN)
                .setCancelable(false)
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(
                            DialogInterface arg0,
                            int arg1) {
                        CategorizationScreen.this.finish();
                    }
                })
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent intent = new Intent(CategorizationScreen.this, CategorizationScreen.class);
                        intent.putExtra("ActivityName", "NextLaunch");
                        intent.putExtra("flag",flag);
                        intent.putExtra("ActivityNo",ActivityNo);
                        intent.putExtra("showBanner", showBanner);
                        intent.putExtra("showInmobiAdWeightage",showInmobiAdWeightage);
                        intent.putExtra("minIntervalInterstitial",minIntervalInterstitial);
                        startActivity(intent);
                        CategorizationScreen.this.finish();
                    }
                });
        AlertDialog dialog = builderError.create();
        dialog.show();
    }

    private void noInternetPresent(){
        Intent intent = new Intent(CategorizationScreen.this,
                NoInternetScreen.class);
        intent.putExtra("flag",flag);
        startActivity(intent);
        CategorizationScreen.this.finish();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        try {
            return super.dispatchTouchEvent(event);
        }
        catch (Exception ignored){
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    CategorizationScreen.this);
            builder.setMessage(R.string.exit_alert_message)
                    .setCancelable(false)
                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .setPositiveButton(R.string.yes,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(
                                        DialogInterface dialogInterface, int i) {
                                    try {
                                        if(CATZ_editor != null){
                                            CATZ_editor.clear();
                                            CATZ_editor.commit();
                                        }
                                        if(editor != null){
                                            editor.clear();
                                            editor.commit();
                                        }
                                        CategorizationScreen.this.finish();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        CategorizationScreen.this.finish();
                                    }
                                }
                            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }catch (Exception e) {e.printStackTrace();}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_categorization_screen, menu);

        listGridConvertor = menu.findItem(R.id.list_grid_catzation_convertor);
        if(flag==false){
            listGridConvertor.setIcon(R.drawable.ic_action_view_as_list);
        }else{
            listGridConvertor.setIcon(R.drawable.ic_action_view_as_grid);
        }
        listGridConvertor.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem arg0) {
               try{
                    if (!flag) {
                        gridViewCategoriesText.setAdapter(categoriListBaseAdapter);
                        gridViewCategoriesText.setNumColumns(1);
                        listGridConvertor.setIcon(R.drawable.ic_action_view_as_grid);
                        flag = true;
                    } else {
                        gridViewCategoriesText.setAdapter(categoriGridBaseAdapter);
                        gridViewCategoriesText.setNumColumns(2);
                        listGridConvertor.setIcon(R.drawable.ic_action_view_as_list);
                        flag = false;
                    }
                }catch (Exception e){e.printStackTrace();}
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            case R.id.refresh:
                Intent ref = new Intent(CategorizationScreen.this,
                        CategorizationScreen.class);
                ref.putExtra("ActivityName", "NextLaunch");
                ref.putExtra("flag",flag);
                ref.putExtra("ActivityNo",ActivityNo);
                ref.putExtra("showBanner", showBanner);
                ref.putExtra("showInmobiAdWeightage",showInmobiAdWeightage);
                ref.putExtra("minIntervalInterstitial",minIntervalInterstitial);
                startActivity(ref);
                CategorizationScreen.this.finish();
                break;

            case R.id.action_feedback:
                Intent intent_fetback = new Intent(CategorizationScreen.this,FeedbackForm.class);
                intent_fetback.putExtra("back_img",appBgImageUrl);
                intent_fetback.putExtra("flag",flag);
                intent_fetback.putExtra("deviceID",deviceID);
                intent_fetback.putExtra("ActivityNo",ActivityNo);
                intent_fetback.putExtra("showBanner", showBanner);
                intent_fetback.putExtra("showInmobiAdWeightage",showInmobiAdWeightage);
                intent_fetback.putExtra("minIntervalInterstitial",minIntervalInterstitial);
                System.out.println("minIntervalInterstitial="+minIntervalInterstitial);
                startActivity(intent_fetback);
                CategorizationScreen.this.finish();
                break;

            case R.id.exit:
                onBackPressed();
                break;

            default:
                break;
        }
        return true;
    }

}