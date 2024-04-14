package helpers;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import daos.AuditDAO;
import daos.AuditDaoInterface;
import exception.CustomBankException;
import model.Audit;
import utilities.Validators;

public class AuditHelper {
	
	private static BlockingQueue<Audit> audits = new LinkedBlockingQueue<>();
	private static ExecutorService auditExecutor = null;
	private static AuditDaoInterface auditDao = new AuditDAO();
	
	public AuditHelper() {
		if(auditExecutor == null) {
			auditExecutor = Executors.newCachedThreadPool();
			initConsumer();			
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
	
	public void initConsumer() {
		System.out.println("!!!!!!!!!!!initConsumer before Executor : " + Thread.currentThread().getName());
		auditExecutor.execute(()->{
			System.out.println("############initConsumer in Executor : " + Thread.currentThread().getName());
			while(true) {
				Audit currAudit;
				try {
					currAudit = audits.take();
					auditDao.addAudit(currAudit);
				} catch (InterruptedException | CustomBankException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public void insertAudit(Audit audit) throws CustomBankException{
		System.out.println("$$$$$$$$$$$$InsertAudit before producer : " + Thread.currentThread().getName());
		Validators.checkNull(audit, "Empty Audit!");
		auditExecutor.execute(()->{
			audits.add(audit);
			System.out.println("%%%%%%%%%%%%%%%InsertAudit in executor : " + Thread.currentThread().getName());
		});
	}
}
