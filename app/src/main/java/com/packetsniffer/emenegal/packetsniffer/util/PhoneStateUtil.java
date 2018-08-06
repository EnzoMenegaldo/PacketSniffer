package com.packetsniffer.emenegal.packetsniffer.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

public class PhoneStateUtil {

    public static final PhoneStateUtil INSTANCE = new PhoneStateUtil();

    private PhoneStateUtil(){};

    /** check whether network is connected or not
     *  @return boolean
     */
    public boolean isConnectedToInternet(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo networkInfo = connectivity.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                return true;
            }
        }
        return false;
    }

    /**
     * check a network interface by name
     *
     * @param networkInterfaceName Network interface Name on Linux, for example tun0
     * @return true if interface exists and is active
     * @throws Exception throws Exception
     */
    public boolean checkForActiveInterface(String networkInterfaceName) throws Exception {
        List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
        for (NetworkInterface networkInterface : interfaces) {
            if (networkInterface.getName().equals(networkInterfaceName)) {
                return networkInterface.isUp();
            }
        }
        return false;
    }
}
