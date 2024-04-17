package helpers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import daos.AuditDAO;
import daos.AuditDaoInterface;
import exception.CustomBankException;
import model.Audit;
import utilities.Validators;

public class AuditHelper {
	
	private static ExecutorService auditExecutor = null;
	private static AuditDaoInterface auditDao = new AuditDAO();
	
	public AuditHelper() {
		if(auditExecutor == null) {
			auditExecutor = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                    60L, TimeUnit.SECONDS,
                    new LinkedBlockingQueue<Runnable>());
		}
	}

//	public AuditHelper() throws CustomBankException {
//		Class<?> AuditDAO;
//		Constructor<?> auditDaoConstructor;
//
//		try {
//			AuditDAO = Class.forName("daos.AuditDAO");
//			auditDaoConstructor = AuditDAO.getDeclaredConstructor();
//			auditDao = (AuditDaoInterface) auditDaoConstructor.newInstance();
//		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException
//				| IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
//			throw new CustomBankException(CustomBankException.ERROR_OCCURRED, e);
//		}
//	}
	
	
	public void insertAudit(Audit audit) throws CustomBankException{
		Validators.checkNull(audit, "Empty Audit!");
		auditExecutor.execute(addTask(audit));
	}

	private Runnable addTask(Audit audit) {
		return new Runnable() {
			@Override
			public void run() {
				try {
					auditDao.addAudit(audit);
				} catch (CustomBankException e) {
				}
			}
		};
	}
}
