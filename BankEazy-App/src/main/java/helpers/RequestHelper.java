package helpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import exception.CustomBankException;

public class RequestHelper {
	
    public static void disableCertificateValidation() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }
                }
            };

            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            HostnameVerifier allHostsValid = (hostname, session) -> true;
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	
	@SuppressWarnings("finally")
	public String sendPostRequest(String urlString, String jsonData, String secretKey) {
		URL url;
		String result = "";
		try {
			url = new URL(urlString);
			disableCertificateValidation();
			
			System.setProperty("javax.net.ssl.trustStore", "/home/surya-pt-7357/Documents/keystore.jks");
	        System.setProperty("javax.net.ssl.trustStorePassword", "surya@131419@sS");
	        
	        HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
			HttpsURLConnection conn = (HttpsURLConnection)url.openConnection();
			
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("secretkey", secretKey);	//read and write scoped apikey...
			conn.setDoOutput(true);
			try(OutputStream os = conn.getOutputStream()){
				byte[] input = jsonData.getBytes();
				os.write(input);
			}
			
			int responseCode = conn.getResponseCode();
			System.out.println("Response Code : " + responseCode);
			
			try {
				result = bodyWriter(conn);
				System.out.println("Result from Request Helper ::: " + result);
			} catch (CustomBankException e) {
				e.printStackTrace();
			}
		
			conn.disconnect();
		}catch(IOException ex) {
			ex.printStackTrace();
		}
		finally {
			return result;
		}
	}

	private String bodyWriter(HttpsURLConnection conn) throws CustomBankException{
		StringBuilder result = new StringBuilder();
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))){
			String line;
			while((line = reader.readLine()) != null) {
				result.append(line);
			}
		} catch (IOException e) {
			throw new CustomBankException("Cannot read response data!" ,e);
		}
		return result.toString();
	}
}
