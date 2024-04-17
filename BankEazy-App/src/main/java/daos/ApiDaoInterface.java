package daos;

import java.util.List;

import exception.CustomBankException;
import model.ApiData;

public interface ApiDaoInterface {
	List<ApiData> getApiList(int userId) throws CustomBankException;
	
	ApiData getApiData(String apiKey) throws CustomBankException;
	
	boolean addApi(ApiData apiData) throws CustomBankException;
	
	boolean removeApi(String apiKey) throws CustomBankException;
}
