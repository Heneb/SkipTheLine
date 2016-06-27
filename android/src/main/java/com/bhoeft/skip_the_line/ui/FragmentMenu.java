package com.bhoeft.skip_the_line.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.*;
import android.webkit.*;
import com.bhoeft.skip_the_line.R;

/**
 * Fragment für die Ansicht des Speiseplans
 *
 * @author Benedikt Höft on 15.05.16.
 */
public class FragmentMenu extends Fragment
{
  private WebView webView;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState)
  {

    View view = inflater.inflate(R.layout.fragment_menu_layout, container, false);
    _initWebView(view);

    String url = getResources().getStringArray(R.array.menuLinks)[0];
    webView.loadUrl(url);

    return view;
  }

  private void _initWebView(View pView)
  {
    webView = (WebView) pView.findViewById(R.id.webView);
    webView.setWebViewClient(new WebViewClient());
    webView.getSettings().setJavaScriptEnabled(true);
    webView.getSettings().setDomStorageEnabled(true);
  }

  public void updateWebViewURL(int pos)
  {
    String url = getResources().getStringArray(R.array.menuLinks)[pos];
    webView.loadUrl(url);
  }
}
