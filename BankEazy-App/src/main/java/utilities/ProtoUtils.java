package utilities;

import java.util.Base64;

import org.json.JSONObject;

import com.google.protobuf.InvalidProtocolBufferException;

import helpers.InterBankProto.InterBank;

public class ProtoUtils {
	public static String getInterBankData(long senderAccount, long recipientAccount, String recipientIfsc,
			String description, double amount) {
		
		
		InterBank data = InterBank.newBuilder().setSenderAccNo(senderAccount).setReceiverAccNo(recipientAccount).setIfsc(recipientIfsc).setDescription(description).setAmount(amount).build();
		
		JSONObject interBankData = new JSONObject();
		JSONObject innerJsonData = new JSONObject();
		innerJsonData.put("inter_bank_data", Base64.getEncoder().encodeToString(data.toByteArray()));
		innerJsonData.put("bank_id", "BEZB");
		interBankData.put("data", innerJsonData);
		interBankData.put("time", Utilities.getCurrentTime());
		return interBankData.toString();
	}
	
	public static InterBank getInterBankProto(String proto) {
		InterBank data = null;
		byte[] byteArr =  Base64.getDecoder().decode(proto);
		try {
			data = InterBank.parseFrom(byteArr);
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
		return data;
	}
}
