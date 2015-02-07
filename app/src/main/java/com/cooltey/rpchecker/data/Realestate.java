package com.cooltey.rpchecker.data;

import com.cooltey.rpchecker.util.LogFactory;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class RealEstate {
	public String realestate_price_date;
    public String realestate_price_date_text;
    public String realestate_max_price;
    public String realestate_min_price;
    public String realestate_total_size;
    public String realestate_carpool_size;
    public String realestate_trades_num;
    public String realestate_city;
    public String realestate_city_id;
    public String realestate_district;
    public String realestate_org_name;
    public String realestate_finish_date;
    public String realestate_address;
    public String realestate_levels;
    public String realestate_build_type;
    public String realestate_carpool_types;
    public ArrayList<RealEstateTradeDetail> realestate_trades_detail = new ArrayList<RealEstateTradeDetail>();

    public static RealEstate fromJSON(String json) {
        RealEstate data = null;
        try {
            JSONObject mainObject = new JSONObject(json);
            data = new RealEstate();
            data.realestate_price_date        = mainObject.getString("realestate_price_date");
            data.realestate_price_date_text        = mainObject.getString("realestate_price_date_text");
            data.realestate_max_price        = mainObject.getString("realestate_max_price");
            data.realestate_min_price        = mainObject.getString("realestate_min_price");
            data.realestate_total_size        = mainObject.getString("realestate_total_size");
            data.realestate_carpool_size        = mainObject.getString("realestate_carpool_size");
            data.realestate_trades_num       = mainObject.getString("realestate_trades_num");
            data.realestate_city             = mainObject.getString("realestate_city");
            data.realestate_city_id           = mainObject.getString("realestate_city_id");
            data.realestate_district           = mainObject.getString("realestate_district");
            data.realestate_org_name         = mainObject.getString("realestate_org_name");
            data.realestate_finish_date        = mainObject.getString("realestate_finish_date");
            data.realestate_address           = mainObject.getString("realestate_address");
            data.realestate_levels            = mainObject.getString("realestate_levels");
            data.realestate_build_type        = mainObject.getString("realestate_build_type");
            data.realestate_carpool_types     = mainObject.getString("realestate_carpool_types");

            JSONArray subObject = new JSONArray(mainObject.getString("realestate_trades_detail"));
            for(int i = 0; i < subObject.length(); i++){
                RealEstateTradeDetail tmpOption = RealEstateTradeDetail.fromJSON(subObject.getString(i));
                data.realestate_trades_detail.add(tmpOption);
            }
        } catch (Exception e) {
            LogFactory.set("RealEstate Util Error", e);
        }
        return data;
    }
}
