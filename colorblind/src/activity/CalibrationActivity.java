package activity;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import org.opencv.core.Mat;

import libera.EraCore;
import utility.ImageProcessHelper;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.naubull2.colorblind.R;

public class CalibrationActivity extends Activity{
	
	private ImageView mTestImage;
	private SeekBar mSeekbar;
	private Button mButtonNext;
	private TextView mExplanation, mValue;
	
	private ArrayList<Integer> mTestImageArray = new ArrayList<Integer>();
	private ArrayList<Integer> mImageExplanation = new ArrayList<Integer>();
	
	private Bitmap mCurrentImage;
	private int cntTest;
	private float totalValue;
	
	private Context mContext;
	private SharedPreferences pref;
	
	private ImageProcessHelper mImageProcHelper = new ImageProcessHelper();
	private EraCore era = new EraCore();

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getActionBar().setTitle(R.string.title_calibration);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		setContentView(R.layout.activity_calibration);
		mContext = this;
		
		mValue = (TextView)findViewById(R.id.text_value);
		mTestImage = (ImageView)findViewById(R.id.test_image);
		mExplanation = (TextView)findViewById(R.id.text_explanation);
		mSeekbar = (SeekBar)findViewById(R.id.calibration_seekbar);
		mButtonNext = (Button)findViewById(R.id.btn_confirm);
		
		initializeImageArray(mTestImageArray, mImageExplanation);
		
		// initial image and string
		cntTest = 0;
		mCurrentImage = BitmapFactory.decodeResource(getResources(), mTestImageArray.get(cntTest));
		// prepare for processing
		mCurrentImage = mImageProcHelper.JPEGtoRGB888(mCurrentImage);
		
		mTestImage.setImageBitmap(mCurrentImage);
		mExplanation.setText(getResources().getString(mImageExplanation.get(cntTest)));
		
		mButtonNext.setOnClickListener(new View.OnClickListener() {
			/**
			 * counter++
			 * save current progress value
			 * show next image and text explanation
			 */
			@Override
			public void onClick(View v) {
				cntTest++;
				totalValue += Float.parseFloat(mValue.getText().toString());
				
				if(cntTest<8){
					mCurrentImage = BitmapFactory.decodeResource(getResources(), mTestImageArray.get(cntTest));
					mCurrentImage = mImageProcHelper.JPEGtoRGB888(mCurrentImage);
					mTestImage.setImageBitmap(mCurrentImage);

					mExplanation.setText(getResources().getString(mImageExplanation.get(cntTest)));
				}else{
					/**
					 * show Toast to confirm final value,
					 * save and exit
					 */
					finish();
				}
			}
		});
		
		mSeekbar.setMax(100);
		mSeekbar.incrementProgressBy(1);
		// mSeekbar.setProgress(progress);
		mSeekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				if(seekBar.getProgress()<1){
					mValue.setText("0");
					seekBar.setProgress(0);
				}else
					mValue.setText(""+((float)seekBar.getProgress()/100));
								
				//process image
				new ImageRefineTask().execute();
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				// change text accordingly
				mValue.setText(""+((float)seekBar.getProgress()/100));
			}
		});
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		float refine = (float)(totalValue/cntTest);
		
		Toast.makeText(mContext, "색약 보정값 : "+refine, Toast.LENGTH_SHORT).show();
		
		pref = getSharedPreferences("ERA", MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.putFloat("era_calib", refine).commit();
	}

	// Bring in to memory all 8 hard coded test images
	public void initializeImageArray(ArrayList<Integer> bitmapArray, ArrayList<Integer> explanation){
		bitmapArray.add(R.drawable.test_2);
		bitmapArray.add(R.drawable.test_5);
		bitmapArray.add(R.drawable.test_6);
		bitmapArray.add(R.drawable.test_8_3);
		bitmapArray.add(R.drawable.test_12);
		bitmapArray.add(R.drawable.test_17);
		bitmapArray.add(R.drawable.test_74_21);
		bitmapArray.add(R.drawable.test_97);
		
		explanation.add(R.string.explanation_1);
		explanation.add(R.string.explanation_2);
		explanation.add(R.string.explanation_3);
		explanation.add(R.string.explanation_4);
		explanation.add(R.string.explanation_5);
		explanation.add(R.string.explanation_6);
		explanation.add(R.string.explanation_7);
		explanation.add(R.string.explanation_8);
	}

	/*
	 * Show pop up to double check exit and save any values currently set
	 */
	public void exitDialog(){
		new AlertDialog.Builder(this)
        .setIcon(android.R.drawable.ic_dialog_alert)
        .setTitle(R.string.dlg_title_calibration)
        .setMessage(R.string.dlg_content_calibration)
        .setPositiveButton(R.string.dlg_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            	/*
            	 * Save current value
            	 */
            	
                // Stop Activity
            	CalibrationActivity.this.finish();    
            }
        })
        .setNegativeButton(R.string.dlg_no, null)
        .show();
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	 
	    switch (item.getItemId()) {
	        case android.R.id.home: {
	        	exitDialog();
	            break;
	        }
	    }
	    return super.onOptionsItemSelected(item);
	}
	
	/**
	 * Confirm and save whatever is done before exit
	 */
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            // Ask if it should really quit
        	exitDialog();
            return true;
        }
        else {
            return super.onKeyDown(keyCode, event);
        }
    }
	
	class ImageRefineTask extends AsyncTask<Void, Void, Boolean> {
		ProgressDialog pDialog;
		Bitmap newImage;
		@Override
		protected void onPreExecute() {
			// Opens dialog on loading data file to external storage
			super.onPreExecute();
			pDialog = new ProgressDialog(mContext);
			pDialog.setMessage("Processing");
			pDialog.setIndeterminate(true);
			pDialog.setCancelable(false);
			pDialog.show();
		}
		@Override
		protected Boolean doInBackground(Void... arg) {
				Mat srcImage = mImageProcHelper.BitmapToMat(mCurrentImage);
				
				era.RefineImage(srcImage.nativeObj, srcImage.nativeObj, Float.parseFloat(mValue.getText().toString()));
				newImage = mImageProcHelper.MatToBitmap(srcImage);
				
				return true;
		}
		@Override
		protected void onPostExecute(Boolean isDone) {
			pDialog.dismiss();
			mTestImage.setImageBitmap(newImage);
		}
	}
}
