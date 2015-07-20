package com.biotronisis.pettplant.activity.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.logging.Level;

import com.github.rtyley.android.sherlock.roboguice.fragment.RoboSherlockFragment;
import com.zlscorp.ultragrav.R;
import com.zlscorp.ultragrav.activity.IntentParams;
import com.zlscorp.ultragrav.activity.AbstractBaseActivity.MyOnClickListener;
import com.zlscorp.ultragrav.activity.dialog.NumberPadDialog;
import com.zlscorp.ultragrav.debug.MyDebug;
import com.zlscorp.ultragrav.file.ErrorHandler;

public abstract class AbstractBaseFragment extends RoboSherlockFragment implements IntentParams {

	private static final String TAG = "AbstractBaseFragment";
    protected boolean dummyLLHasFocus;

    public abstract void setupView(View view, Bundle savedInstanceState);

	public void populateData() {
	}

	public void persistData() {
	}

	@Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            //Test
            boolean test = false;
            if (test) {
                throw new Exception("test");
            } else {
                setupView(view, savedInstanceState);
            }
        } catch (Exception e) {
            if (MyDebug.LOG) {
                Log.e(TAG, "setupView failed", e);
            }
            ErrorHandler errorHandler = ErrorHandler.getInstance();
            errorHandler.logError(Level.WARNING, this.getClass().getSimpleName() + ".onViewCreated(): " +
                    "setupView failed - " + e, 
                    "Alert - Setup View Error", 
                    "There was an error setting up the view for " + this.getClass().getSimpleName(),
                    okOnClickListener);
//            handleFatalActivitySetup();
        }
    }

    MyOnClickListener okOnClickListener = new MyOnClickListener() {
        @Override
        public void onClick() {
            getActivity().finish();
        }
    };

	@Override
	public void onResume() {
        if (MyDebug.LOG) {
            Log.d(TAG, this.getClass().getSimpleName() + ".onResume");
        }
		super.onResume();

		try {
            //Test
//            if (true) {
                populateData();
//            } else {
//                throw new Exception("test");
//            }
		} catch (Exception e) {
	        if (MyDebug.LOG) {
	            Log.e(TAG, "populateData failed", e);
	        }
			ErrorHandler errorHandler = ErrorHandler.getInstance();
            errorHandler.logError(Level.WARNING, this.getClass().getSimpleName() + ".onResume(): " +
            		"populateData failed - " + e,
            		getString(R.string.activity_fatal_setup_title), 
                    getString(R.string.activity_fatal_setup_message) + this.getClass().getSimpleName(), 
                    okOnClickListener);
		}
	}

	@Override
	public void onPause() {
        if (MyDebug.LOG) {
            Log.d(TAG, "onPause");
        }
		try {
            //Test
//            if (true) {
                persistData();
//            } else {
//                throw new Exception("test");
//            }
		} catch (Exception e) {
	        if (MyDebug.LOG) {
	            Log.e(TAG, "persistData failed", e);
	        }
			ErrorHandler errorHandler = ErrorHandler.getInstance();
            errorHandler.logError(Level.WARNING, this.getClass().getSimpleName() + ".onPause(): " +
            		"persistData failed - " + e,
            		getString(R.string.activity_fatal_persist_title), 
            		getString(R.string.activity_fatal_persist_message) + this.getClass().getSimpleName(), 
            		okOnClickListener);
		}
		super.onPause();
	}

    public boolean dummyLLHasFocus() {
        return dummyLLHasFocus;
    }
    
    /**
     * Set the EditText's onFocusChangeListener and onClickListener
     * @params editText - the EditText to work on
     * @params name - the field name that will appear in the numberpad dialog
     * @params dummyLL - the dummy LinearLayout to give focus to
     * @params nextFieldIsEditText - controls onNextClicked behavior 
     * @params useDummyLL - controls whether to use the dummyLLHasFocus flag
     * 
     */
    public void setupNumberPadKeypad(final EditText editText, final String name,
            final LinearLayout dummyLL, final boolean nextFieldIsEditText, final boolean useDummyLL) {

        editText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && (!useDummyLL || (useDummyLL && !dummyLLHasFocus()))) {
                    editText.performClick();
                }
            }
        });
        
	    editText.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
		        if (MyDebug.LOG) {
		            Log.d(TAG, "onClick NumberPadEditText");
		        }
				FragmentManager fm = getActivity().getSupportFragmentManager();
				NumberPadDialog editNameDialog = new NumberPadDialog();
				editNameDialog.setCallback(new NumberPadCallback() {
		            
		            @Override
		            public void onNextClicked() {
		                // When the Next button on the numberpad is clicked, give the next field focus.
		                // This may be an EditText or NumberPadEditText.
		                EditText nextEditText = (EditText) editText.focusSearch(View.FOCUS_DOWN);
		                if (nextEditText != null) {
		                    nextEditText.requestFocus();
		                    if (nextFieldIsEditText) {
		                        // If the next field is a regular EditText instead of a NumperPadEditText,
		                        // position the cursor at the right end of the field and make sure the 
		                        // keyboard is displayed.
		                        nextEditText.setSelection(nextEditText.getText().toString().length());   
		                        InputMethodManager imm = (InputMethodManager) getActivity().
		                                getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,0);
		                    }
		                } else {
	                        dummyLL.requestFocus();
		                }
		            }

                    @Override
                    public void onCancelClicked() {
                        dummyLL.requestFocus();
                    }
		        });
				editNameDialog.bindToEditText(editText, name);
				editNameDialog.show(fm, "fragment_number_pad");
			}
		});
	}
	
    public interface NumberPadCallback {

        public void onNextClicked();

        public void onCancelClicked();

    }
}
