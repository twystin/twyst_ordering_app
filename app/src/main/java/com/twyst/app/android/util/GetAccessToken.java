package com.twyst.app.android.util;

import org.json.JSONObject;

import java.io.InputStream;

/**
 * Created by vivek on 06/08/15.
 */
public class GetAccessToken {
    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";

    public GetAccessToken() {
    }



//    public JSONObject gettoken(String address, String token, String client_id, String client_secret, String redirect_uri, String grant_type) {
//        // Making HTTP request
//        try {
//            List<NameValuePair> params = new ArrayList<NameValuePair>();
//            Map<String, String> mapn;
//            DefaultHttpClient httpClient;
//            HttpPost httpPost;
//            // DefaultHttpClient
//            httpClient = new DefaultHttpClient();
//            httpPost = new HttpPost(address);
//            params.add(new BasicNameValuePair("code", token));
//            params.add(new BasicNameValuePair("client_id", client_id));
//            params.add(new BasicNameValuePair("client_secret", client_secret));
//            params.add(new BasicNameValuePair("redirect_uri", redirect_uri));
//            params.add(new BasicNameValuePair("grant_type", grant_type));
//            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
//            httpPost.setEntity(new UrlEncodedFormEntity(params));
//            HttpResponse httpResponse = httpClient.execute(httpPost);
//            HttpEntity httpEntity = httpResponse.getEntity();
//            is = httpEntity.getContent();
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        } catch (ClientProtocolException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        try {
//            BufferedReader reader = new BufferedReader(new InputStreamReader(
//                    is, "iso-8859-1"), 8);
//            StringBuilder sb = new StringBuilder();
//            String line = null;
//            while ((line = reader.readLine()) != null) {
//                sb.append(line + "\n");
//            }
//            is.close();
//            json = sb.toString();
//            Log.e("JSONStr", json);
//        } catch (Exception e) {
//            e.getMessage();
//            Log.e("Buffer Error", "Error converting result " + e.toString());
//        }
//        // Parse the String to a JSON Object
//        try {
//            jObj = new JSONObject(json);
//        } catch (JSONException e) {
//            Log.e("JSON Parser", "Error parsing data " + e.toString());
//        }
//        // Return JSON String
//        return jObj;
//    }
}