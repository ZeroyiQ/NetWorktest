package top.zeroyiq.networktest;

/**
 * Created by ZeroyiQ on 2017/7/20.
 */

public interface HttpCallbackListener {

    void onFinish(String response);

    void onError(Exception e);
}
