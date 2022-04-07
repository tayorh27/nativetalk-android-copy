package org.linphone.accounts;

import android.app.Fragment;
import android.content.Intent;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.DialogAction;

//import android.support.v4.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardInputWidget;
import com.nk.ntalk.R;

import org.json.JSONException;
import org.json.JSONObject;
import org.linphone.AppConfig;
import org.linphone.DataStore.AppData;
import org.linphone.DataStore.Client;
import org.linphone.adapter.CardsAdapter;
import org.linphone.callbacks.MenuClick;
import org.linphone.dialer.DialerActivity;
import org.linphone.models.NativeTalkCard;
import org.linphone.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import co.paystack.android.Paystack;
import co.paystack.android.PaystackSdk;
import co.paystack.android.Transaction;
import co.paystack.android.exceptions.ExpiredAccessCodeException;
import co.paystack.android.exceptions.InvalidEmailException;
import co.paystack.android.model.Charge;

import static androidx.core.content.ContextCompat.getColor;

public class BuyCreditActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener, MenuClick {

    RadioGroup myRadioGroup;
    CardInputWidget cardInputWidget;
    Button btnContinue;
    int amount = 0;
    Utils utils;
    AppData data;
    String getEmail = "";
    ImageView ivBack;
    TextView sAmt;

    RadioButton rb1, rb2, rb3, rb4;
    ImageView secureImg;
    co.paystack.android.model.Card _payCard;
    Charge charge;
    Transaction transaction;

    ImageView addCardBtn;
    EditText editAmt;

    String customer_code_naira = "", customer_code_dollar = "";

    LinearLayout cardList;
    RecyclerView recyclerView;
    ArrayList<NativeTalkCard> cards = new ArrayList<>();
    CardsAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.buy_credit_fragment);

        utils = new Utils(BuyCreditActivity.this);
        data = new AppData(BuyCreditActivity.this);
        adapter = new CardsAdapter(this, this);
        recyclerView = findViewById(R.id.cardsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(BuyCreditActivity.this, RecyclerView.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);
        cardList = findViewById(R.id.card_list);
        ivBack = findViewById(R.id.back);
        addCardBtn = findViewById(R.id.add_card);
        editAmt = findViewById(R.id.amount_text);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        addCardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardInputWidget.setVisibility(View.VISIBLE);
                btnContinue.setVisibility(View.VISIBLE);
                secureImg.setVisibility(View.VISIBLE);
            }
        });

        myRadioGroup = findViewById(R.id.radioGroup);
        secureImg = findViewById(R.id.secureLogoImage);
        rb1 = findViewById(R.id.radioButton1);
        rb2 = findViewById(R.id.radioButton2);
        rb3 = findViewById(R.id.radioButton3);
        rb4 = findViewById(R.id.radioButton4);
        sAmt = findViewById(R.id.specifyAmount);
        myRadioGroup.setOnCheckedChangeListener(this);
        btnContinue = findViewById(R.id.credit_apply);
        cardInputWidget = findViewById(R.id.cardDetails);
        btnContinue.setOnClickListener(this);
        Log.e("testing-naira", "getTariff:"+data.getTariff());

        if (data.getTariff() == 21) {
            rb1.setText("NGN 500");
            rb2.setText("NGN 1000");
            rb3.setText("NGN 2500");
            rb4.setText("NGN 5000");
            secureImg.setImageResource(R.drawable.paystack_secure);
        }
    }


    @Override
    public void onClick(View v) {
//        int id = myRadioGroup.getCheckedRadioButtonId();
//        if (id == R.id.radioButton1) {
//            if (data.getTariff() == 21) {
//                amount = 500;
//            } else {
//                amount = 5;
//            }
//        }
//        if (id == R.id.radioButton2) {
//            if (data.getTariff() == 21) {
//                amount = 1000;
//            } else {
//                amount = 10;
//            }
//        }
//        if (id == R.id.radioButton3) {
//            if (data.getTariff() == 21) {
//                amount = 2500;
//            } else {
//                amount = 25;
//            }
//        }
//        if (id == R.id.radioButton4) {
//            if (data.getTariff() == 21) {
//                amount = 5000;
//            } else {
//                amount = 50;
//            }
//        }
        String getAmount = "10";
        //editAmt.getText().toString();
//        if(TextUtils.isEmpty(getAmount)) {
//            Toast.makeText(BuyCreditActivity.this, "Select or specify an amount to recharge", Toast.LENGTH_SHORT).show();
//            return;
//        }
        amount = Integer.parseInt(getAmount);

        if (amount <= 0) {
            Toast.makeText(BuyCreditActivity.this, "Select or specify an amount to recharge", Toast.LENGTH_SHORT).show();
            return;
        }

        Card card = cardInputWidget.getCard();
        if (card == null || !card.validateCard()) {
            Toast.makeText(BuyCreditActivity.this, "Enter valid card details", Toast.LENGTH_SHORT).show();
            return;
        }

        if (data.getTariff() == 21) {
            _payCard = new co.paystack.android.model.Card(card.getNumber(), card.getExpMonth(), card.getExpYear(), card.getCVC());
            new MaterialDialog.Builder(this)
                    .cancelable(false)
                    .canceledOnTouchOutside(false)
                    .title("Message")
                    .content("You will be charged NGN "+ getAmount + " in order to verify your card. The money charged will be used to credit your account balance. Please click OK to continue?")
                    .positiveText("OK")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            dialog.dismiss();
                            GetAccessCode();
                        }
                    }).show();
            return;
        }
        utils.displayDialog("Generating token...");
        Stripe stripe = new Stripe(BuyCreditActivity.this, "pk_live_RYOVureFgO2w8BSk9ZPniftN");
        stripe.createToken(card, new TokenCallback() {
            @Override
            public void onError(Exception error) {
                utils.dismissDialog();
                utils.error(error.getMessage());
            }

            @Override
            public void onSuccess(Token token) {
                utils.dismissDialog();
                ProcessPaymentNow(token);
            }
        });
    }

    private void GetAccessCode() {
        String email = data.getClientInformation().getEmail();
        int finalAmount = Integer.parseInt(amount + "00");
        charge = new Charge();
        charge.setCard(_payCard); //sets the card to charge
        charge.setCurrency("NGN");
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            email = (email == "") ? "user@nativetalkmobile.com" : email.substring(0, email.indexOf("@")) + "@gmail.com";
            Log.e("nemail", "GetAccessCode: "+email);
            charge.setEmail(email);
        }else {
            charge.setEmail(email);
        }
        charge.setAmount(finalAmount);
        utils.displayDialog("Initializing transaction");
        final String url = "https://api.paystack.co/transaction/initialize";
        final JSONObject object = new JSONObject();
        try {
            Random r = new Random();
            String ref = "NT" + r.nextInt(9999999);
            object.put("reference", ref);
            object.put("amount", finalAmount);
            object.put("email", email);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final String response = utils.ChargeTransactionForPaystack(url, object.toString());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            utils.dismissDialog();
                            try {
                                JSONObject obj = new JSONObject(response);
                                boolean status = obj.getBoolean("status");
                                String message = obj.getString("message");
                                if (status) {
                                    String code = obj.getJSONObject("data").getString("access_code");
                                    charge.setAccessCode(code);
                                    utils.error("Your card pin is required and an OTP will be sent to your phone to complete this transaction. Please wait on this screen until transaction is successful.");
//                                    Toast.makeText(BuyCreditActivity.this, "Please wait...", Toast.LENGTH_LONG).show();
                                    ProcessPaymentForPayStack();
                                } else {
                                    Toast.makeText(BuyCreditActivity.this, message, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                Log.e("JSONException", "run: "+e.toString() );
                            }
                        }
                    });
                } catch (IOException e) {
                    Log.e("paystackInit", "run: "+e.toString() );
                }
            }
        }).start();
    }

    private void ProcessPaymentForPayStack() {
        transaction = null;
        PaystackSdk.chargeCard(BuyCreditActivity.this, charge, new Paystack.TransactionCallback() {
            @Override
            public void onSuccess(Transaction transaction) {
                utils.dismissDialog();
                utils.displayDialog("Verifying Transaction");
                String url = "https://api.paystack.co/transaction/verify/" + transaction.getReference();
                VerifyTransaction(url);
            }

            @Override
            public void beforeValidate(Transaction transaction) {

            }

            @Override
            public void onError(Throwable error, Transaction transaction) {
                if (error instanceof ExpiredAccessCodeException) {
                    Toast.makeText(BuyCreditActivity.this, "Operation time-out", Toast.LENGTH_SHORT).show();
                    GetAccessCode();
                    return;
                }
                if (transaction.getReference() != null) {
                    Toast.makeText(BuyCreditActivity.this, transaction.getReference() + " concluded with error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(BuyCreditActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    //verify on server
                    //mTextError.setText(String.format("Error: %s %s", error.getClass().getSimpleName(), error.getMessage()));Nnonah
                }
            }
        });
    }

    private void VerifyTransaction(String url) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final String res = utils.TransactionVerification(url);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                utils.dismissDialog();
                                JSONObject object = new JSONObject(res);
//                                Log.e("JSONTAG", "run: "+object.toString() );
                                boolean status = object.getBoolean("status");
                                String message = object.getString("message");
//                                Log.e("MaterialDialog", "s = "+status + " - m = "+message);

                                if (status && message.contentEquals("Verification successful")) {

                                    boolean isExist = false;

                                    JSONObject dataObj = object.getJSONObject("data");
                                    JSONObject authObj = dataObj.getJSONObject("authorization");

                                    //get auth data
                                    String authCode = authObj.getString("authorization_code");
                                    String brand = authObj.getString("brand");
                                    String lastFour = authObj.getString("last4");
                                    String expMonth = authObj.getString("exp_month");
                                    String expYear = authObj.getString("exp_year");
                                    String bank = authObj.getString("bank");

                                    for(NativeTalkCard talkCard : cards) {
                                        if(talkCard.getLast_four().contentEquals(lastFour)){
                                            isExist = true;
                                        }
                                    }

                                    if(!isExist) {
                                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                        String uid = "uid";
                                        if(user != null) {
                                            uid = user.getUid();
                                        }
                                        Log.e("CaRD", "run: "+uid);
                                        String id = FirebaseFirestore.getInstance().collection("users").document(uid).collection("cards").document().getId();

                                        Map<String, Object> mapData = new HashMap<>();
                                        mapData.put("auth_code", authCode);
                                        mapData.put("brand", brand);
                                        mapData.put("last_four", lastFour);
                                        mapData.put("exp_month", expMonth);
                                        mapData.put("exp_year", expYear);
                                        mapData.put("bank", bank);
                                        mapData.put("id", id);
                                        mapData.put("created_date", new Date().toString());
                                        mapData.put("timestamp", FieldValue.serverTimestamp());


                                        FirebaseFirestore.getInstance().collection("users").document(uid).collection("cards").document(id).set(mapData);
                                    }
                                    if (TextUtils.isEmpty(customer_code_naira)) {
                                        customer_code_naira = object.getJSONObject("data").getJSONObject("customer").getString("customer_code");
                                        StoreCustomerCode(21);
                                        return;
                                    }
                                    showDialogAfterSuccessfulPayment();
                                } else {
                                    Toast.makeText(BuyCreditActivity.this, message, Toast.LENGTH_LONG).show();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void showDialogAfterSuccessfulPayment() {
        new BackgroundTask().execute();
        new MaterialDialog.Builder(this)
                .cancelable(false)
                .canceledOnTouchOutside(false)
                .title("Message")
                .content("Payment Successfully made. Press OK to continue.")
                .positiveText("OK")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Log.e("MaterialDialog", "onClick: Continue execute");
//                        onBackPressed();
                        startActivity(new Intent(BuyCreditActivity.this, DialerActivity.class));
                    }
                }).show();
    }

    private void StoreCustomerCode(int code) {
        String url = AppConfig.API_URL + "/VS.WebAPI.Admin/json/syncreply/admin.client.personal.update";
        int[] details = data.getSaveClientDetails();
        JSONObject objectM = new JSONObject();
        JSONObject object = new JSONObject();
        try {
            object.put("idClient", details[0]);
            object.put("clientType", details[1]);
            if (code == 21) {
                objectM.put("taxId", customer_code_naira);
                data.getClientInformation().setTaxId(customer_code_naira);
            } else {
                objectM.put("taxId", customer_code_dollar);
                data.getClientInformation().setTaxId(customer_code_dollar);
            }
            data.SaveClientInformation(data.getClientInformation());
            objectM.put("city", data.getClientInformation().getCity());
            objectM.put("phoneNumber", data.getClientInformation().getPhoneNumber());
            objectM.put("eMail", data.getClientInformation().getEmail());
            objectM.put("lastName", data.getClientInformation().getLastName());
            objectM.put("country", data.getClientInformation().getCountry());
            objectM.put("state", data.getClientInformation().getState());
            objectM.put("address", data.getClientInformation().getAddress());
            objectM.put("zip", data.getClientInformation().getPostalCode());
            objectM.put("phoneNumber", data.getClientInformation().getPhoneNumber());
            objectM.put("mobileNumber", data.getClientInformation().getPhoneNumber());
            object.put("personal", objectM);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String res = utils.MakeHttpsApiCalls(url, object.toString());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            utils.dismissDialog();
                            showDialogAfterSuccessfulPayment();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void DisplayDialogForOthers() {
        String title = "";
        if (data.getTariff() == 21) {
            title = "Enter amount to recharge in NGN";
        } else {
            title = "Enter amount to recharge in USD";
        }
        new MaterialDialog.Builder(this)
                .cancelable(false)
                .canceledOnTouchOutside(true)
                .title(title)
                .input("5", "", false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        amount = Integer.parseInt(String.valueOf(input));
                        sAmt.setVisibility(View.VISIBLE);
                        if (data.getTariff() == 21) {
                            sAmt.setText("NGN " + amount);
                        } else {
                            sAmt.setText(amount + " USD");
                        }

                    }
                }).inputRange(2, 8, getColor(R.color.bootstrap_brand_danger))
                .inputType((data.getTariff() == 21) ? InputType.TYPE_CLASS_PHONE : InputType.TYPE_CLASS_NUMBER)
                .negativeText("Cancel")
                .show();
    }

    private void ProcessPaymentNow(Token token) {
        utils.displayDialog("Processing payment...");
        final String url = AppConfig.STRIPE_API_CHARGE_URL;
        final Map<String, String> object_main = new HashMap<>();
        try {
            int amt = Integer.parseInt(amount + "00");
            object_main.put("amount", amt + "");
            object_main.put("currency", "usd");
            object_main.put("description", "NativeTalk Mobile Recharge");
            object_main.put("source", token.getId());
            object_main.put("receipt_email", getEmail);
            //object_main.put("receipt_number", cooker.getPhoneNumber());

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        final String response = utils.MakeHttpsApiCallsForStripe(url, object_main);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                utils.dismissDialog();
                                JSONObject return_object;
                                try {
                                    //Log.e("response", response);
                                    return_object = new JSONObject(response);
                                    if (!return_object.isNull("error")) {
                                        utils.error(return_object.getJSONObject("error").getString("message"));
                                        return;
                                    }
                                    String status = return_object.getString("status");
                                    if (status.contentEquals("succeeded")) {
                                        customer_code_dollar = return_object.getJSONObject("source").getString("customer");
                                        StoreCustomerCode(10);
                                        new MaterialDialog.Builder(BuyCreditActivity.this)
                                                .cancelable(false)
                                                .canceledOnTouchOutside(false)
                                                .title("Message")
                                                .content("Payment Successfully made. Press OK to continue.")
                                                .positiveText("OK")
                                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                    @Override
                                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                        new BackgroundTask().execute();
                                                        startActivity(new Intent(BuyCreditActivity.this, DialerActivity.class));
                                                    }
                                                }).show();
                                        //utils.error("Payment Successfully made. Press ok to continue.");//THis is no error just that the function name is error
                                        //Toast.makeText(this, "Payment Successfully made", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        });
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }).start();


        } catch (Exception e) {
            Log.e("catch", e.toString());
        }

    }

    @Override
    public void onClick(View view, int position) {
        String getAmount = editAmt.getText().toString();
        if(TextUtils.isEmpty(getAmount)) {
            Toast.makeText(BuyCreditActivity.this, "Select or specify an amount to recharge", Toast.LENGTH_SHORT).show();
            return;
        }
        amount = Integer.parseInt(getAmount);

        NativeTalkCard selectedCard = cards.get(position);

        new MaterialDialog.Builder(this)
                .cancelable(false)
                .canceledOnTouchOutside(false)
                .title("Message")
                .content("You will be charged NGN "+ getAmount + ". Please click OK to continue?")
                .positiveText("OK")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                        utils.displayDialog("Processing payment...please wait");
                        ProcessPaystackPaymentWithAuthCode(selectedCard);
                    }
                }).show();
    }

    @Override
    public void onLongPress(View view, int position) {
        NativeTalkCard selectedCard = cards.get(position);
        new MaterialDialog.Builder(this)
                .cancelable(false)
                .canceledOnTouchOutside(false)
                .title("Confirmation")
                .content("Are you sure you want to delete this card? Please click Yes to continue?")
                .positiveText("Yes")
                .negativeText("No")
                .negativeColor(getResources().getColor(R.color.red_color))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        String uid = "uid";
                        if(user != null) {
                            uid = user.getUid();
                        }
                        FirebaseFirestore.getInstance().collection("users").document(uid).collection("cards").document(selectedCard.getId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()) {
                                    cards.remove(position);
                                    adapter.UpdateCycle(cards);
                                    Toast.makeText(BuyCreditActivity.this, "Card deleted", Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(BuyCreditActivity.this, "An error occurred. Please try again.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }).show();
    }

    private void ProcessPaystackPaymentWithAuthCode(NativeTalkCard card) {
        String url = "https://api.paystack.co/transaction/charge_authorization";
        String email = data.getClientInformation().getEmail();
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            email = (email == "") ? "user@nativetalkmobile.com" : email.substring(0, email.indexOf("@")) + "@gmail.com";
        }
        int finalAmount = Integer.parseInt(amount + "00");
        final JSONObject object = new JSONObject();
        try {
            object.put("authorization_code", card.getAuthCode());
            object.put("amount", finalAmount);
            object.put("email", email);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final String res = utils.TransactionWithAuthCode(url, object.toString());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                utils.dismissDialog();
                                JSONObject object = new JSONObject(res);
                                Log.e("JSONTAG", "run: "+object.toString() );
                                boolean status = object.getBoolean("status");
                                String message = object.getString("message");
//                                Log.e("MaterialDialog", "s = "+status + " - m = "+message);

                                if (status && message.contentEquals("Charge attempted")) {
                                    JSONObject dataObj = object.getJSONObject("data");
                                    String ref = dataObj.getString("reference");
//                                    showDialogAfterSuccessfulPayment();
                                    String url = "https://api.paystack.co/transaction/verify/" + ref;
                                    VerifyTransaction(url);
                                } else {
                                    Toast.makeText(BuyCreditActivity.this, message, Toast.LENGTH_LONG).show();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private class BackgroundTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            String url = AppConfig.API_URL + "/VS.WebAPI.Admin/json/syncreply/admin.payment.add";
            int[] details = data.getSaveClientDetails();
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("money", amount);
                jsonObject.put("paymentType", "PrePaid");
                jsonObject.put("idClient", details[0]);
                jsonObject.put("clientType", details[1]);
                jsonObject.put("addToInvoice", false);
                jsonObject.put("description", "Credit bought");

                try {
                    Log.e("backgroundRes", jsonObject.toString());
                    String res = utils.MakeHttpsApiCalls(url, jsonObject.toString());
                    Log.e("backgroundRes", res);
                } catch (IOException e) {
                    Log.e("backgroundRes", e.toString());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        //int id = group.getCheckedRadioButtonId();
        if (checkedId == R.id.radioButton6) {
            String buyCreditUrl = AppConfig.NATIVETALK_API_URL+"/onlineshop/rechargemobile.aspx";
//            LinphoneActivity.instance().displayOnlineAccount(buyCreditUrl);
        }

        if (checkedId == R.id.radioButton5) {
            DisplayDialogForOthers();
        } else {
            sAmt.setVisibility(View.GONE);
        }


    }

    @Override
    public void onStart() {
        super.onStart();
        PaystackSdk.setPublicKey("pk_live_c15fc0bb4c1a79798e557c2d6fef094d51747766");
        String email = data.getClientInformation().getEmail();
        getEmail = (TextUtils.isEmpty(email)) ? "user@nativetalkmobile.com" : email.substring(0, email.indexOf("@")) + "@gmail.com";
        getCardsStored();
    }

    private void getCardsStored() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = "uid";
        if(user != null) {
            uid = user.getUid();
        }
        Log.e("CaRDOnStart", "run: "+uid);
//        assert uid != null;
        FirebaseFirestore.getInstance().collection("users").document(uid).collection("cards").addSnapshotListener((value, error) -> {
            if(value == null) {
                return;
            }
            if(value.size() > 0) {
                    cards.clear();
                    for (DocumentSnapshot doc : value.getDocuments()) {
                        Map<String, Object> data = doc.getData();

                        NativeTalkCard card = new NativeTalkCard(
                                data.get("id").toString(),
                                data.get("auth_code").toString(),
                                data.get("brand").toString(),
                                data.get("last_four").toString(),
                                data.get("exp_month").toString(),
                                data.get("exp_year").toString(),
                                data.get("bank").toString(),
                                data.get("created_date").toString(),
                                data.get("timestamp")
                        );
                        cards.add(card);
                    }
                    adapter.UpdateCycle(cards);
                }
        });

    }
}
