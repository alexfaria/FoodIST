package pt.ulisboa.tecnico.cmov.foodist.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import pt.inesc.termite.wifidirect.SimWifiP2pBroadcast;
import pt.ulisboa.tecnico.cmov.foodist.view.activities.MainActivity;

public class SimWifiP2pBroadcastReceiver extends BroadcastReceiver {

    private OnPeersChangedListener listener;

    public SimWifiP2pBroadcastReceiver(OnPeersChangedListener listener) {
        super();
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (SimWifiP2pBroadcast.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action))
            // Request available peers from the wifi p2p manager. This is an
            // asynchronous call and the calling activity is notified with a
            // callback on PeerListListener.onPeersAvailable()
            listener.onPeersChanged();
    }
}
