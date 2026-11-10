package com.ultimoarranque.android;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/** Presenta la misma aplicación Spring MVC que se usa desde Chrome. */
public final class MainActivity extends Activity {
    private static final String URL_WEB = "http://10.0.2.2:8080/";
    private WebView navegador;
    private TextView estado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActionBar() != null) getActionBar().hide();

        LinearLayout pantalla = new LinearLayout(this);
        pantalla.setOrientation(LinearLayout.VERTICAL);
        pantalla.setBackgroundColor(0xFFFFFFFF);

        LinearLayout barra = new LinearLayout(this);
        barra.setOrientation(LinearLayout.HORIZONTAL);
        barra.setPadding(dp(12), dp(7), dp(10), dp(7));
        barra.setBackgroundColor(0xFF5041B7);

        TextView titulo = new TextView(this);
        titulo.setText("EL ÚLTIMO ARRANQUE");
        titulo.setTextColor(0xFFFFFFFF);
        titulo.setTextSize(16);
        titulo.setGravity(android.view.Gravity.CENTER_VERTICAL);
        barra.addView(titulo, new LinearLayout.LayoutParams(0, dp(40), 1));

        Button actualizar = new Button(this);
        actualizar.setText("Actualizar");
        actualizar.setAllCaps(false);
        actualizar.setOnClickListener(v -> navegador.reload());
        barra.addView(actualizar);
        pantalla.addView(barra);

        estado = new TextView(this);
        estado.setText("Abriendo el sistema MVC...");
        estado.setTextColor(0xFF373259);
        estado.setTextSize(12);
        estado.setPadding(dp(12), dp(5), dp(12), dp(5));
        pantalla.addView(estado);

        navegador = new WebView(this);
        navegador.getSettings().setJavaScriptEnabled(true);
        navegador.getSettings().setDomStorageEnabled(true);
        navegador.getSettings().setLoadWithOverviewMode(true);
        navegador.getSettings().setUseWideViewPort(true);
        navegador.getSettings().setBuiltInZoomControls(true);
        navegador.getSettings().setDisplayZoomControls(false);
        navegador.setWebChromeClient(new WebChromeClient());
        navegador.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri uri = request.getUrl();
                String url = uri.toString();
                if (url.toLowerCase().contains("/pdf/") || url.toLowerCase().endsWith(".pdf")) {
                    abrirExternamente(uri);
                    return true;
                }
                String scheme = uri.getScheme();
                if ("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme)) return false;
                abrirExternamente(uri);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                estado.setText("Sistema MVC • " + url);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                if (!request.isForMainFrame()) return;
                estado.setText("Sin conexión. Verifica Spring Boot y pulsa Actualizar.");
                view.post(() -> view.loadDataWithBaseURL(null,
                        "<html><meta name='viewport' content='width=device-width,initial-scale=1'><body style='font-family:sans-serif;padding:22px;color:#27324a'>"
                                + "<h2>No se puede abrir el taller</h2>"
                                + "<p>Comprueba que Spring Boot esté ejecutándose en tu computadora y que puedas abrir http://localhost:8080/ desde Chrome.</p>"
                                + "<p>Luego pulsa <b>Actualizar</b> arriba.</p></body></html>",
                        "text/html", "UTF-8", null));
            }
        });
        pantalla.addView(navegador, new LinearLayout.LayoutParams(-1, 0, 1));
        setContentView(pantalla);
        navegador.loadUrl(URL_WEB);
    }

    private void abrirExternamente(Uri uri) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, uri));
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(this, "No hay aplicación para abrir: " + uri, Toast.LENGTH_LONG).show();
        }
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    @Override
    public void onBackPressed() {
        if (navegador != null && navegador.canGoBack()) navegador.goBack();
        else super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        if (navegador != null) navegador.destroy();
        super.onDestroy();
    }
}
