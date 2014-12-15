package com.fluger.app.armenia.util;

import java.util.ArrayList;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.fluger.app.armenia.R;

public class DetailsAdapter extends ArrayAdapter<String> {
	
	private Context context;

	public DetailsAdapter(Context context, int resource, ArrayList<String> objects) {
		super(context, resource, objects);
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.item_details_list, parent, false);
		}
		if (position == 0) {
			((TextView) convertView.findViewById(R.id.item_name)).setText(getItem(position));
		} else if (position == 1) {
			((TextView) convertView.findViewById(R.id.item_name)).setVisibility(View.GONE);
			String[] tags = getItem(position).split(",");
			for (int i = 0; i < tags.length; i++) {
				TextView txtView = new TextView(context);
				txtView.setBackgroundResource(R.drawable.details_tag_background);
				txtView.setText("#" + tags[i]);
				txtView.setPadding(10, 10, 10, 10);
				txtView.setTextColor(context.getResources().getColor(R.color.grey_2));
				txtView.setGravity(Gravity.CENTER);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
			    params.setMargins(5, 5, 5, 5);
			    params.gravity = Gravity.LEFT;
			    txtView.setLayoutParams(params);
			    ((LinearLayout) convertView).addView(txtView);
			}
		} else if (position == 2) {
			((TextView) convertView.findViewById(R.id.item_name)).setText(getItem(position));
		} else if (position == 3) {
			((TextView) convertView.findViewById(R.id.item_name)).setText(getItem(position));
		} else if (position == 4) {
			((TextView) convertView.findViewById(R.id.item_name)).setVisibility(View.GONE);
			((RatingBar) convertView.findViewById(R.id.item_rating_bar)).setVisibility(View.VISIBLE);
		}

		return convertView;
	}

}
