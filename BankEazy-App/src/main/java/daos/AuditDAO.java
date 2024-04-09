package daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import exception.CustomBankException;
import jdbc.JDBCConnector;
import model.Audit;
import utilities.Validators;

public class AuditDAO implements AuditDaoInterface{
	
	private static final String defaultDBName = "BankEazy";

	@Override
	public boolean addAudit(Audit audit) throws CustomBankException {
		Validators.checkNull(audit, "Empty Audit value");
		try (Connection connection = JDBCConnector.getConnection(defaultDBName)){
			try (PreparedStatement statement = connection.prepareStatement("insert into Audit(userId, targetId, action, status, time, description) values(?, ?, ?, ?, ?, ?)")){
				statement.setObject(1, audit.getUserId());
				statement.setObject(2, audit.getTargetId());
				statement.setObject(3, audit.getAction());
				statement.setObject(4, audit.getStatus());
				statement.setObject(5, audit.getTime());
				statement.setObject(6, audit.getDescription());
				int noOfRowsAffected = statement.executeUpdate();
				return noOfRowsAffected > 0;
			}
		} catch (SQLException e) {
			throw new CustomBankException(e.getMessage());
		}
	}

}
