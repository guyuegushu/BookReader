package guyuegushu.myownapp.wifiDirectConnection;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;

/**
 * Created by Administrator on 2017/3/28.
 */

public class WifiDirectReceiver extends BroadcastReceiver {

    private WifiP2pManager wifiP2pManager;
    private Channel wifiP2pChannel;
    private WifiActivity wifiActivity;
    private WifiP2pConfig wifiP2pConfig = new WifiP2pConfig();

    public WifiDirectReceiver(){}

    public WifiDirectReceiver(WifiP2pManager manager, Channel channel, WifiActivity activity){
        this.wifiP2pManager = manager;
        this.wifiP2pChannel = channel;
        this.wifiActivity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)) {
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                //open
            } else if (state == WifiP2pManager.WIFI_P2P_STATE_DISABLED){
                //close
            } else if (action.equals(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)){

            } else if (action.equals(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)){

            }
        }
    }
}
