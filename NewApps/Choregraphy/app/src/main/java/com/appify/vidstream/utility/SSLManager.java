package com.appify.vidstream.utility;

import android.annotation.SuppressLint;
import java.security.SecureRandom;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class SSLManager {
    @SuppressLint("TrulyRandom")
    public static void handleSSLHandshake() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType){
                }
            }};

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String Hostname, SSLSession Session) {
                    System.out.println("Hostname = "+Hostname);
                    System.out.println("Session = "+Session);
                    if(Hostname.contains("appifyworld.com")){
                        System.out.println(" return true");
                        return true;
                    }else if(Hostname.contains("google")){
                        System.out.println(" return true");
                        return true;
                    }else if(Hostname.contains("firebase")){
                        System.out.println(" return true");
                        return true;
                    }else if(Hostname.contains("youtube")){
                        System.out.println(" return true");
                        return true;
                    }else if(Hostname.contains("googleadservices")){
                        System.out.println(" return true");
                        return true;
                    }else if(Hostname.contains("admob")){
                        System.out.println(" return true");
                        return true;
                    }else if(Hostname.contains("inmobi")){
                        System.out.println(" return true");
                        return true;
                    }else if(Hostname.contains(".com")){
                        System.out.println(" return true");
                        return true;
                    }
                    return false;
                }
            });
        } catch (Exception ignored) {
            ignored.printStackTrace();
            System.out.println("SSL Error..");
        }
    }
}
