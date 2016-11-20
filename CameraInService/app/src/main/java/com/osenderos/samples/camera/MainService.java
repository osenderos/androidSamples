package com.osenderos.samples.camera;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.core.Core;
import org.opencv.core.Mat;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;



public class MainService extends Service implements CvCameraViewListener2{

    View view;
    WindowManager wm;
	ImageButton mExitButton;
    CameraBridgeViewBase mOpenCvCameraView;
    static final String LOG_TAG="MainService";
	//define which camera is going to be opened
	int mCameraID =JavaCameraView.CAMERA_ID_BACK;// CAMERA_ID_FRONT && CAMERA_ID_BACK
	//define width and height (320x240) (640x480)
	int mCameraWidth=320;//320 or 640
	int mCameraHeight=240;//240 or 480

    @Override
	public int onStartCommand(Intent intent, int flags, int startId) {
   		return super.onStartCommand(intent, flags, startId);
	}

	@Override
    public void onStart(Intent intent,int startID) {
	
		 
		
    		LayoutInflater layoutInflater = LayoutInflater.from(this);  //creates the layoutinflater
		    view = layoutInflater.inflate(R.layout.service, null);		//inflates a previously created layout from service.xml
			
		        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
		                WindowManager.LayoutParams.WRAP_CONTENT,
		                WindowManager.LayoutParams.WRAP_CONTENT,
		                WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
						WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
		                PixelFormat.TRANSLUCENT);
		      
		        wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);	// gets the window service to diaplay opencv's image output

				//--**Kill service button
				mExitButton = (ImageButton) view.findViewById(R.id.exit);
				mExitButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
							Log.d(LOG_TAG, "Clicked");

							stopSelf();
										}
				});

				//add viewgroup to hide opencv's needed camera output renderer if nedded
				ViewGroup containerLayout = (ViewGroup) view.findViewById(R.id.container_view);
				containerLayout.removeAllViewsInLayout();
				//to create a element to hide camera preview
				ImageView image = new ImageView(this);
				image.setBackgroundResource(R.drawable.icon);
                containerLayout.setVisibility(View.VISIBLE);
				containerLayout.addView(image);
				//to show camera preview
				//containerLayout.setVisibility(View.INVISIBLE);

				//add  opencv camera's preview element
                ViewGroup cameraLayout = (ViewGroup) view.findViewById(R.id.camera_view);
                cameraLayout.removeAllViewsInLayout();
				mOpenCvCameraView = new org.opencv.android.JavaCameraView(this, mCameraID);// CAMERA_ID_FRONT CAMERA_ID_BACK);
				ViewGroup.LayoutParams params_cam = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
				mOpenCvCameraView.setLayoutParams(params_cam);
				//mOpenCvCameraView.setZOrderOnTop(true);
                cameraLayout.addView(mOpenCvCameraView,params_cam);
		        	       
		        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
		        mOpenCvCameraView.setMinimumHeight(mCameraHeight);
		        mOpenCvCameraView.setMinimumWidth(mCameraWidth);
		        mOpenCvCameraView.setMaxFrameSize(mCameraWidth, mCameraHeight);

				mOpenCvCameraView.enableFpsMeter();
		        mOpenCvCameraView.setCvCameraViewListener(this);

		       
		        //mOpenCvCameraView.enableView();
				initAsync();
		     		        
		        wm.addView(view, params);

		        


       
    }
	public void initAsync()
	{
		Log.v(LOG_TAG, "onResume");
		if (!OpenCVLoader.initDebug()) {
			Log.d(LOG_TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
			OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, mLoaderCallback);
		} else {
			Log.d(LOG_TAG, "OpenCV library found inside package. Using it!");
			mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
		}
	}
    @Override
    public void onDestroy() {
		if (mOpenCvCameraView != null)
			mOpenCvCameraView.disableView();


		wm.removeViewImmediate(view);
		super.onDestroy();

    }
    
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(LOG_TAG, "OpenCV loaded successfully");
                
                    mOpenCvCameraView.enableView();
                
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

	public void onCameraViewStarted(int width, int height) {
		// TODO Auto-generated method stub
		Log.v(LOG_TAG, "CameraViewStarted");
		
	}

	public void onCameraViewStopped() {
		Log.v(LOG_TAG, "onCameraViewStopped");
		// TODO Auto-generated method stub
		
	}

	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
		// TODO Auto-generated method stub
		
		Mat mRgba = inputFrame.rgba();
		Mat mGray = inputFrame.gray();
		if (mCameraID==JavaCameraView.CAMERA_ID_FRONT) {
			Core.flip(mRgba, mRgba, 1);
			Core.flip(mGray, mGray, 1);
		}
		Log.d(LOG_TAG, "NewFrame");

		//process(mRgba,mGray);

		return mRgba;//display this image.
	}
	public void process (Mat _mrgba ,Mat _mgray){

		//run(_mgray);//Run processor


		 //Output alarms on UI from opencv's onCameraFrame callback thread
		Handler mainHandler = new Handler(Looper.getMainLooper());
		Runnable myRunnable = new Runnable() {
			@Override
			public void run() {

				//do changes on UI

			} // This is your code
		};
		mainHandler.post(myRunnable);
	}


	static String ext="";

	static
	{
		try
		{
			Log.v(LOG_TAG, "adding libopencv_java3"+ext);
			System.loadLibrary("opencv_java3"+ext);
		}
		catch (UnsatisfiedLinkError e)
		{
			System.err.println("public native code library failed to load.\n" + e);
			Log.e(LOG_TAG,e.getMessage());

		}
	};
}
