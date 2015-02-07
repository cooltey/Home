package com.cooltey.rpchecker.data;

import com.cooltey.rpchecker.util.LogFactory;

import org.json.JSONObject;

public class CarpoolPrice {
	public int carpool_type_id;
    public String carpool_type_name;
    public int carpool_price;

    public static CarpoolPrice fromJSON(String json) {
        CarpoolPrice data = null;
        try {
            JSONObject mainObject = new JSONObject(json);
            data = new CarpoolPrice();
            data.carpool_type_id        = mainObject.getInt("carpool_type_id");
            data.carpool_type_name     = mainObject.getString("carpool_type_name");
            data.carpool_price          = mainObject.getInt("carpool_price");
            
        } catch (Exception e) {
            LogFactory.set("CarpoolPrice Util Error", e);
        }
        return data;
    }
}
