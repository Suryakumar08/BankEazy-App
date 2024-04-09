package helpers;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import daos.AuditDaoInterface;
import exception.CustomBankException;
import model.Audit;
import utilities.Validators;

public class AuditHelper {
	
	private AuditDaoInterface auditDao = null;

	public AuditHelper() throws CustomBankException {
		Class<?> AuditDAO;
		Constructor<?> auditDaoConstructor;

		try {
			AuditDAO = Class.forName("daos.AuditDAO");
			auditDaoConstructor = AuditDAO.getDeclaredConstructor();
			auditDao = (AuditDaoInterface) auditDaoConstructor.newInstance();
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException
				| IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new CustomBankException(CustomBankException.ERROR_OCCURRED, e);
		}
	}
	
	public boolean insertAudit(Audit audit) throws CustomBankException{
		Validators.checkNull(audit, "Empty Audit!");
		return auditDao.addAudit(audit);
	}
}
