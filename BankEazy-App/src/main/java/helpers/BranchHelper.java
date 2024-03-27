package helpers;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import daos.BranchDaoInterface;
import exception.CustomBankException;
import model.Branch;
import utilities.Validators;

public class BranchHelper {
	private BranchDaoInterface branchDao = null;

	public BranchHelper() throws CustomBankException {
		Class<?> BranchDAO;
		Constructor<?> branchDaoCon;

		try {
			BranchDAO = Class.forName("daos.BranchDAO");
			branchDaoCon = BranchDAO.getDeclaredConstructor();
			branchDao = (BranchDaoInterface) branchDaoCon.newInstance();
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException
				| IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new CustomBankException(CustomBankException.ERROR_OCCURRED);
		}
	}

	
	//create
	public int addBranch(Branch newBranch) throws CustomBankException {
		Validators.checkNull(newBranch);
		return branchDao.addBranch(newBranch);
	}

	//read
	public Map<Integer, Branch> getAllBranches() throws CustomBankException {
		Branch dummyBranch = new Branch();
		return branchDao.getBranches(dummyBranch, 50, 0);
	}

	public Branch getBranch(int branchId) throws CustomBankException {
		Branch dummyBranch = new Branch();
		dummyBranch.setId(branchId);
		Map<Integer, Branch> branches = branchDao.getBranches(dummyBranch, 1, 0);
		Validators.checkNull(branches);
		return branches.get(branchId);
	}

	
	//update
	public boolean editBranch(Branch editBranch) throws CustomBankException {
		Validators.checkNull(editBranch);
		return branchDao.updateBranch(editBranch);
	}

}
