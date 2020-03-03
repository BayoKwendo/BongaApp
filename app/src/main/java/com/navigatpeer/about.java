package com.navigatpeer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.wang.avi.AVLoadingIndicatorView;

import java.lang.reflect.Method;

import static com.navigatpeer.utils.LoadingUtil.enableDisableView;


public class about extends AppCompatActivity {

    FrameLayout fader;
    FrameLayout mainFrame;
    boolean isPageError = false;
    LinearLayout linear;
    Button btn;
    TextView txt;
    AVLoadingIndicatorView avi;
    private MyWebChromeClient mWebChromeClient = null;
    private View mCustomView;
    private LinearLayout container;
    private ProgressBar progressbar;
    private Button nextButton, closeButton;
    private EditText findBox;
    private RelativeLayout mContentView;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private FrameLayout mCustomViewContainer;
    private WebChromeClient.CustomViewCallback mCustomViewCallback;

    private WebView myWebView;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);
        linear = findViewById(R.id.linear);
        btn = findViewById(R.id.reload);
        txt = findViewById(R.id.errText);

        mAuth = FirebaseAuth.getInstance();
        mAuth.getCurrentUser().getUid();

        progressbar = findViewById(R.id.progress_bar);

        fader = findViewById(R.id.fader);
        mainFrame = findViewById(R.id.mainFrame);
        avi = findViewById(R.id.avi);
        setLoadingAnimation();
        isPageError = false;
        progressbar.setVisibility(View.GONE);
        Toolbar toolbar = findViewById(R.id.toolbar);
        Handler handler = new Handler();
        linear.setVisibility(View.GONE);

        TextView tool = toolbar.findViewById(R.id.title);
        tool.setText("About Us");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (mAuth.getCurrentUser() == null){
//                    startActivity(new Intent(about.this, LoginActivityDeaf.class));
//                    finish();
                    Toast.makeText(about.this, "NULLL", Toast.LENGTH_SHORT).show();
                }
            }
        };




        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recreate();
            }
        });
        if (!isNetworkAvailable()) {
            stopLoadingAnimation();
            linear.setVisibility(View.GONE);

//            // Create an Alert Dialog
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            // Set the Alert Dialog Message
//            builder.setMessage("Internet Connection Required");
//            builder.setCancelable(false);
//
//            builder.setNegativeButton("Cancel",
//                    new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int id) {
//                            finish();
//                        }
//                    });
//            builder.setPositiveButton("Retry",
//                    new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog,
//                                            int id) {
//                            // Restart the Activity
//                            recreate();
//                        }
//                    });
//            AlertDialog alert = builder.create();
//            alert.show();
        }

        myWebView = findViewById(R.id.webview);
        mWebChromeClient = new MyWebChromeClient();
        myWebView.setWebChromeClient(mWebChromeClient);
        myWebView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {

                if (isPageError) {
                    myWebView.setVisibility(View.GONE);
                    linear.setVisibility(View.VISIBLE);

                }
                stopLoadingAnimation();
                progressbar.setVisibility(View.GONE);

            }


            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

                isPageError = true;

            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                setLoadingAnimation();
                progressbar.setVisibility(View.VISIBLE);
                return true;

            }

        });


//        if (isNetworkAvailable()) {
            WebSettings webSettings = myWebView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setDomStorageEnabled(true);
            webSettings.setUseWideViewPort(true);
            webSettings.setLoadsImagesAutomatically(true);
            webSettings.setLoadWithOverviewMode(true);
            webSettings.setBuiltInZoomControls( true );
            webSettings.setDisplayZoomControls( false);
            webSettings.setSupportMultipleWindows(true);
            webSettings.setGeolocationEnabled(true);
            myWebView.loadUrl("file:///android_asset/about.html");
//        } else {
//            linear.setVisibility(View.VISIBLE);
//        }

        nextButton = findViewById(R.id.nextButto);
        closeButton = findViewById(R.id.closeButto);
        findBox = findViewById(R.id.findBo);
        findBox.setSingleLine(true);
        findBox.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && ((keyCode == KeyEvent.KEYCODE_ENTER))) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    myWebView.findAllAsync(findBox.getText().toString());
                    myWebView.setFindListener(new WebView.FindListener() {
                        @Override
                        public void onFindResultReceived(int activeMatchOrdinal, int numberOfMatches, boolean isDoneCounting) {
                            if (numberOfMatches == 0) {
                                // Creating alert Dialog with one Button+
                                AlertDialog alertDialog = new AlertDialog.Builder(
                                        about.this).create();
                                // Setting Dialog Message
                                alertDialog.setTitle(Html.fromHtml("<font color='#e20719'> No result found</font>"));
                                // Setting Icon to Dialog
                                // Setting OK Button
                                alertDialog.setButton(Html.fromHtml("<font color='#1d7a10'> OK</font>"),
                                        (dialog, which) -> {
                                            // Write your code here to execute after dialog
                                            // closed
                                            dialog.dismiss();
                                        });
                                // Showing Alert Message
                                alertDialog.show();
                                myWebView.clearMatches();
                                findBox.setText("");
                                hideSoftKeyboard();
                                container.setVisibility(RelativeLayout.GONE);
                            }
                        }

                    });
                } else {
                    // Creating alert Dialog with one Button
                    AlertDialog alertDialog = new AlertDialog.Builder(
                            about.this).create();
                    // Setting Dialog Message
                    alertDialog.setMessage(Html.fromHtml("<font color='#e20719'>Search can not be performed!! kindly upgrade your android version/font>"));
                    // Setting Icon to Dialog
                    // Setting OK Button
                    alertDialog.setButton(Html.fromHtml("<font color='#1d7a10'> OK</font>"),
                            new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    // Write your code here to execute after dialog
                                    // closed
                                    dialog.dismiss();
                                }
                            });
                    // Showing Alert Message
                    alertDialog.show();
                    myWebView.clearMatches();
                    findBox.setText("");
                    container.setVisibility(RelativeLayout.GONE);
                }
                try {
                    // Can't use getMethod() as it's a private method
                    for (Method m : WebView.class.getDeclaredMethods()) {
                        if (m.getName().equals("setFindIsUp")) {
                            m.setAccessible(true);
                            m.invoke(myWebView, true);
                            break;
                        }
                    }
                } catch (Exception ignored) {
                } finally {
                    InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    // check if no view has focus:
                    View vv = getCurrentFocus();
                    if (vv != null) {
                        assert inputManager != null;
                        inputManager.hideSoftInputFromWindow(v.getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);

                    }
                }
            }
            return false;
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myWebView.findNext(true);

            }
        });
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myWebView.clearMatches();
                findBox.setText("");
                hideSoftKeyboard();
                container.setVisibility(View.GONE);

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_shar) {
            search();
        }
        if (id == android.R.id.home) {
            onBackPressed();
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void search() {
        container = findViewById(R.id.layoutId);
        if (container.getVisibility() == RelativeLayout.GONE) {
            container.setVisibility(RelativeLayout.VISIBLE);
            findBox.requestFocus();
            showKeyboard();
        }

    }

    public void showKeyboard() {
        findBox.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    public void hideSoftKeyboard() {
        findBox.clearFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(findBox.getWindowToken(), 0);

    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


    // Private class isNetworkAvailable
    private boolean isNetworkAvailable() {
        // Using ConnectivityManager to check for Network Connection
        ConnectivityManager connectivityManager = (ConnectivityManager) this
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    public void setLoadingAnimation() {
        enableDisableView(mainFrame, false);
        fader.setVisibility(View.VISIBLE);
        avi.show();
    }

    public void stopLoadingAnimation() {
        enableDisableView(mainFrame, true);
        fader.setVisibility(View.GONE);
        avi.hide();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(about.this, Dashboard.class));


    }

    public class MyWebChromeClient extends WebChromeClient {

        FrameLayout.LayoutParams LayoutParameters = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);

        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            // if a view already exists then immediately terminate the new one
            if (mCustomView != null) {
                callback.onCustomViewHidden();
                return;
            }
            mContentView = findViewById(R.id.activity_main21);
            mContentView.setVisibility(View.GONE);
            mCustomViewContainer = new FrameLayout(about.this);
            mCustomViewContainer.setLayoutParams(LayoutParameters);
            mCustomViewContainer.setBackgroundResource(android.R.color.black);
            view.setLayoutParams(LayoutParameters);
            mCustomViewContainer.addView(view);
            mCustomView = view;
            mCustomViewCallback = callback;
            mCustomViewContainer.setVisibility(View.VISIBLE);
            setContentView(mCustomViewContainer);
        }

        @Override
        public void onHideCustomView() {
            if (mCustomView == null) {
                return;
            } else {
                // Hide the custom view.
                mCustomView.setVisibility(View.GONE);
                // Remove the custom view from its container.
                mCustomViewContainer.removeView(mCustomView);
                mCustomView = null;
                mCustomViewContainer.setVisibility(View.GONE);
                mCustomViewCallback.onCustomViewHidden();
                // Show the content view.
                mContentView.setVisibility(View.VISIBLE);
                setContentView(mContentView);
            }
        }
    }
}


