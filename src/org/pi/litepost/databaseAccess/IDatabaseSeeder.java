package org.pi.litepost.databaseAccess;

public interface IDatabaseSeeder {
	public void seed(DatabaseQueryManager db) throws Exception;
	public default String getName() {
		return getClass().getSimpleName();
	}
}
