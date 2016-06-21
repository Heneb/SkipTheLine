package com.bhoeft.skip_the_line;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by HB on 15.05.16.
 */
public class FragmentMenu extends Fragment
{
    WebView webView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub

        View view = inflater.inflate(R.layout.fragment_menu_layout,container,false);

        webView = (WebView) view.findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient());
        webView .getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);

        //String url = "";
        String url = getResources().getStringArray(R.array.menuLinks)[0];
        System.out.println(url);
        webView.loadUrl(url);

        return view;
    }

    public void updateWebViewURL(int pos)
    {
        String url = getResources().getStringArray(R.array.menuLinks)[pos];
        webView.loadUrl(url);
        System.out.println("Reload: " + url);
    }
}
