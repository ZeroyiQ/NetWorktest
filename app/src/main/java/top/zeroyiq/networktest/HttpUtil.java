package top.zeroyiq.networktest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by ZeroyiQ on 2017/7/20.
 */

public class HttpUtil {
    /**
     * 不使用第三方库
     * @param address
     * @param listener
     */
    public static void sendHttpRequest(final String address, final HttpCallbackListener listener) {
       new Thread(new Runnable() {
           @Override
           public void run() {
               HttpURLConnection connection = null;
               BufferedReader reader = null;
               try {
                   URL url = new URL(address);
                   connection = (HttpURLConnection) url.openConnection();
                   connection.setRequestMethod("GET");
                   connection.setConnectTimeout(8000);
                   connection.setReadTimeout(8000);
                   connection.setDoInput(true);
                   connection.setDoOutput(true);
                   InputStream in = connection.getInputStream();
                   reader = new BufferedReader(new InputStreamReader(in));
                   StringBuilder response = new StringBuilder();
                   String line;
                   while ((line = reader.readLine()) != null) {
                       response.append(line);
                   }
                   if (listener != null) {
                       listener.onFinish(response.toString());
                   }
               } catch (Exception e) {
                   if (listener != null) {
                       listener.onError(e);
                   }

               } finally {
                   if (reader != null) {
                       try {
                           reader.close();
                       } catch (IOException e) {
                           e.printStackTrace();
                       }
                   }
                   if (connection != null) {
                       connection.disconnect();
                   }
               }
           }
       }).start();
    }

    /**
     * OKHttp 库
     * @param address
     * @param callback
     */
    public static void sendOKHttpRequest(String address, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(address)
                .build();
        client.newCall(request).enqueue(callback);
    }
}
