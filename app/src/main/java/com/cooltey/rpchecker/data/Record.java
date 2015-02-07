package com.cooltey.rpchecker.data;

import com.cooltey.rpchecker.util.LogFactory;

import org.json.JSONObject;

public class Record {
	public String record_id;
    public String record_city_id;
    public String record_city;
    public String record_district;
    public String record_org_name;
    public String record_address;
    public String record_address_number;
    public String record_address_img;
    public int record_is_available;

    public static Record fromJSON(String json) {
        Record data = null;
        try {
            JSONObject mainObject = new JSONObject(json);
            data = new Record();
            data.record_id 		             = mainObject.getString("record_id");
            data.record_city_id 	             = mainObject.getString("record_city_id");
            data.record_city 	             = mainObject.getString("record_city");
            data.record_district 	             = mainObject.getString("record_district");
            data.record_org_name    	     = mainObject.getString("record_org_name");
            data.record_address 	         = mainObject.getString("record_address");
            data.record_address_number 	 = mainObject.getString("record_address_number");
            data.record_address_img 	     = mainObject.getString("record_address_img");
            data.record_is_available 	     = mainObject.getInt("record_is_available");
            
        } catch (Exception e) {
            LogFactory.set("Record Util Error", e);
        }
        return data;
    }
}
