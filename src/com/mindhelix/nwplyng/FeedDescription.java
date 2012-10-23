package com.mindhelix.nwplyng;

import java.io.File;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FeedDescription extends Activity {
	
	String title = "", comment = "", itunes_preview_url = "", itunes_download_url = "", track_name = "", artist_name = "";
	String youtube_id = "", snapshot_file_name = "",user_name = "", created_at = "", place = "", thumbnail_url = "";
	String profile_image_url = "",career_progression = "", user_id = "", unique_id = "", feed_id = "", full_name = "";
	String last_played = "", video_name = "";
	
	String uploadTime = "",duration="";
	ImageView userImage , youtubeThumb, photo;
	TextView username, fullName, timeNLoc, commentTV;
	Button play, goiTunes;
	Button back;
	TextView songName, artistName, titleofVideo;
	TextView headline;
	TextView uploadDetails;
	
	RelativeLayout videoBlock, profileLayout;
	LinearLayout photoLayout, dynamLayout;
	
	Bitmap bitmap;
	
	public ImageLoader imageLoader;
	public ImageLoaderWithAspectRatio imageWithaspect;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feed_description);
		
		Bundle extras = this.getIntent().getExtras();
		
		title = extras.getString("title");
		comment = extras.getString("comment");
		itunes_preview_url = extras.getString("itunes_preview_url");
		itunes_download_url = extras.getString("itunes_download_url");
		track_name = extras.getString("track_name");
		artist_name = extras.getString("artist_name");
		youtube_id = extras.getString("youtube_id");
		snapshot_file_name = extras.getString("snapshot_filename");
		user_name = extras.getString("user_name");
		created_at = extras.getString("created_at");
		place = extras.getString("place");
		thumbnail_url = extras.getString("thumbnail_url");
		profile_image_url = extras.getString("profile_image_url");
		career_progression = extras.getString("career_progression");
		user_id = extras.getString("user_id");
		unique_id = extras.getString("unique_id");
		feed_id = extras.getString("feed_id");
		full_name = extras.getString("full_name");
		uploadTime = extras.getString("upload_time");
		duration = extras.getString("duration");
		last_played = extras.getString("last_played");
		video_name = extras.getString("video_name");
		
		
		initialiseWidgets();
		
		final Dialog dialog = new Dialog(FeedDescription.this);
		dialog.setContentView(R.layout.big_photo);
		dialog.setCancelable(true);
		final ImageView bigphoto = (ImageView)dialog.findViewById(R.id.big_photo_dialog);
		Button close = (Button)dialog.findViewById(R.id.closeBigPhoto);
		
		close.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				dialog.dismiss();
				
			}
		});
		
		
		if(!profile_image_url.equals("null")) {
			imageLoader.DisplayImage(profile_image_url, userImage);
		}
		
		if(!thumbnail_url.equals("null")) {
			imageLoader.DisplayImage(thumbnail_url, youtubeThumb);
			titleofVideo.setText(video_name);
			uploadDetails.setText(duration+" ("+uploadTime+")");
		}
		else {
			dynamLayout.removeView(videoBlock);
		}
		
		if(!snapshot_file_name.equals(ConstantURIs.MISSING_IMAGE)) {
			photoLayout.setVisibility(LinearLayout.VISIBLE);
			photo.setVisibility(ImageView.VISIBLE);
			imageWithaspect.DisplayImage(snapshot_file_name, photo);
		}
		
		username.setText(user_name);
		
		if(last_played.equals("true"))
			headline.setText("last played");
		else
			headline.setText(user_name);
		
		if(!full_name.equals("null")) {
			fullName.setText(full_name);
			
		}
		
		String time = CalculateTime.compareAndConvertDate(created_at);
		
		if(place.equals("null")||place.length()==0) {
			timeNLoc.setText(time);
		}
		else {
			timeNLoc.setText(time + " @ " + place);
		}
		
		songName.setText(track_name);
		artistName.setText(artist_name);
		
		if(!comment.equals("null")) {
			commentTV.setText("'"+comment+"'");
		}
		else
		{
			dynamLayout.removeViewAt(0);
		}
		
		profileLayout.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				Intent i = new Intent(FeedDescription.this, FriendsProfile.class);
				i.putExtra("user_id", user_id);
				startActivity(i);		
			}
		});
		
		videoBlock.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_VIEW,
						Uri.parse("http://www.youtube.com/watch?v="+youtube_id));
				
				if(isAppInstalled("com.google.android.youtube", FeedDescription.this)) {
                    intent.setClassName("com.google.android.youtube", "com.google.android.youtube.WatchActivity");
                }
				startActivity(intent);
			}
		});
		
		photoLayout.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				
				if(!snapshot_file_name.equals(ConstantURIs.MISSING_IMAGE)) {
					dialog.show();
					bitmap = imageWithaspect.getBitmap(snapshot_file_name);
					File file = imageWithaspect.GetFile();
					Bitmap rotated = checkImageOrientation(file, bitmap);
					if(rotated!=null)
					{
						Drawable d = new BitmapDrawable(rotated);
						bigphoto.setBackgroundDrawable(d);
					}
				}
				
			}
		});
		
		goiTunes.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				if(!itunes_download_url.equals("")) {
					final Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(itunes_download_url));
					startActivity(intent);
				}
			}
		});
		
		back.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				
				FeedDescription.this.finish();
				
			}
		});
		
	}
	
	public static boolean isAppInstalled(String uri, Context context) {
	    PackageManager pm = context.getPackageManager();
	    boolean installed = false;
	    try {
	        pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
	        installed = true;
	    } catch (PackageManager.NameNotFoundException e) {
	        installed = false;
	    }
	    return installed;
	}
	
	public Bitmap checkImageOrientation(File file, Bitmap bmp) {
		
		int orientation = 0;
		
		try {
       	 ExifInterface exif = new ExifInterface(file.getAbsolutePath());
       	 orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
       	 
       }catch (Exception e) {}
       
       switch (orientation) {
        case ExifInterface.ORIENTATION_ROTATE_270:
            return(RotateImage.rotate(bmp, 270));
        case ExifInterface.ORIENTATION_ROTATE_180:
       	 	return(RotateImage.rotate(bmp, 180));
        case ExifInterface.ORIENTATION_ROTATE_90:
       	 	return(RotateImage.rotate(bmp, 90));
        default:
        	return(RotateImage.rotate(bmp, 0));
        }
	}
	
	public void initialiseWidgets() {
		
		headline = (TextView)findViewById(R.id.feedDescriptionHeadline);
		
		userImage = (ImageView)findViewById(R.id.friend_user_image);
		youtubeThumb = (ImageView)findViewById(R.id.youtube_thumbnail);
		photo = (ImageView)findViewById(R.id.photo_from_feed);
		
		username = (TextView)findViewById(R.id.friendUserName);
		fullName = (TextView)findViewById(R.id.friendCompleteName);
		timeNLoc = (TextView)findViewById(R.id.time_n_Location_of_feed);
		commentTV = (TextView)findViewById(R.id.commentblock);
		songName = (TextView)findViewById(R.id.feed_song);
		artistName = (TextView)findViewById(R.id.feed_artist);
		
		goiTunes = (Button)findViewById(R.id.feed_itunes);
		back = (Button)findViewById(R.id.backButtonFeedDescription);
		
		videoBlock = (RelativeLayout)findViewById(R.id.videoBlock);
		profileLayout = (RelativeLayout)findViewById(R.id.profileLayout);
		photoLayout = (LinearLayout)findViewById(R.id.feedPhotoLayout);
		dynamLayout = (LinearLayout)findViewById(R.id.dynamicLayout);
		
		titleofVideo = (TextView)findViewById(R.id.titleofYTVideo);
		uploadDetails = (TextView)findViewById(R.id.uploadDetails);
		
		imageLoader = new ImageLoader(FeedDescription.this);
		
		imageWithaspect = new ImageLoaderWithAspectRatio(FeedDescription.this);
		
	}
	
}
