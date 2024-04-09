package daos;

import exception.CustomBankException;
import model.Audit;

public interface AuditDaoInterface {
	boolean addAudit(Audit audit) throws CustomBankException;
}
