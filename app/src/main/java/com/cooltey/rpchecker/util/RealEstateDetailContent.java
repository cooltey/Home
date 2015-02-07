package com.cooltey.rpchecker.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cooltey.rpchecker.R;
import com.cooltey.rpchecker.data.RealEstate;


public class RealEstateDetailContent {

    private static TipDialog mTipDialog;

	public static void show(Context context, RealEstate realEstateData, String carpoolPriceSummary){
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        // get data
        RealEstate tmpData = realEstateData;
        if(tmpData.realestate_org_name.length() > 1) {
            View dialogView = layoutInflater.inflate(R.layout.dialog_org_detail, null);
            LinearLayout dialogContainer = (LinearLayout) dialogView.findViewById(R.id.org_detail_content);

            // set data array
            String[] columnName = {context.getString(R.string.tip_org_column_1),
                    context.getString(R.string.tip_org_column_2),
                    context.getString(R.string.tip_org_column_3),
                    context.getString(R.string.tip_org_column_4),
                    context.getString(R.string.tip_org_column_5),
                    context.getString(R.string.tip_org_column_6),
                    context.getString(R.string.tip_org_column_7)};

            // divide date
            String finalDate = tmpData.realestate_finish_date.substring(0, 2) + "/" + tmpData.realestate_finish_date.substring(2, 4) + "/" + tmpData.realestate_finish_date.substring(4, 6);
            if(tmpData.realestate_finish_date.length() > 6){
                finalDate  = tmpData.realestate_finish_date.substring(0, 3) + "/" + tmpData.realestate_finish_date.substring(3, 5) + "/" + tmpData.realestate_finish_date.substring(5, 7);
            }

            String[] columnValue = {tmpData.realestate_city,
                    tmpData.realestate_district,
                    tmpData.realestate_org_name,
                    tmpData.realestate_finish_date.substring(0, 2) + "/" + tmpData.realestate_finish_date.substring(2, 4) + "/" + tmpData.realestate_finish_date.substring(4, 6),
                    tmpData.realestate_address + context.getString(R.string.tip_org_column_5_lastfix),
                    tmpData.realestate_levels + context.getString(R.string.tip_org_column_6_lastfix),
                    tmpData.realestate_build_type};

            // get content
            for (int i = 0; i < columnName.length; i++) {
                View rootView = layoutInflater.inflate(R.layout.item_tip_org, null);

                TextView textLeft = (TextView) rootView.findViewById(R.id.text);
                TextView textRight = (TextView) rootView.findViewById(R.id.text2);

                textLeft.setText(columnName[i]);
                textRight.setText(columnValue[i]);

                dialogContainer.addView(rootView);
            }

            //  get carpool type
            String[] carpoolType = tmpData.realestate_carpool_types.split(",");
            String[] carpoolPrice = carpoolPriceSummary.split(",");

            LogFactory.set("carpoolType", carpoolType.length);
            LogFactory.set("carpoolPrice", carpoolPrice.length);
            for (int j = 0; j < carpoolType.length; j++) {
                View rootView = layoutInflater.inflate(R.layout.item_tip_org, null);

                TextView textLeft = (TextView) rootView.findViewById(R.id.text);
                TextView textRight = (TextView) rootView.findViewById(R.id.text2);

                textLeft.setText(carpoolType[j] + context.getString(R.string.tip_org_column_8_divider));
                textRight.setText(NumberFormatter.get(context, carpoolPrice[j], 0));

                dialogContainer.addView(rootView);
            }

            // tip dialog
            if(mTipDialog == null) {
                mTipDialog = new TipDialog(context);
                mTipDialog.show(dialogView);
            }else{
                mTipDialog.close();
                mTipDialog.show(dialogView);
            }
        }else{
            // tip dialog
            if(mTipDialog == null) {
                mTipDialog = new TipDialog(context);
                mTipDialog.showPlain(context.getString(R.string.tip_date_range_empty));
            }else{
                mTipDialog.close();
                mTipDialog.showPlain(context.getString(R.string.tip_date_range_empty));
            }
        }


	}
}
