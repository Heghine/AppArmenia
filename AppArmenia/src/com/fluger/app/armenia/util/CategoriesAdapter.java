package com.fluger.app.armenia.util;

import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.fluger.app.armenia.R;
import com.fluger.app.armenia.data.TagItemData;

public class CategoriesAdapter extends ArrayAdapter<TagItemData> {
	private Context context;

	public CategoriesAdapter(Context context, int resource, ArrayList<TagItemData> objects) {
		super(context, resource, objects);
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.item_category_list, parent, false);
		}

		((TextView) convertView.findViewById(R.id.item_name)).setText(getItem(position).tag);
//		((TextView) convertView.findViewById(R.id.item_count)).setVisibility(View.VISIBLE);
//		((TextView) convertView.findViewById(R.id.item_count)).setText("15");

		return convertView;
	}
}