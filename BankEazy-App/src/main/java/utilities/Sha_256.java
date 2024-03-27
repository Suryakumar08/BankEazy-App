package utilities;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Sha_256 {
	@SuppressWarnings("finally")
	public static String getHashedPassword(String password) {
		String resultString = "";
		try {
			resultString = password.hashCode() + "";
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] passBytes = digest.digest(password.getBytes());
			resultString = new BigInteger(1, passBytes).toString();
			
//			String resultString = String.format("%064x", new BigInteger(1, passBytes));
			
			int length = resultString.length();
			
			if(length > 64) {
				resultString = resultString.substring(length - 64);
			}
			else {
				resultString = ("0".repeat(64 - resultString.length()) + resultString);
			}
		} catch (NoSuchAlgorithmException e) {
		} 
		finally {
			return resultString;
		}
	}
}
