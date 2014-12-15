package com.fluger.app.armenia.fragment.details;

import java.util.ArrayList;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ActionBar.Tab;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.fluger.app.armenia.R;
import com.fluger.app.armenia.data.AppCategoryItemData;
import com.fluger.app.armenia.util.DetailsAdapter;

public class NotificationDetailsFragment extends Fragment implements ActionBar.TabListener {
	
	private AppCategoryItemData itemData;
	private RelativeLayout detailsView;
	private RelativeLayout previewView;
	
	public NotificationDetailsFragment(AppCategoryItemData data) {
		itemData = data;
		Log.d("heghine", "item = " + itemData.audioUrl);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_details_notification, container, false);
		
		detailsView = ((RelativeLayout) rootView.findViewById(R.id.details_container));
		previewView = ((RelativeLayout) rootView.findViewById(R.id.preview_container));
		
		ArrayList<String> details = new ArrayList<String>();
		details.add(itemData.title);
		details.add(itemData.tagsStr);
		details.add("Downloads: " + itemData.downloadCount);
		details.add("User: ");
		details.add("Rating");
		
		DetailsAdapter categoriesAdapter = new DetailsAdapter(getActivity(), R.layout.item_details_list, details);
		ListView detailsListView = ((ListView) rootView.findViewById(R.id.details_list));
		detailsListView.setAdapter(categoriesAdapter);
		
		((TextView) rootView.findViewById(R.id.set_as_notification_txt)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.d("heghine", "Set As Notification clicked");
			}
		});
		
		return rootView;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setHasOptionsMenu(true);
		getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
		ActionBar actionBar = getActivity().getActionBar();

		if (actionBar.getTabCount() == 0) {
			actionBar.addTab(actionBar.newTab().setText(R.string.tab_notifications_preview).setTabListener(this));
			actionBar.addTab(actionBar.newTab().setText(R.string.tab_notifications_details).setTabListener(this));
		}
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		getActivity().getActionBar().removeAllTabs();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			getActivity().onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		if (tab.getText().equals(getActivity().getResources().getString(R.string.tab_notifications_preview))) {
			detailsView.setVisibility(View.GONE);
			previewView.setVisibility(View.VISIBLE);
		} else if (tab.getText().equals(getActivity().getResources().getString(R.string.tab_notifications_details))) {
			detailsView.setVisibility(View.VISIBLE);
			previewView.setVisibility(View.GONE);
		}
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		
	}
}
