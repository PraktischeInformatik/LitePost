package org.pi.litepost.databaseAccess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DatabaseSchema {
	public static final Schema SCHEMA = new Schema();
	static {
		SCHEMA
			.table("Users")
				.column("user_id", "INT").notNull().primaryKey()
		        .column("username", "VARCHAR(255)").notNull()
		        .column("password", "VARCHAR(102)").notNull()
		        .column("firstname", "VARCHAR(255)").notNull()
		        .column("lastname", "VARCHAR(255)").notNull()
		        .column("email", "VARCHAR(255)").notNull()
		        .column("admin", "INT(1)").notNull().defaultVal(0)
	        .table("Messages")
		        .column("message_id", "INT").notNull().primaryKey()
		        .column("date", "DATE").notNull()
		        .column("sender", "INT").notNull().foreignKey("Users", "user_id")
		        .column("receiver", "INT").notNull().foreignKey("Users", "user_id")
		        .column("subject", "TEXT").notNull()
		        .column("content", "TEXT").notNull()
		        .column("hidden", "INT(1)").notNull().defaultVal(0)
		        .column("read", "INT(1)").notNull().defaultVal(0)
			.table("Posts")
			    .column("post_id", "INT").notNull().primaryKey()
			    .column("title", "TEXT").notNull()
			    .column("content", "TEXT").notNull()
			    .column("date", "DATE").notNull()
			    .column("contact", "TEXT").notNull()
			    .column("user_id", "INT").notNull().foreignKey("Users", "user_id")
			    .column("reported", "INT(1)").notNull()
			    .column("presentation", "INT(1)").notNull()
			.table("Events")
			    .column("event_id", "INT").notNull().primaryKey()
			    .column("post_id", "INT").notNull().foreignKey("Posts", "post_id")
			    .column("event_date", "DATE").notNull()
		    .table("Comments")
			    .column("comment_id", "INT").notNull().primaryKey()
			    .column("user_id", "INT").notNull().foreignKey("Users", "user_id")
			    .column("content", "TEXT").notNull()
			    .column("date", "DATE").notNull() 
			    .column("parent_id", "INT").notNull().defaultVal(0)
			    .column("post_id", "INT").notNull().foreignKey("Posts", "post_id")
			.table("Sessions")
			    .column("session_id", "VARCHAR(255)").notNull().primaryKey()
			    .column("key", "VARCHAR(64)").notNull().primaryKey()
			    .column("value", "TEXT").notNull()
			.table("Ids")
			    .column("table_name", "VARCHAR(128)").notNull().primaryKey()
			    .column("next_id", "INT").notNull().defaultVal(0)
			.table("Images")
			    .column("image_id", "INT").notNull().primaryKey()
			    .column("source", "TEXT").notNull()
			.table("Post_has_Images")
			    .column("image_id", "INT").notNull().primaryKey().foreignKey("Posts", "post_id")
			    .column("post_id", "INT").notNull().primaryKey().foreignKey("Images", "image_id")
			.afterCreation("INSERT INTO Ids(table_name, next_id) VALUES(\"Users\", 1)")
			.afterCreation("INSERT INTO Ids(table_name, next_id) VALUES(\"Messages\", 1)")
			.afterCreation("INSERT INTO Ids(table_name, next_id) VALUES(\"Posts\", 1)")
			.afterCreation("INSERT INTO Ids(table_name, next_id) VALUES(\"Events\", 1)")
			.afterCreation("INSERT INTO Ids(table_name, next_id) VALUES(\"Comments\", 1)")
			.afterCreation("INSERT INTO Ids(table_name, next_id) VALUES(\"Images\", 1)");
	}
}

class Schema {
	ArrayList<Table> tables = new ArrayList<>();
	ArrayList<String> afterCreate = new ArrayList<>();
	public Table table(String name) {
		Table t = new Table(name, this);
		tables.add(t);
		return t;
	}
	
	public Schema afterCreation(String sql) {
		afterCreate.add(sql);
		return this;
	}
	
	public List<String> getCreate() {
		List<String> sql = tables.stream().map(Table::toString).collect(Collectors.toList());
		sql.addAll(afterCreate);
		return sql;
		
	}
	
	public List<String> getDropAndCreate() {
		List<String> sql = tables.stream().flatMap(Table::dropAndCreate).collect(Collectors.toList());
		sql.addAll(afterCreate);
		return sql;
	}
	
	public boolean validate(HashMap<String, ArrayList<String>> existingSchema) {
		for(Table table : tables) {
			if(!existingSchema.containsKey(table.name)) {
				return false;
			}
			if(!table.validate(existingSchema.get(table.name))) {
				return false;
			}
		}
		return true;
	}
}

class Table {
	String name;
	ArrayList<Column> columns = new ArrayList<>();
	ArrayList<ForeignKey> foreignKeys = new ArrayList<>();
	PrimaryKey primaryKey = new PrimaryKey();
	Schema schema;
	
	public Table(String name, Schema schema) {
		this.name = name;
		this.schema = schema;
	}
	
	public boolean validate(ArrayList<String> existingColumns) {
		for(Column column : columns) {
			if(!existingColumns.contains(column.name)) {
				return false;
			}
		}
		return true;
	}

	public Column column(String name, String definition) {
		Column col = new Column(name, definition, this);
		columns.add(col);
		return col;
	}
	
	public Table table(String name) {
		return schema.table(name);
	}
	
	public Schema afterCreation(String sql) {
		schema.afterCreate.add(sql);
		return schema;
	}
	
	public Stream<String> dropAndCreate() {
		String drop = String.format("DROP TABLE IF EXISTS %s;", name);
		String create = toString();
		return Stream.of(drop, create);
	}
	
	@Override
	public String toString() {
		String definition = columns.stream()
				.map(Column::toString)
				.reduce((a, s) -> a + ",\n\t" + s)
				.get();
		if(!primaryKey.empty()) {
			definition += ",\n\t" + primaryKey.toString();
		}
		if(foreignKeys.size() != 0) {
			definition += ",\n\t" + foreignKeys.stream()
					.map(ForeignKey::toString)
					.reduce((a, s) -> a + ",\n\t" + s)
					.get();
		}
		return String.format("CREATE TABLE %s(\n"
				+ "\t%s\n"
				+ ");", name, definition);
	}
}

class Column {
	String name;
	String definition;
	Table table;
	public Column(String name, String type, Table table) {
		this.name = name;
		this.definition = type;
		this.table = table;
	}
	
	public Table table(String name) {
		return table.table(name);
	}
	
	public Schema afterCreation(String sql) {
		table.schema.afterCreate.add(sql);
		return table.schema;
	}

	public Column notNull() {
		definition += " NOT NULL";
		return this;
	}
	
	public Column defaultVal(Object value) {
		definition += " DEFAULT " + value.toString();
		return this;
	}
	
	public Column unique() {
		definition += " UNIQUE";
		return this;
	}
	
	public Column primaryKey() {
		table.primaryKey.add(this);
		return this;
	}
	
	public Column foreignKey(String otherTable, String otherColumn) {
		table.foreignKeys.add(new ForeignKey(this, otherTable, otherColumn));
		return this;
	}
	
	public Column column(String name, String definition) {
		return table.column(name, definition);
	}
	
	@Override
	public String toString() {
		return String.format("%s %s", name, definition);
	}
}

class PrimaryKey {
	ArrayList<Column> columns = new ArrayList<>();
	public PrimaryKey() {}
	
	public void add(Column col) {
		columns.add(col);
	}
	
	public boolean empty() {
		return columns.size() == 0;
 	}
	
	@Override
	public String toString() {
		String cols = columns.stream()
			.map(c -> c.name)
			.reduce((a, s) -> a + ", " + s)
			.get();
		return String.format("PRIMARY KEY(%s)", cols);
	}
}

class ForeignKey  {
	Column column;
	String otherTable;
	String otherColumn;
	public ForeignKey(Column column, String otherTable, String otherColumn) {
		this.column = column;
		this.otherTable = otherTable;
		this.otherColumn = otherColumn;
	}
	
	@Override
	public String toString() {
		return String.format("FOREIGN KEY(%s) REFERENCES %s(%s)",
				column.name,
				otherTable,
				otherColumn); 
	}
}