package com.vascomouta.vmlogger.webservice;


import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import android.os.Build;
import android.util.Log;

/**
 * This class handle server request and response.
 *
 */
public class HttpUrlConnectionUtil {
	
	
public static final String TAG = HttpUrlConnectionUtil.class.getName();
	
	public static final String HEADER_ACCEPT = "Accept";
	public static final String CONTENT_TYPE = "Content-type";
	public static final int TIME_OUT = 2 * 60 * 1000;
	private static final int _4KB = 4 * 1024;
	
	
	/**
	 * Request to server
	 * @param urlString
	 * @param body
	 * @param httpParams :can be null
	 * @return
	 */
	public static Response request(String urlString, String body, String method, HashMap<String, String> httpParams) {
		return request(urlString, body, method, httpParams, true);
	}
	
	static Response request(String urlString, String body, String method, HashMap<String, String> httpParams, boolean retryOnEOF) {
		HttpURLConnection conn = null;
		try {
			URL url = new URL(urlString);
			System.setProperty("http.keepAlive", "false");
			if (checkHTTPS(urlString)) {
				// create a self-signed certificate
				createSelfSignedCertificate();
				// connect to https
				conn = (HttpsURLConnection) url.openConnection();
			} else {
				conn = (HttpURLConnection) url.openConnection();
			}
			conn.setReadTimeout(TIME_OUT);
			conn.setConnectTimeout(TIME_OUT);
			conn.setRequestMethod(method);
			conn.setRequestProperty(CONTENT_TYPE, "application/json");
			conn.setRequestProperty(HEADER_ACCEPT, "application/json");
			if (httpParams != null && httpParams.size() > 0) {
				for (HashMap.Entry<String, String> entry : httpParams.entrySet()) {
					conn.setRequestProperty(entry.getKey(), entry.getValue());
				}
			}
			if (Build.VERSION.SDK_INT > 13) {
				conn.setRequestProperty("Connection", "close");
			}
			conn.setDoInput(true);
			conn.setDoOutput(true);
			body = (body != null) ? body : "";
			byte[] outputInBytes = body.getBytes("UTF-8");
			OutputStream os = conn.getOutputStream();
			os.write(outputInBytes);
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
			writer.flush();
			writer.close();
			os.close();
			conn.connect();
			int statusCode = conn.getResponseCode();
			Map<String, List<String>> headerFields = conn.getHeaderFields();
			String response = "";
			switch (statusCode) {
				case HttpURLConnection.HTTP_OK:
					response = new String(readFullyBytes(conn.getInputStream(), 2 * _4KB));
					return new Response(statusCode, response, headerFields);
				case HttpURLConnection.HTTP_PAYMENT_REQUIRED:
					response = new String(readFullyBytes(conn.getErrorStream(), 2 * _4KB));
				default:
					return new Response(statusCode, response, headerFields);
			}
		}catch (IOException ex){
			Log.e(TAG, "Error in getting response" + ex);
		} catch (Exception e) {
			Log.e(TAG, "Error in getting response" + e);
			if(retryOnEOF) {
				Log.e(TAG, "EOF Exception while making request. Retrying ..");
				return request(urlString, body,method, httpParams, false);
			} else {
				Log.e(TAG, "EOF Exception while making request." + e);
			}
		} finally {
			if(conn!=null)
				conn.disconnect();
		}
		return null; 
	}

	/**
	 * Check wheather request is http or https
	 * @param url
	 * @return
	 */
	public static boolean checkHTTPS(String url){
		return url.contains("https");
	}
	
	  /**
     * Read bytes from InputStream efficiently. All data will be read from
     * stream. This method return the bytes or null. This method will not close
     * the stream.
     */
    public static byte[] readFullyBytes(InputStream is, int blockSize) {
        byte[] bytes = null;
        if (is != null) {
            try {
                int readed = 0;
                byte[] buffer = new byte[blockSize];
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                while ((readed = is.read(buffer)) >= 0) {
                    bos.write(buffer, 0, readed);
                }
                bos.flush();
                bytes = bos.toByteArray();
            } catch (IOException e) {
                Log.e(TAG, " : readFullyBytes: ", e);
            }
        }
        return bytes;
    }
  

	// Create a trust manager that does not validate certificate chains
	private static TrustManager[] trustAllCerts = new TrustManager[] {
			new X509TrustManager() {
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}
				public void checkClientTrusted(
						java.security.cert.X509Certificate[] certs, String authType) throws CertificateException{
				}
				public void checkServerTrusted(
						java.security.cert.X509Certificate[] certs, String authType) throws CertificateException{
				}
			}
	};

	private static void createSelfSignedCertificate() throws NoSuchAlgorithmException, KeyManagementException {
		// Install the all-trusting trust manager
		SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(null, trustAllCerts, new java.security.SecureRandom());
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

		// Create all-trusting host name verifier
		HostnameVerifier allHostsValid = new HostnameVerifier() {
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
		};
		// Install the all-trusting host verifier
		HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
	}

}
