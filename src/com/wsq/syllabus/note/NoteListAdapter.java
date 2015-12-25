package com.wsq.syllabus.note;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.download.ImageDownloader.Scheme;
import com.wsq.syllabus.R;

public class NoteListAdapter extends BaseAdapter {

	/** 图片显示选项 */
	private DisplayImageOptions options;
	/** UIL实体 */
	private ImageLoader imageLoader;
	
	private Context context;
	private Cursor cursor;

	public NoteListAdapter(Context context, Cursor cursor) {
		this.context = context;
		this.cursor = cursor;
		
		// 初始化图片显示选项
		options = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.ic_launcher)
				.showImageOnFail(R.drawable.ic_launcher)
				.cacheInMemory(true)
				.cacheOnDisk(true)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
				.build();     
		
		// 获取单例
		imageLoader = ImageLoader.getInstance();
	}
	
	@Override
	public int getCount() {
		return cursor.getCount();
	}

	@Override
	public Object getItem(int position) {
		return cursor.getPosition();
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
            convertView = inflater.inflate(R.layout.item_note_list, null);
            
			viewHolder.tvContent = (TextView) convertView
					.findViewById(R.id.tv_item_note_list_content);
			viewHolder.tvTime = (TextView) convertView
					.findViewById(R.id.tv_item_note_list_time);
			viewHolder.ivPic = (ImageView) convertView
					.findViewById(R.id.iv_item_note_list_pic);
    		
    		convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		cursor.moveToPosition(position);
		String content = cursor.getString(cursor.getColumnIndex("content"));
		String time = cursor.getString(cursor.getColumnIndex("time"));
		String url = cursor.getString(cursor.getColumnIndex("image"));
		String url_video = cursor.getString(cursor.getColumnIndex("video"));

		viewHolder.tvContent.setText(content);
		viewHolder.tvTime.setText(time);
		if (!url.equals("null")) {
			imageLoader.displayImage(Scheme.FILE.wrap(url), viewHolder.ivPic, options);
//			viewHolder.ivPic.setImageBitmap(getImageThumbnail(url, 200, 200));
		}
		if (!url_video.equals("null"))
			viewHolder.ivPic.setImageBitmap(getVideoThumbnail(url_video, 200, 200,
					MediaStore.Images.Thumbnails.MICRO_KIND));
		
		return convertView;
	}

	static class ViewHolder {
		TextView tvContent;
		TextView tvTime;
		ImageView ivPic;
	}

	/**
	 * 获取压缩后的图片
	 * @param uri
	 * @param width
	 * @param height
	 * @return
	 */
	public Bitmap getImageThumbnail(String uri, int width, int height) {
		Bitmap bitmap = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		bitmap = BitmapFactory.decodeFile(uri, options); // get image
		options.inJustDecodeBounds = false;
		int beWidth = options.outWidth / width;
		int beHeight = options.outHeight / height;
		int be = 1;
		if (beWidth < beHeight) {
			be = beWidth;
		} else {
			be = beHeight;
		}
		if (be <= 0) {
			be = 1;
		}
		options.inSampleSize = be;
		bitmap = BitmapFactory.decodeFile(uri, options);
		bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
				ThumbnailUtils.OPTIONS_RECYCLE_INPUT);

		return bitmap;
	}

	/**
	 * 获取视频首帧图片
	 * @param uri
	 * @param width
	 * @param height
	 * @param kind
	 * @return
	 */
	public Bitmap getVideoThumbnail(String uri, int width, int height, int kind) {
		Bitmap bitmap = null;
		bitmap = ThumbnailUtils.createVideoThumbnail(uri, kind);
		bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
				ThumbnailUtils.OPTIONS_RECYCLE_INPUT);

		return bitmap;
	}
}
