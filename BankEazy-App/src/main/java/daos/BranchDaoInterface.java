package daos;

import java.util.Map;

import exception.CustomBankException;
import model.Branch;

public interface BranchDaoInterface {
	
	//create
	int addBranch(Branch branch) throws CustomBankException;
	
	//read
	Map<Integer, Branch> getBranches(Branch branch, int limit, long offset) throws CustomBankException;
	
	//update
	boolean updateBranch(Branch branch) throws CustomBankException;

	
}
