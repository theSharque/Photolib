package com.saturn7.app.photos.free;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Vector;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher.ViewFactory;

public class PhotolibraryActivity extends Activity implements
		View.OnClickListener, ViewFactory, OnTouchListener {
	private Vector< String > vImages;
	private Vector< String > vIcons;
	private int iCurrent;
	private int iPager = 1;
	private int iCat = 36;

	final String sPath = Environment.getExternalStorageDirectory().toString()
			+ "/photolib/";
	final String sPath2 = Environment.getExternalStorageDirectory().toString()
			+ "/photosight/";
	private ImageView ivMain = null;
	private BitmapDrawable dImage;
	private ProgressDialog pdLoader;
	private RelativeLayout rlButtons;
	private TextView tvPreview;
	private Thread tLoader = null;
	private float iOldX;
	private float iOldY;
	private boolean bRated;
	private LinearLayout llRate;

	private final Handler hMain = new Handler() {
		@Override
		public void handleMessage( final Message msg ) {
			switch( msg.what ) {

			case 0:
				ivMain.setImageDrawable( dImage );
				if( pdLoader.getProgress() == 0 ) {
					pdLoader.dismiss();
					tvPreview.setVisibility( TextView.VISIBLE );
				} else {
					tvPreview.setVisibility( TextView.INVISIBLE );
				}
				break;

			case 1:
				if( msg != null && msg.arg1 > 0 ) {
					pdLoader.setMax( msg.arg1 );
					pdLoader.setProgress( 0 );
					pdLoader.setProgressStyle( ProgressDialog.STYLE_HORIZONTAL );
				} else {
					pdLoader.setMax( 64000 );
					pdLoader.setProgress( 0 );
					pdLoader.setProgressStyle( ProgressDialog.STYLE_HORIZONTAL );
				}
				break;

			case 2:
				if( msg != null && msg.arg1 > 0 ) {
					pdLoader.incrementProgressBy( msg.arg1 );
				}
				break;

			case 3:
				ivMain.setImageDrawable( dImage );
				if( pdLoader.getProgress() == 0 ) {
					pdLoader.dismiss();
					tvPreview.setVisibility( TextView.INVISIBLE );
				}
				break;

			case 4:
				pdLoader.show();
				break;

			case 5:
				ivMain.setImageDrawable( dImage );
				pdLoader.dismiss();
				break;
			}
		}
	};

	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate( R.menu.menu, menu );
		return super.onCreateOptionsMenu( menu );
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
		switch( item.getItemId() ) {

		case R.id.exit:
			if( !bRated ) {
				ivMain.setVisibility( ImageView.INVISIBLE );
				llRate.setVisibility( LinearLayout.VISIBLE );
			} else {
				this.finish();
			}
			break;

		case R.id.wallpaper:
			String urlPath = vImages.get( iCurrent );
			URL uImage = null;
			Bitmap bmImage;

			try {
				uImage = new URL( urlPath );
			} catch( MalformedURLException e1 ) {
				e1.printStackTrace();
			}

			File fImage = new File( uImage.getFile() );
			bmImage = BitmapFactory.decodeFile( sPath + fImage.getName() );
			Bitmap bmScaled,
			bmCroped;
			if( bmImage != null ) {
				DisplayMetrics metrics = new DisplayMetrics();
				getWindowManager().getDefaultDisplay().getMetrics( metrics );
				int iScrHeight = ( int ) ( metrics.heightPixels * metrics.density );

				if( bmImage.getHeight() != iScrHeight ) {
					float fScale = ( float ) bmImage.getHeight()
							/ ( float ) iScrHeight;
					bmScaled = Bitmap.createScaledBitmap( bmImage,
							( int ) ( ( float ) bmImage.getWidth() / fScale ),
							iScrHeight, false );
				} else {
					bmScaled = bmImage;
				}
				if( bmScaled.getWidth() > ( iScrHeight * 4 / 3 ) ) {
					int iCrop = ( bmScaled.getWidth() - ( iScrHeight * 4 / 3 ) ) / 2;
					bmCroped = Bitmap.createBitmap( bmScaled, iCrop, 0,
							iScrHeight * 4 / 3, iScrHeight );
				} else {
					bmCroped = bmScaled;
				}
				try {
					getApplicationContext().setWallpaper( bmCroped );
				} catch( IOException e ) {
					e.printStackTrace();
				}
			}
			break;

		case R.id.cat_2:
			iCat = 2;
			changeCategory();
			break;
		case R.id.cat_3:
			iCat = 3;
			changeCategory();
			break;
		case R.id.cat_4:
			iCat = 4;
			changeCategory();
			break;
		case R.id.cat_5:
			iCat = 5;
			changeCategory();
			break;
		case R.id.cat_6:
			iCat = 6;
			changeCategory();
			break;
		case R.id.cat_7:
			iCat = 7;
			changeCategory();
			break;
		case R.id.cat_8:
			iCat = 8;
			changeCategory();
			break;
		case R.id.cat_9:
			iCat = 9;
			changeCategory();
			break;
		case R.id.cat_10:
			iCat = 10;
			changeCategory();
			break;
		case R.id.cat_11:
			iCat = 11;
			changeCategory();
			break;
		case R.id.cat_12:
			iCat = 12;
			changeCategory();
			break;
		case R.id.cat_13:
			iCat = 13;
			changeCategory();
			break;
		case R.id.cat_14:
			iCat = 14;
			changeCategory();
			break;
		case R.id.cat_15:
			iCat = 15;
			changeCategory();
			break;
		case R.id.cat_16:
			iCat = 16;
			changeCategory();
			break;
		case R.id.cat_18:
			iCat = 18;
			changeCategory();
			break;
		case R.id.cat_19:
			iCat = 19;
			changeCategory();
			break;
		case R.id.cat_27:
			iCat = 27;
			changeCategory();
			break;
		case R.id.cat_64:
			iCat = 64;
			changeCategory();
			break;
		case R.id.cat_65:
			iCat = 65;
			changeCategory();
			break;
		case R.id.cat_78:
			iCat = 78;
			changeCategory();
			break;
		case R.id.cat_80:
			iCat = 80;
			changeCategory();
			break;
		case R.id.cat_82:
			iCat = 82;
			changeCategory();
			break;
		case R.id.cat_87:
			iCat = 87;
			changeCategory();
			break;
		case R.id.cat_91:
			iCat = 91;
			changeCategory();
			break;
		case R.id.cat_92:
			iCat = 92;
			changeCategory();
			break;

		case R.id.clear:
			try {
				File fPath = new File( sPath );
				File[] files = fPath.listFiles();
				for( File file : files ) {
					file.delete();
				}
				fPath.delete();

				fPath = new File( sPath2 );
				files = fPath.listFiles();
				for( File file : files ) {
					file.delete();
				}
				fPath.delete();
			} catch( Exception e ) {
			}
			break;
		}

		return super.onOptionsItemSelected( item );
	}

	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		requestWindowFeature( Window.FEATURE_NO_TITLE );
		setContentView( R.layout.main );

		SharedPreferences spPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		bRated = spPrefs.getBoolean( "rated", false );

		ivMain = ( ImageView ) findViewById( R.id.ivMain );
		tvPreview = ( TextView ) findViewById( R.id.tvPreview );
		rlButtons = ( RelativeLayout ) findViewById( R.id.rlButtons );
		findViewById( R.id.ivMain ).setOnClickListener( this );
		findViewById( R.id.ivMain ).setOnTouchListener( this );
		findViewById( R.id.btn_left ).setOnClickListener( this );
		findViewById( R.id.btn_right ).setOnClickListener( this );
		
		llRate = ( LinearLayout ) findViewById( R.id.llButtons );
		findViewById( R.id.btnLater ).setOnClickListener( this );
		findViewById( R.id.btnYes ).setOnClickListener( this );

		vImages = new Vector< String >();
		vIcons = new Vector< String >();
		changeCategory();
	}

	public void changeCategory() {
		vImages.clear();
		vIcons.clear();
		iCurrent = 0;
		pdLoader = new ProgressDialog( this );
		pdLoader.setCancelable( false );
		pdLoader.setTitle( "Download" );
		pdLoader.setProgressStyle( ProgressDialog.STYLE_HORIZONTAL );
		pdLoader.show();
		if( tLoader != null && tLoader.isAlive() ) {
			tLoader.interrupt();
		}
		tLoader = new Thread() {
			public void run() {
				BitmapDrawable bdTemp;
				parsePage( "http://www.photosight.ru/photos/category/" + iCat
						+ "/" );
				bdTemp = loadImage();
				if( !this.isInterrupted() ) {
					dImage = bdTemp;
				}
				hMain.sendEmptyMessage( 5 );
			}
		};
		tLoader.start();
	}

	public void parsePage( String urlPath ) {
		String sPage = null;

		try {
			sPage = DownloadPage( urlPath );
		} catch( Exception e ) {
			e.printStackTrace();
		}
		if( sPage != null ) {
			sPage = getBetween( sPage, "<ul class=\"photolist\">",
					"<div class=\"pages >", false );
			String[] sParts = sPage.split( "<li class" );
			for( int i = 1; i < sParts.length; i++ ) {
				String sUrl = "http://"
						+ getBetween( sParts[i], "background-image:url('http://", "');\">", false );
				vIcons.add( sUrl );
				sUrl = sUrl.replace( "_thumb", "_large" );
				vImages.add( sUrl );
			}
		}
	}

	public BitmapDrawable loadImage() {
		URL uImage = null;
		Bitmap bmImage;
		if( vImages.size() > 0 ) {
			String urlPath = vImages.get( iCurrent );

			try {
				uImage = new URL( urlPath );
			} catch( MalformedURLException e1 ) {
				e1.printStackTrace();
			}
			File fImage = new File( uImage.getFile() );
			File fPath = new File( sPath );
			fPath.mkdirs();
			bmImage = BitmapFactory.decodeFile( sPath + fImage.getName() );

			if( bmImage == null ) {
				hMain.sendEmptyMessage( 4 );
				dImage = loadIcon();
				hMain.sendEmptyMessage( 0 );

				try {
					bmImage = DownloadImage( urlPath );
					FileOutputStream osFile = new FileOutputStream( sPath
							+ fImage.getName() );
					bmImage.compress( Bitmap.CompressFormat.JPEG, 80, osFile );
					osFile.flush();
					osFile.close();
				} catch( Exception e ) {
				}
			}

			if( bmImage != null && bmImage.getWidth() > bmImage.getHeight() ) {
				int w = bmImage.getWidth();
				int h = bmImage.getHeight();
				Matrix mtx = new Matrix();
				mtx.postRotate( 90 );
				return new BitmapDrawable( Bitmap.createBitmap( bmImage, 0, 0, w,
						h, mtx, true ) );
			} else {
				return new BitmapDrawable( bmImage );
			}
		} else {
			bmImage = Bitmap.createBitmap( 320, 480, Bitmap.Config.ALPHA_8 );
			return new BitmapDrawable( bmImage );
		}
	}

	public BitmapDrawable loadIcon() {
		String urlPath = vIcons.get( iCurrent );
		URL uImage = null;
		Bitmap bmImage;

		try {
			uImage = new URL( urlPath );
		} catch( MalformedURLException e1 ) {
			e1.printStackTrace();
		}
		File fImage = new File( uImage.getFile() );
		File fPath = new File( sPath );
		fPath.mkdirs();
		bmImage = BitmapFactory.decodeFile( sPath + fImage.getName() );

		if( bmImage == null ) {
			try {
				bmImage = DownloadImage( urlPath );
				FileOutputStream osFile = new FileOutputStream( sPath
						+ fImage.getName() );
				bmImage.compress( Bitmap.CompressFormat.JPEG, 80, osFile );
				osFile.flush();
				osFile.close();
			} catch( Exception e ) {
			}
		}

		if( bmImage.getWidth() > bmImage.getHeight() ) {
			int w = bmImage.getWidth();
			int h = bmImage.getHeight();
			Matrix mtx = new Matrix();
			mtx.postRotate( 90 );
			return new BitmapDrawable( Bitmap.createBitmap( bmImage, 0, 0, w,
					h, mtx, true ) );
		} else {
			return new BitmapDrawable( bmImage );
		}
	}

	private String getBetween( String sSrc, String sStart, String sEnd,
			boolean bInclude ) {
		String sResult = null;
		if( sSrc != null && sStart != null && sEnd != null && sSrc.contains( sStart ) ) {
			if( bInclude ) {
				sResult = sSrc.substring( sSrc.indexOf( sStart ) );
			} else {
				sResult = sSrc.substring( sSrc.indexOf( sStart )
						+ sStart.length() );
			}

			if( sResult.contains( sEnd ) ) {
				if( bInclude ) {
					sResult = sResult.substring( 0, sResult.indexOf( sEnd )
							+ sEnd.length() );
				} else {
					sResult = sResult.substring( 0, sResult.indexOf( sEnd ) );
				}
			}
		}
		return sResult;
	}

	public void getPrevImage() {
		if( iCurrent > 0 ) {
			iCurrent--;
			pdLoader = new ProgressDialog( this );
			pdLoader.setCancelable( false );
			pdLoader.setTitle( "Download" );
			pdLoader.setMax( 0 );
			pdLoader.setProgressStyle( ProgressDialog.STYLE_SPINNER );
			pdLoader.setMessage( "Load image" );
			if( tLoader != null && tLoader.isAlive() ) {
				tLoader.interrupt();
			}
			tLoader = new Thread() {
				public void run() {
					BitmapDrawable bdTemp;
					bdTemp = loadImage();
					if( !this.isInterrupted() ) {
						dImage = bdTemp;
						hMain.sendEmptyMessage( 3 );
					}
				}
			};
			tLoader.start();
			ivMain.setImageDrawable( dImage );
		}
	}

	public void getNextImage() {
		if( iCurrent < vImages.size() - 1 ) {
			iCurrent++;
			pdLoader = new ProgressDialog( this );
			pdLoader.setCancelable( false );
			pdLoader.setTitle( "Download" );
			pdLoader.setMax( 0 );
			pdLoader.setProgressStyle( ProgressDialog.STYLE_SPINNER );
			pdLoader.setMessage( "Load image" );
			if( tLoader != null && tLoader.isAlive() ) {
				tLoader.interrupt();
			}
			tLoader = new Thread() {
				public void run() {
					BitmapDrawable bdTemp;
					bdTemp = loadImage();
					if( !this.isInterrupted() ) {
						dImage = bdTemp;
						hMain.sendEmptyMessage( 3 );
					}
				}
			};
			tLoader.start();
			ivMain.setImageDrawable( dImage );
		} else {
			iPager++;
			iCurrent++;
			pdLoader = new ProgressDialog( this );
			pdLoader.setCancelable( false );
			pdLoader.setTitle( "Download" );
			pdLoader.setProgressStyle( ProgressDialog.STYLE_HORIZONTAL );
			pdLoader.show();
			if( tLoader != null && tLoader.isAlive() ) {
				tLoader.interrupt();
			}
			tLoader = new Thread() {
				public void run() {
					BitmapDrawable bdTemp;
					parsePage( "http://www.photosight.ru/photos/category/"
							+ iCat + "/?pager=" + iPager );
					bdTemp = loadImage();
					if( !this.isInterrupted() ) {
						dImage = bdTemp;
					}
					hMain.sendEmptyMessage( 5 );
				}
			};
			tLoader.start();
		}
	}

	@Override
	public void onClick( View v ) {
		switch( v.getId() ) {

		case R.id.ivMain:
			if( rlButtons.getVisibility() == View.GONE ) {
				getWindow().addFlags(
						WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN );
				getWindow().clearFlags(
						WindowManager.LayoutParams.FLAG_FULLSCREEN );
				rlButtons.setVisibility( View.VISIBLE );
			} else {
				getWindow().addFlags(
						WindowManager.LayoutParams.FLAG_FULLSCREEN );
				getWindow().clearFlags(
						WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN );
				rlButtons.setVisibility( View.GONE );
			}
			break;

		case R.id.btn_left:
			getPrevImage();
			break;

		case R.id.btn_right:
			getNextImage();
			break;

		case R.id.btnYes:
			bRated = true;
			Intent goToMarket = null;
	        goToMarket = new Intent(Intent.ACTION_VIEW,Uri.parse("market://details?id="+getPackageName()));
	        startActivity( goToMarket );
			this.finish();
	        break;

		case R.id.btnLater:
			this.finish();
			break;
		}
	}

	@Override
	public View makeView() {
		ImageView imageView = new ImageView( this );
		imageView.setBackgroundColor( 0xFF000000 );
		imageView.setScaleType( ImageView.ScaleType.CENTER_INSIDE );
		imageView.setLayoutParams( new ImageSwitcher.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT ) );
		return imageView;
	}

	public Bitmap DownloadImage( String imageURL )
			throws ClientProtocolException, IOException, URISyntaxException {
		HttpGet httpRequest = null;
		HttpClient httpClient = new DefaultHttpClient();
		HttpResponse response = null;
		BufferedHttpEntity bufHttpEntity = null;
		httpRequest = new HttpGet( new URL( imageURL ).toURI() );
		response = ( HttpResponse ) httpClient.execute( httpRequest );

		HttpEntity entity = response.getEntity();
		try {
			bufHttpEntity = new BufferedHttpEntity( entity );
		} catch( IOException e ) {
			e.printStackTrace();
		}

		final long contentLength = bufHttpEntity.getContentLength();
		Bitmap bitmap = null;
		if( contentLength >= 0 ) {
			InputStream is = null;
			try {
				is = bufHttpEntity.getContent();
			} catch( IOException e ) {
				e.printStackTrace();
			}
			bitmap = BitmapFactory.decodeStream( is );
		}
		return bitmap;
	}

	public String DownloadPage( String pageURL ) throws IllegalStateException,
			IOException {

		String page = "";
		HttpClient httpClient = new DefaultHttpClient();
		HttpContext localContext = new BasicHttpContext();
		HttpGet httpGet = new HttpGet( pageURL );
		HttpResponse response = null;
		try {
			response = httpClient.execute( httpGet, localContext );
		} catch( ClientProtocolException e ) {
			e.printStackTrace();
		} catch( IOException e ) {
			e.printStackTrace();
		}

		BufferedReader reader = new BufferedReader( new InputStreamReader(
				response.getEntity().getContent() ) );

		Message msgOut = hMain.obtainMessage( 1, ( int ) response.getEntity()
				.getContentLength(), 0 );
		hMain.sendMessage( msgOut );

		String line = null;
		while( ( line = reader.readLine() ) != null ) {
			Message msgLen = hMain.obtainMessage( 2, line.length(), 0 );
			if( line.length() > 0 ) {
				hMain.sendMessage( msgLen );
			}
			page += line + "\n";
		}
		return page;
	}

	@Override
	public boolean onTouch( View v, MotionEvent event ) {
		if( event.getAction() == MotionEvent.ACTION_DOWN ) {
			iOldX = event.getX();
			iOldY = event.getY();
		}

		if( event.getAction() == MotionEvent.ACTION_UP ) {
			float iNewX = event.getX();
			float iNewY = event.getY();
			if( Math.abs( iOldX - iNewX ) > 40 ) {
				if( iOldX - iNewX > 0 ) {
					getNextImage();
				} else {
					getPrevImage();
				}
				return true;
			}
			if( Math.abs( iOldY - iNewY ) > 40 ) {
				if( iOldY - iNewY > 0 ) {
					getNextImage();
				} else {
					getPrevImage();
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean onKeyDown( int keyCode, KeyEvent event ) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if( !bRated ) {
				llRate.setVisibility( LinearLayout.VISIBLE );
				ivMain.setVisibility( ImageView.INVISIBLE );

				return true;
			} else {
				this.finish();
			}
	    }

		return super.onKeyDown( keyCode, event );
	}

	@Override
	protected void onStop() {
		SharedPreferences spPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor spEditor = spPrefs.edit();
		
		spEditor.putBoolean( "rated", bRated );
		spEditor.commit();

		super.onStop();
	}
}
