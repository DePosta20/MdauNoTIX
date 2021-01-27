package mdausoft.co.tz.mdaunotix;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.text.Layout;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import mdausoft.co.tz.mdaunotix.services.ConnectivityReceiver;

import static androidx.core.content.ContextCompat.getSystemService;

public class Function {
    public void make_toast(Context activityContext, String textStr) {
        Toast.makeText(activityContext, textStr, Toast.LENGTH_SHORT).show();
    }
    ////////////////////////////////////////////////////////////////
    public void checkConnection(Context ctx) {
        boolean isConnected = ConnectivityReceiver.isConnected();
        if (!isConnected) {
            make_toast(ctx, "Sorry! Not connected to internet");
        }
    }
    ////////////////////////////////////////////////////////////////
    public String md5(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte[] messageDigest = digest.digest();
            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (int i = 0; i < messageDigest.length; i++) {
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    ////////////////////////////////////////////////////////////////
    public JSONObject add_to_json(JSONObject jsonObj, String name, String value) throws JSONException {
        return jsonObj.put(name, value);
    }

    ////////////////////////////////////////////////////////////////
    public String base64_decrypt(String s) {
        byte[] decodeValue = android.util.Base64.decode(s.getBytes(), android.util.Base64.DEFAULT);
        return new String(decodeValue);
    }

    /////////////////////////////////////////////////////////////////
    public String base64_encrypt(String s) {
        byte[] encodeValue = android.util.Base64.encode(s.getBytes(), android.util.Base64.DEFAULT);
        return new String(encodeValue);
    }

    /////////////////////////////////////////////////////////////////////////////////////////
    public static String get_today_date() {
        Date dNow = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
        return ft.format(dNow);
    }

    /////////////////////////////////////////////////////////////////////////////////////////
    public static String get_date_time() {
        Date dNow = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd H:mm:ss");
        return ft.format(dNow);
    }

    ///////////////////////////////////////////////////////////////////////////////////////
    public String insert_comma(String s) {
        try {
            Double d = Double.valueOf(s);
            NumberFormat myFormat = NumberFormat.getInstance();
            myFormat.setGroupingUsed(true);
            return String.valueOf(myFormat.format(d));
        } catch (Exception e) {
            return "";
        }
    }

    /////////////////////////////////////////////////////////////////////////////////
    public String remove_HTML_Tags(String s) {
        s = s.replaceAll("\\<.*?\\>", "");
        s = s.replaceAll("\\&.*?\\;", "");
        s = remove_htm_without_regex(s);
        return s;
    }

    /////////////////////////////////////////////////////////////
    public String remove_htm_without_regex(String inp) {
        boolean intag = false;
        String outp = "";
        for (int i = 0; i < inp.length(); ++i) {
            if (!intag && inp.charAt(i) == '<') {
                intag = true;
                continue;
            }
            if (intag && inp.charAt(i) == '>') {
                intag = false;
                continue;
            }
            if (!intag) {
                outp = outp + inp.charAt(i);
            }
        }
        return outp;
    }

    ////////////////////////////////////////////////////////////
    public boolean isInteger_str(String nnn) {
        Double number = Double.valueOf(nnn);
        return Math.ceil(number) == Math.floor(number);
    }

    ////////////////////////////////////////////////////////////
    public boolean isInteger(Double number) {
        return Math.ceil(number) == Math.floor(number);
    }
}
