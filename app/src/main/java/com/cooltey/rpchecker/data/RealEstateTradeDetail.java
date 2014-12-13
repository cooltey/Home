package com.cooltey.rpchecker.data;

import com.cooltey.rpchecker.util.LogFactory;

import org.json.JSONObject;

public class RealEstateTradeDetail {
	public String detail_date;
    public String detail_total_price;
    public String detail_price_per_unit;
    public String detail_levels;
    public String detail_total_size;
    public String detail_carpool_size;
    public String detail_extra;

    public static RealEstateTradeDetail fromJSON(String json) {
        RealEstateTradeDetail data = null;
        try {
            JSONObject mainObject = new JSONObject(json);
            data = new RealEstateTradeDetail();
            data.detail_date        = mainObject.getString("detail_date");
            data.detail_total_price        = mainObject.getString("detail_total_price");
            data.detail_price_per_unit        = mainObject.getString("detail_price_per_unit");
            data.detail_levels        = mainObject.getString("detail_levels");
            data.detail_total_size       = mainObject.getString("detail_total_size");
            data.detail_carpool_size = mainObject.getString("detail_carpool_size");
            data.detail_extra       = mainObject.getString("detail_extra");
            
        } catch (Exception e) {
            LogFactory.set("RealEstateTradeDetail Util Error", e);
        }
        return data;
    }
}
