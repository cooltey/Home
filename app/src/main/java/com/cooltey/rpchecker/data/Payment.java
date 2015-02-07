package com.cooltey.rpchecker.data;

import com.cooltey.rpchecker.util.LogFactory;

import org.json.JSONObject;

public class Payment {
	public String payment_serial_number;
    public String payment_create_time;
    public String payment_expire_time;

    public static Payment fromJSON(String json) {
        Payment data = null;
        try {
            JSONObject mainObject = new JSONObject(json);
            data = new Payment();
            data.payment_serial_number   = mainObject.getString("payment_serial_number");
            data.payment_create_time      = mainObject.getString("payment_create_time");
            data.payment_expire_time      = mainObject.getString("payment_expire_time");

        } catch (Exception e) {
            LogFactory.set("Payment Util Error", e);
        }
        return data;
    }
}
