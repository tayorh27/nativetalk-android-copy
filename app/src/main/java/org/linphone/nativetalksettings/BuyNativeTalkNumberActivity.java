package org.linphone.nativetalksettings;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.nk.ntalk.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.linphone.AppConfig;
import org.linphone.DataStore.AppData;
import org.linphone.dialer.DialerActivity;
import org.linphone.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;

public class BuyNativeTalkNumberActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView ivBack;
    Utils utils;
    AppData data;
    Spinner countrySpin, numberSpin, areaSpin, periodSpin;
    Button buyN;

    boolean isSelectableCountries = false;
    boolean isSelectableArea = false;

    //Countries
    ArrayList<Integer> countryID = new ArrayList<>();
    ArrayList<String> countryName = new ArrayList<>();
    ArrayList<String> countryCode = new ArrayList<>();
    ArrayList<String> countryPhoneCode = new ArrayList<>();
    ArrayList<String> countryMonthlyFee = new ArrayList<>();

    //Area
    ArrayList<Integer> areaID = new ArrayList<>();
    ArrayList<Integer> countryAreaId = new ArrayList<>();
    ArrayList<String> areaName = new ArrayList<>();
    ArrayList<String> areaCode = new ArrayList<>();

    //Number
    ArrayList<Integer> numberID = new ArrayList<>();
    ArrayList<String> numberName = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_native_talk_number);

        ivBack = findViewById(R.id.back);

        utils = new Utils(this);
        data = new AppData(this);
        ivBack = findViewById(R.id.back);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                //LinphoneActivity.instance().displayEmpty();
            }
        });
        countrySpin = findViewById(R.id.countriesSpinner);
        numberSpin = findViewById(R.id.numbersSpinner);
        areaSpin = findViewById(R.id.areasSpinner);
        periodSpin = findViewById(R.id.SpinnerPeroid);
        buyN = findViewById(R.id.BtnBuyNumber);

        countrySpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isSelectableCountries)
                    GetLocalAreas(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        areaSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isSelectableArea)
                    GetNumbers(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        buyN.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        utils.displayDialog("Please wait...");
        String url = AppConfig.API_URL + "/VS.WebAPI.Admin/json/syncreply/admin.did.local.country.list";
        JSONObject jsonObject = new JSONObject();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String res = utils.MakeHttpsApiCalls(url, jsonObject.toString());
//                    if(getActivity() == null){
//                        return;
//                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            utils.dismissDialog();
                            try {
                                JSONObject object = new JSONObject(res);
                                JSONArray jsonArray = object.getJSONArray("data");
                                if (jsonArray.length() > 0) {
                                    countryID.clear();
                                    countryName.clear();
                                    countryMonthlyFee.clear();
                                    countryCode.clear();
                                    countryPhoneCode.clear();
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject js = jsonArray.getJSONObject(i);
                                        countryID.add(js.getInt("id"));
                                        countryName.add(js.getString("name"));
                                        countryMonthlyFee.add(js.getString("monthlyFee"));
                                        countryCode.add(js.getString("code"));
                                        countryPhoneCode.add(js.getString("phoneCode"));
                                    }
                                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(BuyNativeTalkNumberActivity.this, android.R.layout.simple_list_item_1, countryName);
                                    countrySpin.setAdapter(arrayAdapter);
                                    isSelectableCountries = true;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (IOException e) {
                    utils.dismissDialog();
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void GetLocalAreas(int position) {
        //utils.displayDialog("Please wait...");
        String url = AppConfig.API_URL + "/VS.WebAPI.Admin/json/syncreply/admin.did.local.areas.list";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("countryId", countryID.get(position));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String res = utils.MakeHttpsApiCalls(url, jsonObject.toString());
//                    if(getActivity() == null){
//                        return;
//                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            utils.dismissDialog();
                            try {
                                JSONObject object = new JSONObject(res);
                                JSONArray jsonArray = object.getJSONArray("data");
                                if (jsonArray.length() > 0) {
                                    areaID.clear();
                                    countryAreaId.clear();
                                    areaName.clear();
                                    areaCode.clear();
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject js = jsonArray.getJSONObject(i);
                                        areaID.add(js.getInt("id"));
                                        countryAreaId.add(js.getInt("countryId"));
                                        areaName.add(js.getString("name"));
                                        areaCode.add(js.getString("code"));
                                    }
                                    if (areaName.isEmpty()) {
                                        areaName.add("Not Available");
                                    }
                                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(BuyNativeTalkNumberActivity.this, android.R.layout.simple_list_item_1, areaName);
                                    areaSpin.setAdapter(arrayAdapter);
                                    isSelectableArea = true;

                                    ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<>(BuyNativeTalkNumberActivity.this, android.R.layout.simple_list_item_1, new String[]{"₦" + countryMonthlyFee.get(0) + "/month"});
                                    periodSpin.setAdapter(arrayAdapter2);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (IOException e) {
                    utils.dismissDialog();
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void GetNumbers(int position) {
        //utils.displayDialog("Please wait...");
        String url = AppConfig.API_URL + "/VS.WebAPI.Admin/json/syncreply/admin.did.local.number.list";
        //{"areaId":0,"countryId":0,"number":"String", "pageOffset":0,"pageSize":0}
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("areaId", areaID.get(position));
            jsonObject.put("countryId", countryAreaId.get(position));
            jsonObject.put("number", "");
            jsonObject.put("pageOffset", 0);
            jsonObject.put("pageSize", 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String res = utils.MakeHttpsApiCalls(url, jsonObject.toString());
//                    if(getActivity() == null){
//                        return;
//                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            utils.dismissDialog();
                            try {
                                JSONObject object = new JSONObject(res);
                                JSONArray jsonArray = object.getJSONArray("data");
                                if (jsonArray.length() > 0) {
                                    numberID.clear();
                                    numberName.clear();
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject js = jsonArray.getJSONObject(i);
                                        if (js.getString("status").equals("Available") || js.getString("status").equals("Canceled")) {
                                            numberID.add(js.getInt("id"));
                                            numberName.add(js.getString("number"));
                                        }
                                    }
                                    if (numberName.isEmpty()) {
                                        numberName.add("Not Available");
                                    }
                                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(BuyNativeTalkNumberActivity.this, android.R.layout.simple_list_item_1, numberName);
                                    numberSpin.setAdapter(arrayAdapter);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (IOException e) {
                    utils.dismissDialog();
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        if (countrySpin.getSelectedItemPosition() == -1) {
            utils.error("Select a country");
            return;
        }
        if (areaSpin.getSelectedItemPosition() == -1 || areaName.get(areaSpin.getSelectedItemPosition()).contentEquals("Not Available")) {
            utils.error("Select Local Area");
            return;
        }
        if (numberSpin.getSelectedItemPosition() == -1 || numberName.get(numberSpin.getSelectedItemPosition()).contentEquals("Not Available")) {
            utils.error("Choose a number");
            return;
        }
        if (periodSpin.getSelectedItemPosition() == -1) {
            utils.error("Choose a period");
            return;
        }
        utils.displayDialog("Purchase in  process...");
        String url = AppConfig.API_URL + "/vsservices/api/json/syncreply/client.dids.numbers.buy";
        //{"countryId":1,"purches":[{"quantity":1,"phoneNumber":"8090426103", "areaCode":"809", "countryPhoneCode":"NGN",
        // "countryCode":"234", "areaName":"Etisalat", "voxboneGroupId":0,"localAreaId":0,"cnam":"",
        // "channels":1,"dIDWWUniqueCode":"", "nPA":"", "nXX":"", "city":"", "stateCode":"",
        // "phoneGroup":""}],"resellerDb":"", "promotion":false,"resellerRetailClient":"", "subscription":false}
        int[] dt = data.getSaveClientDetails();
        //{"data":{"id":0,"number":"String", "areaId":0,"status":"Assigned"}}
        JSONObject main = new JSONObject();
        JSONArray jsonArrayPur = new JSONArray();
        JSONObject subPur = new JSONObject();
        try {
            main.put("countryId", countryID.get(countrySpin.getSelectedItemPosition()));
            main.put("resellerDb", "");
            main.put("promotion", false);
            main.put("resellerRetailClient", "");
            main.put("subscription", false);

            subPur.put("quantity", 1);
            subPur.put("phoneNumber", numberName.get(numberSpin.getSelectedItemPosition()));
            subPur.put("areaCode", areaCode.get(areaSpin.getSelectedItemPosition()));
            subPur.put("countryPhoneCode", countryPhoneCode.get(countrySpin.getSelectedItemPosition()));
            subPur.put("countryCode", countryCode.get(countrySpin.getSelectedItemPosition()));
            subPur.put("areaName", areaName.get(areaSpin.getSelectedItemPosition()));
            subPur.put("voxboneGroupId", 0);
            subPur.put("localAreaId", 0);
            subPur.put("cnam", "");
            subPur.put("channels", 1);
            subPur.put("dIDWWUniqueCode", "");
            subPur.put("nPA", "");
            subPur.put("nXX", "");
            subPur.put("city", "");
            subPur.put("stateCode", "");
            subPur.put("phoneGroup", "");

            jsonArrayPur.put(0, subPur);

            main.put("purches", jsonArrayPur);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String res = utils.MakeHttpsApiCallsForDiDs(url, main.toString());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            utils.dismissDialog();
                            try {
                                JSONObject object = new JSONObject(res);
                                JSONObject data = object.getJSONArray("data").getJSONObject(0);
                                boolean status = data.getBoolean("status");
                                if (status) {
                                    Toast.makeText(BuyNativeTalkNumberActivity.this, "Number purchase was successful", Toast.LENGTH_SHORT).show();
                                    //AddAniNumber(numberName.get(numberSpin.getSelectedItemPosition()));
                                    new DoSetAndAdd(numberName.get(numberSpin.getSelectedItemPosition())).execute();
//                                    onBackPressed();
                                    startActivity(new Intent(BuyNativeTalkNumberActivity.this, DialerActivity.class));
                                } else {
                                    utils.error(data.getString("message"));
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (IOException e) {
                    utils.dismissDialog();
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void AddAniNumber(String s) {
        String url = AppConfig.API_URL + "/VS.WebAPI.Admin/json/syncreply/admin.client.ani.add";
        int[] dt = data.getSaveClientDetails();
        //{"idClient":0,"clientType":0,"aniNumber":{"phoneNumber":"String", "isDef":false}}
        JSONObject jsonObject = new JSONObject();
        JSONObject jsonObject2 = new JSONObject();
        try {
            //jsonObject.put("id", numberID.get(numberSpin.getSelectedItemPosition()));
            jsonObject.put("idClient", dt[0]);
            jsonObject.put("clientType", dt[1]);
            jsonObject2.put("phoneNumber", s);
            jsonObject2.put("isDef", true);
            jsonObject.put("aniNumber", jsonObject2);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            String res = utils.MakeHttpsApiCalls(url, jsonObject.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void SetCallerNumber(String s) {
        String url = AppConfig.API_URL + "/VS.WebAPI.Admin/json/syncreply/admin.client.techprefix.set";
        int[] dt = data.getSaveClientDetails();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("clientId", dt[0]);
            jsonObject.put("techPrefix", "CP:!" + s);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            String res = utils.MakeHttpsApiCalls(url, jsonObject.toString());
            AddAniNumber(s);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    class DoSetAndAdd extends AsyncTask<Void, Void, Void> {


        String number;

        DoSetAndAdd(String number) {
            this.number = number;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            SetCallerNumber(number);
            return null;
        }
    }
}