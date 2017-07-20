package top.zeroyiq.networktest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import javax.xml.parsers.SAXParserFactory;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    String id = "";
    String name = "";
    String version = "";
    private final static String TAG = "MainActivity";

    @BindView(R.id.tv_response_text)
    TextView responseText;

    @OnClick(R.id.btn_send_request)
    void sendRequest() {
        new Thread(new Runnable() {
            @Override
            public void run() {
//                HttpURLConnection connection = null;
//                BufferedReader reader = null;
//                try {
//                    URL url = new URL("http://live.bilibili.com/1937333");
//                    connection = (HttpURLConnection) url.openConnection();
//                    connection.setRequestMethod("GET");
//                    connection.setConnectTimeout(8000);
//                    connection.setReadTimeout(8000);
//                    InputStream in = connection.getInputStream();
//
//                    reader = new BufferedReader(new InputStreamReader(in));
//                    StringBuilder response = new StringBuilder();
//                    String line;
//                    while ((line = reader.readLine()) != null) {
//                        response.append(line);
//                    }
//                    showResponse(response.toString());
//                } catch (Exception e) {
//                    e.printStackTrace();
//                } finally {
//                    if (reader != null) {
//                        try {
//                            reader.close();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    if (connection != null) {
//                        connection.disconnect();
//                    }
//                }

                /**
                 *  OKHttp
                 */
                try {
//                    OkHttpClient client = new OkHttpClient();
//                    Request request = new Request.Builder()
////                            .url("http://zeroyiq.top./posts/get_data.xml")
//                            .url("http://zeroyiq.top/doc/get_data.json")
//                            .build();
//                    Response response = client.newCall(request).execute();
//                    String responseBody = response.body().string();
                    /**
                     * 将 Http 请求封装到单独类 HttpUtil
                     */
                    String address = "http://zeroyiq.top./posts/get_data.json";

                    //不使用第三方库
//                    HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
//                        @Override
//                        public void onFinish(String response) {
//                            showResponse(response);
//                        }
//
//                        @Override
//                        public void onError(Exception e) {
//                            e.printStackTrace();
//                        }
//                    });
                    HttpUtil.sendOKHttpRequest(address, new okhttp3.Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String responseData = response.body().string();
                            showResponse(responseData);
                        }
                    });
                    /**
                     * HTML
                     */
//                    例一：仅仅展示获取到的网页
//                    showResponse(responseBody);

                    /**
                     * XML
                     */
//                    例二：通过 Pull 解析 XML
//                    parseXMLWithPull(responseBody);

//                    例三；通过 SAX 解析 XML
//                    parseXMLWithSAX(responseBody);

                    /**
                     * JSON
                     */
//                    例四：JSONObject
//                    parseJSONWithJSONObject(responseBody);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }


    private void showResponse(final String response) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                responseText.setText(response);
            }
        });

    }

    //Pull 解析 XML
    private void parseXMLWithPull(String responseBody) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = factory.newPullParser();
            xmlPullParser.setInput(new StringReader(responseBody));
            int eventType = xmlPullParser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String nodeName = xmlPullParser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG: {
                        if ("id".equals(nodeName)) {
                            id = xmlPullParser.nextText();
                        } else if ("name".equals(nodeName)) {
                            name = xmlPullParser.nextText();
                        } else if ("version".equals(nodeName)) {
                            version = xmlPullParser.nextText();
                        }
                        break;
                    }
                    case XmlPullParser.END_TAG: {
                        if ("app".equals(nodeName)) {
                            Log.d(TAG, "id is " + id);
                            Log.d(TAG, "name is " + name);
                            Log.d(TAG, "version is " + version);
                        }
                        break;
                    }
                    default:
                        break;
                }
                eventType = xmlPullParser.next();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseXMLWithSAX(String responseBody) {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            XMLReader reader = factory.newSAXParser().getXMLReader();
            ContentHandler handler = new ContentHandler();

            reader.setContentHandler(handler);
            reader.parse(new InputSource(new StringReader(responseBody)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseJSONWithJSONObject(String responseBody) {
        try {
            JSONArray jsonArray = new JSONArray(responseBody);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                id = jsonObject.getString("id");
                version = jsonObject.getString("version");
                name = jsonObject.getString("name");
                Log.d(TAG, "id is " + id);
                Log.d(TAG, "name is " + name);
                Log.d(TAG, "version is " + version);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseJSONWithGSON(String responseBody) {
        Gson gson = new Gson();
        List<App> appList = gson.fromJson(responseBody, new TypeToken<List<App>>() {
        }.getType());
        for (App app : appList) {
            Log.d(TAG, "id is " + app.getId());
            Log.d(TAG, "name is " + app.getName());
            Log.d(TAG, "version is " + app.getVersion());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }
}
