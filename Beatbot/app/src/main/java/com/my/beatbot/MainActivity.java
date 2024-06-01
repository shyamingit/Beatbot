package com.my.beatbot;

import android.Manifest;
import android.animation.*;
import android.app.*;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.*;
import android.content.pm.PackageManager;
import android.content.res.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.media.*;
import android.media.MediaPlayer;
import android.net.*;
import android.os.*;
import android.text.*;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.style.*;
import android.util.*;
import android.view.*;
import android.view.View;
import android.view.View.*;
import android.view.animation.*;
import android.webkit.*;
import android.widget.*;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import de.hdodenhof.circleimageview.*;
import java.io.*;
import java.text.*;
import java.util.*;
import java.util.HashMap;
import java.util.regex.*;
import org.json.*;

public class MainActivity extends Activity {
	
	private String url_string = "";
	private HashMap<String, Object> map = new HashMap<>();
	private String filePath = "";
	private String fileName = "";
	
	private LinearLayout linear1;
	private LinearLayout headtab;
	private ScrollView vscroll1;
	private CircleImageView circleimageview1;
	private TextView textview1;
	private LinearLayout linear4;
	private LinearLayout linear6;
	private LinearLayout linear7;
	private LinearLayout genete;
	private TextView plswait;
	private LinearLayout download;
	private TextView textview3;
	private TextView textview2;
	private ImageView clear;
	private EditText url;
	private TextView gentext;
	private ProgressBar progressbar1;
	private TextView downlaod;
	private ImageView imageview1;
	
	private RequestNetwork req;
	private RequestNetwork.RequestListener _req_request_listener;
	private MediaPlayer mg;
	
	@Override
	protected void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		setContentView(R.layout.main);
		initialize(_savedInstanceState);
		
		if (Build.VERSION.SDK_INT >= 23) {
			if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
			||checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
				requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
			} else {
				initializeLogic();
			}
		} else {
			initializeLogic();
		}
	}
	
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == 1000) {
			initializeLogic();
		}
	}
	
	private void initialize(Bundle _savedInstanceState) {
		linear1 = findViewById(R.id.linear1);
		headtab = findViewById(R.id.headtab);
		vscroll1 = findViewById(R.id.vscroll1);
		circleimageview1 = findViewById(R.id.circleimageview1);
		textview1 = findViewById(R.id.textview1);
		linear4 = findViewById(R.id.linear4);
		linear6 = findViewById(R.id.linear6);
		linear7 = findViewById(R.id.linear7);
		genete = findViewById(R.id.genete);
		plswait = findViewById(R.id.plswait);
		download = findViewById(R.id.download);
		textview3 = findViewById(R.id.textview3);
		textview2 = findViewById(R.id.textview2);
		clear = findViewById(R.id.clear);
		url = findViewById(R.id.url);
		gentext = findViewById(R.id.gentext);
		progressbar1 = findViewById(R.id.progressbar1);
		downlaod = findViewById(R.id.downlaod);
		imageview1 = findViewById(R.id.imageview1);
		req = new RequestNetwork(this);
		
		genete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				if (url.getText().toString().equals("")) {
					SketchwareUtil.showMessage(getApplicationContext(), "Enter music description!");
				}
				else {
					progressbar1.setVisibility(View.VISIBLE);
					download.setVisibility(View.GONE);
					gentext.setVisibility(View.GONE);
					plswait.setVisibility(View.VISIBLE);
					map = new HashMap<>();
					map.put("prompt", url.getText().toString());
					map.put("vibe", "Chill");
					map.put("duration", 30);
					req.setParams(map, RequestNetworkController.REQUEST_BODY);
					req.startRequestNetwork(RequestNetworkController.POST, "https://www.veed.io/text-to-music-ap/api/text-to-music", "", _req_request_listener);
				}
			}
		});
		
		download.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				_DownloadFile(textview3.getText().toString(), "/Download/");
			}
		});
		
		clear.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				url.setText("");
				clear.setVisibility(View.GONE);
			}
		});
		
		url.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence _param1, int _param2, int _param3, int _param4) {
				final String _charSeq = _param1.toString();
				if (_charSeq.length() > 0) {
					clear.setVisibility(View.VISIBLE);
				}
				else {
					clear.setVisibility(View.GONE);
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence _param1, int _param2, int _param3, int _param4) {
				
			}
			
			@Override
			public void afterTextChanged(Editable _param1) {
				
			}
		});
		
		_req_request_listener = new RequestNetwork.RequestListener() {
			@Override
			public void onResponse(String _param1, String _param2, HashMap<String, Object> _param3) {
				final String _tag = _param1;
				final String _response = _param2;
				final HashMap<String, Object> _responseHeaders = _param3;
				SketchwareUtil.showMessage(getApplicationContext(), _responseHeaders.get("x-mubert-download-link").toString());
				textview3.setText(_responseHeaders.get("x-mubert-download-link").toString());
				progressbar1.setVisibility(View.GONE);
				download.setVisibility(View.VISIBLE);
				gentext.setVisibility(View.VISIBLE);
				plswait.setVisibility(View.GONE);
				url_string = textview3.getText().toString();
				mg = new MediaPlayer();
				mg.setAudioStreamType(3);
				try {
					mg.setDataSource(url_string);
				}catch (IllegalArgumentException e) {
					Toast.makeText(getApplicationContext(), "Unknown URL!", 1).show();
				} catch (SecurityException e2) {
					Toast.makeText(getApplicationContext(), "Unknown URL!", 1).show(); }
				catch (IllegalStateException e3) {
					Toast.makeText(getApplicationContext(), "Unknown URL!", 1).show(); }
				catch (java.io.IOException e4)
				{
					e4.printStackTrace();
				}
				try
				{
					mg.prepare();
				}
				catch (IllegalStateException e5)
				{
					Toast.makeText(getApplicationContext(), "Unknown URL!", 1).show();
				}
				catch (java.io.IOException e6)
				{
					Toast.makeText(getApplicationContext(), "Unknown URL!", 1).show();
				}
				mg.start();
			}
			
			@Override
			public void onErrorResponse(String _param1, String _param2) {
				final String _tag = _param1;
				final String _message = _param2;
				
			}
		};
	}
	
	private void initializeLogic() {
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
			Window w =MainActivity.this.getWindow();
			w.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS); w.setStatusBarColor(0xFF0D47A1);
		}
		linear1.setBackground(new GradientDrawable(GradientDrawable.Orientation.BR_TL, new int[] {0xFF9C27B0,0xFF0D47A1}));
		headtab.setElevation((float)50);
		genete.setBackground(new GradientDrawable(GradientDrawable.Orientation.BR_TL, new int[] {0xFF3949AB,0xFF1565C0}));
		genete.setBackground(new GradientDrawable() { public GradientDrawable getIns(int a, int b) { this.setCornerRadius(a); this.setColor(b); return this; } }.getIns((int)90, 0xFF1565C0));
		url.setBackground(new GradientDrawable() { public GradientDrawable getIns(int a, int b) { this.setCornerRadius(a); this.setColor(b); return this; } }.getIns((int)45, 0xFFFAFAFA));
		genete.setElevation((float)15);
		url.setElevation((float)15);
		download.setBackground(new GradientDrawable(GradientDrawable.Orientation.BR_TL, new int[] {0xFF3949AB,0xFF1565C0}));
		download.setBackground(new GradientDrawable() { public GradientDrawable getIns(int a, int b) { this.setCornerRadius(a); this.setColor(b); return this; } }.getIns((int)90, 0xFF1565C0));
		download.setElevation((float)15);
		
		progressbar1.setVisibility(View.GONE);
		plswait.setVisibility(View.GONE);
		clear.setVisibility(View.GONE);
	}
	
	public void _DownloadFile(final String _url, final String _path) {
		FileUtil.makeDir(FileUtil.getPackageDataDir(getApplicationContext()));
		android.net.ConnectivityManager connMgr = (android.net.ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		android.net.NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			
			
			final String urlDownload = _url;
			
			DownloadManager.Request request = new DownloadManager.Request(Uri.parse(urlDownload));
			
			final String fileName = URLUtil.guessFileName(urlDownload, null, null);
			
			request.setDescription("Downloader - " + urlDownload);
			
			request.setTitle(fileName);
			
			request.allowScanningByMediaScanner();
			
			request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
			
			request.setDestinationInExternalPublicDir(_path, fileName);
			
			final DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
			
			final long downloadId = manager.enqueue(request);
			
			final ProgressDialog prog = new ProgressDialog(this);
			prog.setMax(100);
			prog.setIndeterminate(true);
			prog.setCancelable(false);
			prog.setCanceledOnTouchOutside(false);
			prog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			prog.setTitle("Downloader");
			
			new Thread(new Runnable() {
				@Override
				public void run() {
					
					boolean downloading = true;
					
					while (downloading) {
						
						DownloadManager.Query q = new DownloadManager.Query();
						
						q.setFilterById(downloadId);
						
						android.database.Cursor cursor = manager.query(q);
						
						cursor.moveToFirst();
						
						int bytes_downloaded = cursor.getInt(cursor .getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
						
						int bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
						
						if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
							
							downloading = false;
							
						}
						
						final int dl_progress = (int) ((bytes_downloaded * 100l) / bytes_total);
						
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								
								prog.setTitle("Downloading...");
								prog.setMessage("Downloading " + fileName + ".\n\nProgress - " + dl_progress + "%");
								prog.show();
								if (dl_progress == 100) {
									
									
									filePath = _path.concat(fileName);
									showMessage("Downloaded in" + filePath );
									prog.dismiss();
								}
								
							} });
					} } }).start();
			
			
		} else {
			showMessage("No Internet Connection.");
		}
	}
	
	
	@Deprecated
	public void showMessage(String _s) {
		Toast.makeText(getApplicationContext(), _s, Toast.LENGTH_SHORT).show();
	}
	
	@Deprecated
	public int getLocationX(View _v) {
		int _location[] = new int[2];
		_v.getLocationInWindow(_location);
		return _location[0];
	}
	
	@Deprecated
	public int getLocationY(View _v) {
		int _location[] = new int[2];
		_v.getLocationInWindow(_location);
		return _location[1];
	}
	
	@Deprecated
	public int getRandom(int _min, int _max) {
		Random random = new Random();
		return random.nextInt(_max - _min + 1) + _min;
	}
	
	@Deprecated
	public ArrayList<Double> getCheckedItemPositionsToArray(ListView _list) {
		ArrayList<Double> _result = new ArrayList<Double>();
		SparseBooleanArray _arr = _list.getCheckedItemPositions();
		for (int _iIdx = 0; _iIdx < _arr.size(); _iIdx++) {
			if (_arr.valueAt(_iIdx))
			_result.add((double)_arr.keyAt(_iIdx));
		}
		return _result;
	}
	
	@Deprecated
	public float getDip(int _input) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, _input, getResources().getDisplayMetrics());
	}
	
	@Deprecated
	public int getDisplayWidthPixels() {
		return getResources().getDisplayMetrics().widthPixels;
	}
	
	@Deprecated
	public int getDisplayHeightPixels() {
		return getResources().getDisplayMetrics().heightPixels;
	}
}