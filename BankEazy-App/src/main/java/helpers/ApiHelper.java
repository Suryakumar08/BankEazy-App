package helpers;

import java.util.List;

import daos.ApiDAO;
import daos.ApiDaoInterface;
import exception.CustomBankException;
import model.ApiData;

public class ApiHelper {
	
	private final int READAPI_SCOPE = 1;
	private final int WRITEAPI_SCOPE = 2;
	private final int DEFAULT_VALIDITY = 7;
	
	public List<ApiData> getUserApiList(int userId) throws CustomBankException{
		ApiDaoInterface apiDao = new ApiDAO();
		List<ApiData> datas = apiDao.getApiList(userId);
		if(datas == null) {
			throw new CustomBankException("No ApiKeys Available!");
		}
		return datas;
	}
	
	
	public boolean removeApiKey(String apiKey) {
		ApiDaoInterface apiDao = new ApiDAO();
		try {
			return apiDao.removeApi(apiKey);
		}catch(CustomBankException ex) {
			return false;
		}
	}
	
	public boolean addReadApi(int userId) {
		try {
			return addApiWithScopeValidity(userId, READAPI_SCOPE, DEFAULT_VALIDITY);
		}
		catch(CustomBankException ex) {
			return false;
		}
	}
	
	public boolean addWriteApi(int userId) {
		try {
			return addApiWithScopeValidity(userId, WRITEAPI_SCOPE, DEFAULT_VALIDITY);
		}
		catch(CustomBankException ex) {
			return false;
		}
	}
	
	public ApiData getApi(String apiKey) throws CustomBankException {
		ApiDaoInterface apiDao = new ApiDAO();
		return apiDao.getApiData(apiKey);
	}


	private boolean addApiWithScopeValidity(int userId ,int scope, int validity) throws CustomBankException {
		ApiDaoInterface apiDao = new ApiDAO();
		ApiData newApi = new ApiData();
		newApi.setUserId(userId);
		newApi.setApiKey(createApi());
		newApi.setCreatedAt(System.currentTimeMillis());
		newApi.setScope(scope);
		newApi.setValidity(validity);
		return apiDao.addApi(newApi);
	}
	
	
	private String createApi() {
		String apiChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890&";
		StringBuilder apiKey = new StringBuilder();
		int len = apiChars.length();
		for(int i = 0; i < 40; i++) {
			int currRandom = (int)(Math.random() * len);
			apiKey.append(apiChars.charAt(currRandom));
		}
		return apiKey.toString();
	}

}
