package com.cooltey.rpchecker.data;

import com.cooltey.rpchecker.util.LogFactory;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchHistory {
	public String search_history_id;
    public String search_history_record_id;
    public String search_history_date;
    public String search_history_org_name;
    public String search_history_address;
    public String search_history_city;
    public String search_history_district;
    public ArrayList<Record> search_history_record_data = new ArrayList<Record>();

    public static SearchHistory fromJSON(String json) {
        SearchHistory data = null;
        try {
            JSONObject mainObject = new JSONObject(json);
            data = new SearchHistory();
            data.search_history_id             = mainObject.getString("search_history_id");
            data.search_history_record_id      = mainObject.getString("search_history_record_id");
            data.search_history_date           = mainObject.getString("search_history_date");
            data.search_history_org_name      = mainObject.getString("search_history_org_name");
            data.search_history_address         = mainObject.getString("search_history_address");
            data.search_history_city            = mainObject.getString("search_history_city");
            data.search_history_district         = mainObject.getString("search_history_district");

            JSONArray subObject = new JSONArray(mainObject.getString("search_history_record_data"));
            for(int i = 0; i < subObject.length(); i++){
                Record tmpOption = Record.fromJSON(subObject.getString(i));
                data.search_history_record_data.add(tmpOption);
            }
        } catch (Exception e) {
            LogFactory.set("SearchHistory Util Error", e);
        }
        return data;
    }
}
