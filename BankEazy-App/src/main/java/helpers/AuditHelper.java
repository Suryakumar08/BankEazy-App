package helpers;

import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import daos.AuditDAO;
import daos.AuditDaoInterface;
import exception.CustomBankException;
import model.Audit;
import utilities.Validators;

public class AuditHelper {
	
//	private static ExecutorService auditExecutor = null;
//	private static AuditDaoInterface auditDao = new AuditDAO();
//	
//	public AuditHelper() {
//		if(auditExecutor == null) {
//			auditExecutor = new ThreadPoolExecutor(0, 50,
//                    60L, TimeUnit.SECONDS,
//                    new LinkedBlockingQueue<Runnable>());
//		}
//	}

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
	
	
	
	//Executor Service using runnable....
//	public void insertAudit(Audit audit) throws CustomBankException{
//		Validators.checkNull(audit, "Empty Audit!");
//		auditExecutor.execute(addTask(audit));
//	}
//	
//	private Runnable addTask(Audit audit) {
//		return new Runnable() {
//			@Override
//			public void run() {
//				try {
//					auditDao.addAudit(audit);
//				} catch (CustomBankException e) {
//				}
//			}
//		};
//	}
	
	
	
	
	//Improved Auditing!!
	
	
	 private static AuditDaoInterface auditDao = null;
	 private static ExecutorService auditExecutor = null;
	 private static CompletionService<String> completionService = null;
	 private static ExecutorService completionHandler = null;
	 private static final Semaphore semaphore = new Semaphore(100, true);;
	 
	 public AuditHelper() {
		 if(auditDao == null) {
			 auditDao = new AuditDAO();			 
		 }
		 if(auditExecutor == null) {
			 auditExecutor = new ThreadPoolExecutor(0, 50, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), new RejectionHandler());			 
		 }
		 if(completionService == null) {
			 completionService = new ExecutorCompletionService<>(auditExecutor);			 
		 }
		 if(completionHandler == null) {
			 completionHandler = Executors.newSingleThreadExecutor();
		 }
	 }
	


     public void insertAudit(Audit audit) throws CustomBankException {
    	 Validators.checkNull(audit, "Audit null!");
    	 
    	 try {
			semaphore.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	 
    	 completionService.submit(()->{
    		 try {
				auditDao.addAudit(audit);
				semaphore.release();
				return "Audit addition success!";
			} catch (CustomBankException e) {
				throw new ExecutionException("Audit entry failed!" ,e);
			}
    	 });
    	 
    	 completionHandler.submit(()->{
    			try {
					Future<String> future = completionService.take();
					System.out.println(future.get());
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
    	 });
     }
     
     
     class RejectionHandler implements RejectedExecutionHandler {
         @Override
         public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
             // Handling the rejected task here
             if (r instanceof FutureTask) {
                 FutureTask<?> futureTask = (FutureTask<?>) r;
                 if (!executor.isShutdown()) {
                     executor.submit(futureTask);
                 }
             }
         }
     }
}
