package com.wsq.syllabus.note;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wsq.syllabus.R;

public class NoteCatalogAdapter extends BaseAdapter {

	private List<NoteCatalogEntity> list;
	private Context context;

	public NoteCatalogAdapter(Context context, List<NoteCatalogEntity> list) {
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (null == convertView) {
			viewHolder = new ViewHolder();
			LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_note_catalog, null);
            
			viewHolder.tvName = (TextView) convertView
					.findViewById(R.id.tv_item_notecatalog_name);
			viewHolder.tvCount = (TextView) convertView
					.findViewById(R.id.tv_item_notecatalog_count);

    		convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		String name = list.get(position).getName();
		String count = "条目：" + list.get(position).getCount();
		
		viewHolder.tvName.setText(name);
		viewHolder.tvCount.setText(count);
		
		return convertView;
	}

	static class ViewHolder {
		TextView tvName;
		TextView tvCount;
	}
}
