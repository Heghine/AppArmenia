package com.fluger.app.armenia.fragment;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.fluger.app.armenia.HomeActivity;
import com.fluger.app.armenia.R;
import com.fluger.app.armenia.activity.details.NotificationDetailsActivity;
import com.fluger.app.armenia.activity.details.RingtonesDetailsActivity;
import com.fluger.app.armenia.data.AppCategory;
import com.fluger.app.armenia.data.AppCategoryItemData;
import com.fluger.app.armenia.manager.AppArmeniaManager;
import com.fluger.app.armenia.util.Constants;
import com.fluger.app.armenia.util.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;

public class AppCategoriesFragment extends Fragment {
	
	public static final String ARG_POSITION = "position";
	
	private ExpandableListView listView;
	
	private int position;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_app_categories, container, false);
		
		listView = (ExpandableListView) rootView.findViewById(R.id.listView);
		listView.setGroupIndicator(null);
		AppCategoriesAdapter adapter = new AppCategoriesAdapter(getActivity(), AppArmeniaManager.getInstance().categoriesTrendingData);
		listView.setAdapter(adapter);
		
		for (int i = 0; i < adapter.getGroupCount(); i++) {
			if (!AppArmeniaManager.getInstance().categoriesTrendingData.get(i).children.isEmpty()) {
				AppArmeniaManager.getInstance().categoriesTrendingData.get(i).childrenToShow = AppArmeniaManager.getInstance().categoriesTrendingData.get(i).children.subList(0, 3); 
				listView.expandGroup(i);
			}
		}
		
		return rootView;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		position = getArguments().getInt(ARG_POSITION);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		getActivity().setTitle(getActivity().getResources().getString(R.string.app_name));
		getActivity().getActionBar().setIcon(Utils.getIconIdByMenuPosition(position));
		((HomeActivity) getActivity()).getmDrawerToggle().setDrawerIndicatorEnabled(true);
	}

	public class AppCategoriesAdapter extends BaseExpandableListAdapter {

		private final SparseArray<AppCategory> categories;
		public LayoutInflater inflater;
		public Activity activity;

		public AppCategoriesAdapter(Activity act, SparseArray<AppCategory> groups) {
			activity = act;
			this.categories = groups;
			inflater = act.getLayoutInflater();
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return categories.get(groupPosition).childrenToShow.get(childPosition);
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
			final AppCategoryItemData child = (AppCategoryItemData) getChild(groupPosition, childPosition);
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.item_app_category, parent, false);
			}
			((TextView) convertView.findViewById(R.id.app_category_item_name)).setText(child.title);
			((TextView) convertView.findViewById(R.id.app_category_item_download)).setText("Downloads: " + child.downloadCount);
			((RatingBar) convertView.findViewById(R.id.app_category_item_rating)).setRating(child.rating);
			if (child.category != Constants.RINGTONES_CATEGORY_POSITION && child.category != Constants.NOTIFICATIONS_CATEGORY_POSITION) {
				ImageView imageView = (ImageView) convertView.findViewById(R.id.app_category_img);
				String url = Constants.FILES_URL + child.imageUrl;
				ImageSize targetSize = new ImageSize(70, 70);
				ImageLoader.getInstance().loadImage(url, targetSize, null);
				ImageLoader.getInstance().displayImage(url, imageView, AppArmeniaManager.getInstance().options);
			}
			
			((ImageButton) convertView.findViewById(R.id.arrow_img_btn)).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (child.category == Constants.RINGTONES_CATEGORY_POSITION) {
						AppArmeniaManager.getInstance().itemDataToBePassed = child;
						Intent ringtonesDetailsActivity = new Intent(AppCategoriesFragment.this.getActivity(), RingtonesDetailsActivity.class);
						ringtonesDetailsActivity.putExtra(HomeActivity.POSITION, Constants.RINGTONES_MENU_POSITION);
						startActivity(ringtonesDetailsActivity);
					} else if (child.category == Constants.NOTIFICATIONS_CATEGORY_POSITION) {
						AppArmeniaManager.getInstance().itemDataToBePassed = child;
						Intent ringtonesDetailsActivity = new Intent(AppCategoriesFragment.this.getActivity(), NotificationDetailsActivity.class);
						ringtonesDetailsActivity.putExtra(HomeActivity.POSITION, Constants.NOTIFICATIONS_MENU_POSITION);
						startActivity(ringtonesDetailsActivity);
					}
				}
			});
			return convertView;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return categories.get(groupPosition).childrenToShow.size();
		}

		@Override
		public Object getGroup(int groupPosition) {
			return categories.get(groupPosition);
		}

		@Override
		public int getGroupCount() {
			return categories.size();
		}

		@Override
		public void onGroupCollapsed(int groupPosition) {
			super.onGroupCollapsed(groupPosition);
		}

		@Override
		public void onGroupExpanded(int groupPosition) {
			super.onGroupExpanded(groupPosition);
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.group_app_category, parent, false);
			}
			final AppCategory category = (AppCategory) getGroup(groupPosition);

			((RelativeLayout) convertView.findViewById(R.id.app_category_group_container)).setBackgroundResource(Utils.getColorByIndex(groupPosition));

			((TextView) convertView.findViewById(R.id.app_category_group_name)).setText(category.name);
			((TextView) convertView.findViewById(R.id.app_category_group_name)).setCompoundDrawablesWithIntrinsicBounds(Utils.getIconIdByIndex(groupPosition + 1), 0, 0, 0);
			
			int imageButtonCount = AppArmeniaManager.getInstance().categoriesTrendingData.get(groupPosition).children.size() / 3;
			if (imageButtonCount > 5)
				imageButtonCount = 5;
			for (int i = imageButtonCount - 1; i >= 0; i--) {
				final int iFinal = i;
				((ImageButton) ((LinearLayout) convertView.findViewById(R.id.app_category_group_slider_container)).getChildAt(i)).setVisibility(View.VISIBLE);
				((ImageButton) ((LinearLayout) convertView.findViewById(R.id.app_category_group_slider_container)).getChildAt(i)).setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						LinearLayout parent = ((LinearLayout) v.getParent());
						for (int i = 0; i < parent.getChildCount(); i++) {
							((ImageButton) parent.getChildAt(i)).setImageResource(R.drawable.ic_slider_inactive);
						}
						((ImageButton) v).setImageResource(R.drawable.ic_slider_active);
						category.childrenToShow = AppArmeniaManager.getInstance().categoriesTrendingData.get(groupPosition).children.subList(iFinal, iFinal + 3);
						
						if (listView.isGroupExpanded(groupPosition)) {
							((AppCategoriesAdapter) listView.getExpandableListAdapter()).notifyDataSetChanged();
						} else {
							listView.expandGroup(groupPosition, true);
						}
					}
				});
			}
			
			((TextView) convertView.findViewById(R.id.app_category_group_name)).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					int menuPosition = Utils.getMenuPositionBasedOnCategoryIndex(groupPosition + 1);
					((HomeActivity) AppCategoriesFragment.this.getActivity()).selectItemWithActivity(menuPosition);
				}
			});
			
			return convertView;
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}
	}

}
