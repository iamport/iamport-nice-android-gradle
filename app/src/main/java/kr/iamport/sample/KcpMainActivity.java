package kr.iamport.sample;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

import iamport.kr.iamportniceandroid.R;
import kr.iamport.sdk.NiceWebViewClient;

public class KcpMainActivity extends AppCompatActivity {

    private String TAG = "iamport";
    private WebView mainWebView;
    private NiceWebViewClient niceClient;
    private final String APP_SCHEME = "iamportnice://";

    @SuppressLint("NewApi") @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainWebView = (WebView) findViewById(R.id.mainWebView);

        niceClient = new NiceWebViewClient(this, mainWebView);
        mainWebView.setWebViewClient(niceClient);
        WebSettings settings = mainWebView.getSettings();
        settings.setJavaScriptEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);
            cookieManager.setAcceptThirdPartyCookies(mainWebView, true);
        }

        Intent intent = getIntent();
        Uri intentData = intent.getData();

        if ( intentData == null ) {
            mainWebView.loadUrl("http://www.iamport.kr/demo");
        } else {
            //isp 인증 후 복귀했을 때 결제 후속조치
            String url = intentData.toString();
            if ( url.startsWith(APP_SCHEME) ) {
                String redirectURL = url.substring(APP_SCHEME.length()+3); //"://"가 추가로 더 전달됨
                mainWebView.loadUrl(redirectURL);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
    	/* 실시간 계좌이체 인증 후 후속처리 루틴 */
        String resVal = data.getExtras().getString("bankpay_value");
        String resCode = data.getExtras().getString("bankpay_code");

        //KCP 실시간 계좌이체 후 처리
        if ( "000".equals(resCode) ) {
            mainWebView.loadUrl("javascript:KCP_App_script('"+resCode+"','AutoCheck')" );
        }
    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

    }

}
