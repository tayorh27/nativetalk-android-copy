package org.linphone.utils;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.linphone.DataStore.AppData;
import org.linphone.LinphoneManager;
//import org.linphone.LinphoneUtils;
//import org.linphone.core.LinphoneProxyConfig;
import org.linphone.core.ProxyConfig;
import org.linphone.network.TLSSocketFactory;
import org.linphone.utils.LinphoneUtils;

import java.io.IOException;
import java.net.UnknownServiceException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.CipherSuite;
import okhttp3.ConnectionSpec;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.TlsVersion;
import okio.ByteString;

import com.afollestad.materialdialogs.MaterialDialog;

public class Utils {

    Context context;
    private MaterialDialog materialDialog;
    private final OkHttpClient httpClient = new OkHttpClient();

    public Utils(Context context) {
        this.context = context;
    }

    public void displayDialog(String text) {
        materialDialog = new MaterialDialog.Builder(context)
                .content(text)
                .canceledOnTouchOutside(true)
                .cancelable(false)
                .progress(true, 0)
                .show();
    }

    public void dismissDialog() {
        materialDialog.dismiss();
    }

    public void error(String text) {
        new MaterialDialog.Builder(context)
                .title("Message")
                .content(text)
                .positiveText("OK")
                .show();
    }

    public String formatNumber(String number) {
        String n = number.replaceAll(" ", "");
        if (n.startsWith("234")) {
            return n.replaceAll(" ", "");
        } else if (n.startsWith("+234")) {
            return n.substring(1).replace(" ", "");
        } else if (n.startsWith("0")) {
            return "234"+n.substring(1).replaceAll(" ", "");
        } else if (n.startsWith("7") || n.startsWith("8") || n.startsWith("9")) {
            return "234"+n.replaceAll(" ", "");
        }
        return n.replace(" ", "");
    }

    public String getAdminEmail() {
        ProxyConfig proxy = Objects.requireNonNull(LinphoneManager.getCore()).getDefaultProxyConfig();
        assert proxy != null;
        String username = LinphoneUtils.getAddressDisplayName(proxy.getIdentityAddress()); //getAddress()
        return username+"@nativetalk.com.ng";
    }


    public String _MakeHttpsApiCalls(final String url, String json) throws IOException {
        OkHttpClient client = null;
        try {
            TLSSocketFactory tls = new TLSSocketFactory();
            client = new OkHttpClient.Builder()
                    .connectionSpecs(createHttpConnectionSpecs())
                    .sslSocketFactory(tls, tls.x509TrustManager)
                    .build();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }catch (Exception e) {
            Log.e("MakeHttpsApiCalls", "MakeHttpsApiCalls: "+e.toString() );
        }
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Basic YWRtaW4jYWRtaW46NTE1RDQ3QTFDMUQ0NzMxNUVFQkJDQTg0RDVEQjdEQTI5OTRBRDFBOA==")
                .post(body)
                .build();
        okhttp3.Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public String MakeHttpsApiCalls(final String url, String json) throws IOException {
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Basic YWRtaW4jYWRtaW46NTE1RDQ3QTFDMUQ0NzMxNUVFQkJDQTg0RDVEQjdEQTI5OTRBRDFBOA==") //YWRtaW4jYWRtaW46OGFiNGFmYjFiYmRmNmI1OTJjMzdjNTE4YzlkNDA3MDYwMDk2NWY1Mw==")
                .post(body)
                .build();
        Response response = httpClient.newCall(request).execute();
        return response.body().string();
    }


    public String _MakeHttpsApiCallsForDiDs(final String url, String json) throws IOException {
        ProxyConfig proxy = Objects.requireNonNull(LinphoneManager.getCore()).getDefaultProxyConfig();
        assert proxy != null;
        String username = LinphoneUtils.getAddressDisplayName(proxy.getIdentityAddress());
        String pwd = new AppData(context).getPassword();
        String hash = username + ":" + pwd.replace(" ","");
        //String base64 = Base64.getEncoder().encodeToString(hash.getBytes("utf-8"));
        //String base64 = android.util.Base64.encodeToString(hash.getBytes("UTF-8"), android.util.Base64.NO_WRAP);
        String base64 = ByteString.encodeUtf8(hash).base64();

        OkHttpClient client = null;
        try {
            TLSSocketFactory tls = new TLSSocketFactory();
            client = new OkHttpClient.Builder()
                    .connectionSpecs(createHttpConnectionSpecs())
                    .sslSocketFactory(tls, tls.x509TrustManager)
                    .build();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Basic " + base64)//2348035013937
                .post(body)
                .build();
        okhttp3.Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public String MakeHttpsApiCallsForDiDs(final String url, String json) throws IOException {
        ProxyConfig proxy = Objects.requireNonNull(LinphoneManager.getCore()).getDefaultProxyConfig();
        assert proxy != null;
        String username = LinphoneUtils.getAddressDisplayName(proxy.getIdentityAddress());
        String pwd = new AppData(context).getPassword();
        String hash = username + ":" + pwd.replace(" ","");
        //String base64 = Base64.getEncoder().encodeToString(hash.getBytes("utf-8"));
        //String base64 = android.util.Base64.encodeToString(hash.getBytes("UTF-8"), android.util.Base64.NO_WRAP);
        String base64 = ByteString.encodeUtf8(hash).base64();

        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Basic " + base64)//2348035013937
                .post(body)
                .build();
        Response response = httpClient.newCall(request).execute();
        return response.body().string();
    }

    public String MakeHttpsApiCallsForStripe(final String url, Map<String, String> json) throws IOException {
        OkHttpClient client = null;
        try {
            TLSSocketFactory tls = new TLSSocketFactory();
            client = new OkHttpClient.Builder()
                    .connectionSpecs(createConnectionSpecs())
                    .sslSocketFactory(tls, tls.x509TrustManager)
                    .build();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        //RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        RequestBody formBody = new FormBody.Builder()
                .add("amount", json.get("amount"))
                .add("currency", json.get("currency"))
                .add("description", json.get("description"))
                .add("source", json.get("source"))
                .add("receipt_email", json.get("receipt_email"))
                .build();
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8")
                .addHeader("Authorization", "Bearer sk_live_Gt88KRgXxdoAJZrFU4OwXlNS")
                .post(formBody)
                .build();
        okhttp3.Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public String MakeHttpsApiCallsForStripeSubscription(final String url, Map<String, String> json) throws IOException {
        OkHttpClient client = null;
        try {
            TLSSocketFactory tls = new TLSSocketFactory();
            client = new OkHttpClient.Builder()
                    .connectionSpecs(createConnectionSpecs())
                    .sslSocketFactory(tls, tls.x509TrustManager)
                    .build();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        //RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        RequestBody formBody = new FormBody.Builder()
                .add("items[0][plan]", json.get("plan"))
                .add("customer", json.get("customer"))
                .build();
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8")
                .addHeader("Authorization", "Bearer sk_live_Gt88KRgXxdoAJZrFU4OwXlNS")
                .post(formBody)
                .build();
        okhttp3.Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public String GetNormalApiCalls(final String url) throws IOException {
        OkHttpClient client = null;
//        try {
//            TLSSocketFactory tls = new TLSSocketFactory();
//            client = new OkHttpClient.Builder()
//                    .connectionSpecs(createHttpConnectionSpecs())
//                    .sslSocketFactory(tls, tls.x509TrustManager)
//                    .build();
//        } catch (KeyManagementException e) {
//            e.printStackTrace();
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }catch (Exception e) {
//            Log.e("GetNormalApiCalls", "MakeHttpsApiCalls: "+e.toString() );
//        }
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .get()
                .build();
        okhttp3.Response response = httpClient.newCall(request).execute();
        return response.body().string();
    }

    public String ChargeTransactionForPaystack(String url, String json) throws IOException {
        OkHttpClient client = null;
        try {
            TLSSocketFactory tls = new TLSSocketFactory();
            client = new OkHttpClient.Builder()
                    .connectionSpecs(createConnectionSpecs())
                    .sslSocketFactory(tls, tls.x509TrustManager)
                    .build();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer sk_live_a3d68c096c46bf96e87f6ffecb4dcc3f96f956dd")
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();
        okhttp3.Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public String TransactionVerification(String url) throws IOException {
        OkHttpClient client = null;
        try {
            TLSSocketFactory tls = new TLSSocketFactory();
            client = new OkHttpClient.Builder()
                    .connectionSpecs(createConnectionSpecs())
                    .sslSocketFactory(tls, tls.x509TrustManager)
                    .build();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        //RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer sk_live_a3d68c096c46bf96e87f6ffecb4dcc3f96f956dd")
                .get()
                .build();
        okhttp3.Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public String TransactionWithAuthCode(String url, String json) throws IOException {
        OkHttpClient client = null;
        try {
            TLSSocketFactory tls = new TLSSocketFactory();
            client = new OkHttpClient.Builder()
                    .connectionSpecs(createConnectionSpecs())
                    .sslSocketFactory(tls, tls.x509TrustManager)
                    .build();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer sk_live_a3d68c096c46bf96e87f6ffecb4dcc3f96f956dd")
                .post(body)
                .build();
        okhttp3.Response response = client.newCall(request).execute();
        return response.body().string();
    }

    private static List<ConnectionSpec> createConnectionSpecs() {
        ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                .tlsVersions(TlsVersion.TLS_1_2)
                .cipherSuites(
                        CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                        CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                        CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256,
                        CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA,
                        CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA,
                        CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA,
                        CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA,
                        CipherSuite.TLS_ECDHE_ECDSA_WITH_RC4_128_SHA,
                        CipherSuite.TLS_ECDHE_RSA_WITH_RC4_128_SHA,
                        CipherSuite.TLS_DHE_RSA_WITH_AES_128_CBC_SHA,
                        CipherSuite.TLS_DHE_DSS_WITH_AES_128_CBC_SHA,
                        CipherSuite.TLS_DHE_RSA_WITH_AES_256_CBC_SHA)
                .build();
        return Collections.singletonList(spec);
    }

    private static List<ConnectionSpec> createHttpConnectionSpecs() {
        ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.CLEARTEXT)
                .tlsVersions(TlsVersion.TLS_1_2)
                .cipherSuites(
                        CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                        CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                        CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256,
                        CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA,
                        CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA,
                        CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA,
                        CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA,
                        CipherSuite.TLS_ECDHE_ECDSA_WITH_RC4_128_SHA,
                        CipherSuite.TLS_ECDHE_RSA_WITH_RC4_128_SHA,
                        CipherSuite.TLS_DHE_RSA_WITH_AES_128_CBC_SHA,
                        CipherSuite.TLS_DHE_DSS_WITH_AES_128_CBC_SHA,
                        CipherSuite.TLS_DHE_RSA_WITH_AES_256_CBC_SHA)
                .build();
        return Collections.singletonList(spec);
    }
}
