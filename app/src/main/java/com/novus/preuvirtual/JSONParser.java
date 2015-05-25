package com.novus.preuvirtual;

import android.util.Log;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by Gustavo Salvo Lara on 24-05-2015.
 * A futuro se debe actualizar  esta clase. :(
 */
public class JSONParser {

    //Lectura desde una fuente de bytes.
    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";


    public JSONObject makeHttpRequest(String url, String method, List<NameValuePair> params) {
        try {
            if(method == "POST"){
                //Implementacion cliente http.
                DefaultHttpClient httpClient = new DefaultHttpClient();
                //Permite enviar mensajes al servidor.
                HttpPost httpPost = new HttpPost(url);
                // UrlEncodedFormEntity: es una entidad compuesta de una lista pares de URL codificada. Se utiliza para enviar HTTP POST request.
                httpPost.setEntity(new UrlEncodedFormEntity(params));
                HttpResponse httpResponse = httpClient.execute(httpPost);
                //Entidad que puede enviar o recibir un mensaje HTTP.
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();

            }else if(method == "GET"){
                DefaultHttpClient httpClient = new DefaultHttpClient();
                String paramString = URLEncodedUtils.format(params, "utf-8");
                url += "?" + paramString;
                //Permite recuperar informacion.
                HttpGet httpGet = new HttpGet(url);
                //Respuesta HTTP
                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }


        try {
            jObj = new JSONObject(json);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }


        return jObj;

    }
}
