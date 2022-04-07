package org.linphone.DataStore;

import android.content.Context;
import android.content.SharedPreferences;

public class AppData {

    Context context;
    SharedPreferences sharedPreferences;


    public AppData(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("nativeTalk", 0);
    }

    public void SaveClientDetails(int clientID, int clientType) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("clientID", clientID);
        editor.putInt("clientType", clientType);
        editor.apply();
    }

    public void SaveClientInformation(Client client) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("first", client.getFirstName());
        editor.putString("last", client.getLastName());
        editor.putString("email", client.getEmail());
        editor.putString("city", client.getCity());
        editor.putString("taxId", client.getTaxId());
        editor.putString("phoneNumber", client.getPhoneNumber());
        editor.putString("address", client.getAddress());
        editor.putString("state", client.getState());
        editor.putString("country", client.getCountry());
        editor.putString("postal", client.getPostalCode());
        editor.apply();
    }

    public Client getClientInformation() {
        return new Client(
                sharedPreferences.getString("first", ""),
                sharedPreferences.getString("last", ""),
                sharedPreferences.getString("email", ""),
                sharedPreferences.getString("city", ""),
                sharedPreferences.getString("taxId", ""),
                sharedPreferences.getString("phoneNumber", ""),
                sharedPreferences.getString("address", ""),
                sharedPreferences.getString("state", ""),
                sharedPreferences.getString("country", ""),
                sharedPreferences.getString("postal", "")
        );
    }

    public int[] getSaveClientDetails() {
        return new int[]{
                sharedPreferences.getInt("clientID", 0),
                sharedPreferences.getInt("clientType", 0)
        };
    }

    public void SavePlanId(int planID) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("planID", planID);
        editor.apply();
    }

    public int getPlanId() {
        return sharedPreferences.getInt("planID", 0);
    }

    public void SavePassword(String password) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("password", password);
        editor.apply();
    }

    public String getPassword() {
        return sharedPreferences.getString("password", "");
    }

    public void SaveUID(String uid) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("firebase_uid", uid);
        editor.apply();
    }

    public String getUID() {
        return sharedPreferences.getString("firebase_uid", "");
    }

    public void SaveTariff(int tariff) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("tariff", tariff);
        editor.apply();
    }

    public int getTariff() {
        return sharedPreferences.getInt("tariff", 0);
    }

    public void LogOut() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
