<<<<<<< HEAD
package org.pi.litepost;

import java.io.File;
import java.sql.SQLException;
import java.time.LocalDateTime;

import org.pi.litepost.databaseAccess.*;

public class Seeder {
	
	public static void seed() throws Exception{
		File data = new File("res/litepost.db");
	    if (data.exists()) {
	    	data.delete();
	    }
		DatabaseConnector DbConnector = new DatabaseConnector("res/litepost.db");
		DbConnector.connect();
		DatabaseQueryManager dbQueryManager= new DatabaseQueryManager(DbConnector);
		
		dbQueryManager.executeQuery("insertUser", "uhukoenig123", "troll22", "Markus", "Cohenheim", "blubla@gmx.de");
		dbQueryManager.executeQuery("insertUser", "uhukönig123", "troll22", "Reiner", "Mahan", "Reiner_Mahan@gmx.de");
		dbQueryManager.executeQuery("insertUser", "FRUITSPONGYSAMURAISAN", "troll22", "Sebastian", "Rosenthal", "emo_lord89@gmx.de");
		dbQueryManager.executeQuery("insertUser", "PakoElectronics", "troll222", "Herbert", "Kranz", "PakoElectronixs@gmx.de");
		
		dbQueryManager.executeQuery("insertPost", "Was ist Text?", "Text schmerz mich", LocalDateTime.of(2015, 2, 15, 5, 15), "0090/99913213", 1);
		dbQueryManager.executeQuery("insertPost", "Mein supertolles neues Fahrrad", "Mir wurde zu Weihnachten ein supertolles neues Fahrrad geschenkt, welches ich aber nicht brauche, also wollte ich es verkaufen.", LocalDateTime.of(2015, 2, 15, 5, 20), "Schlachthausstraße 19", 3);
		dbQueryManager.executeQuery("insertPost", "Gamer PC DDDDD FFFFF 00000 1 x 109191000hz TKraft 73", "", LocalDateTime.of(2015, 2, 15, 8, 1), "Burgumstraße 20", 4);
		dbQueryManager.executeQuery("insertPost", "Schärfe-Wettessen", "Beispiel-Text zur Demonstration des Forums.", LocalDateTime.of(2015, 2, 15, 17, 15), "I-wo beliebiges", 2);
		
		dbQueryManager.executeQuery("insertComment", 3, "nicht weiter!", LocalDateTime.of(2015, 2, 15, 5, 17), 0, 1);
		dbQueryManager.executeQuery("insertComment", 1, "lol", LocalDateTime.of(2015, 2, 15, 5, 18), 1, 1);
		
		
		DbConnector.close();
	}
}
=======
package org.pi.litepost;

import java.io.File;
import java.sql.SQLException;
import java.time.LocalDateTime;

import org.pi.litepost.databaseAccess.*;

public class Seeder {
	
	public static void seed() throws Exception{
		File data = new File("res/litepost.db");
	    if (data.exists()) {
	    	data.delete();
	    }
		DatabaseConnector DbConnector = new DatabaseConnector("res/litepost.db");
		DbConnector.connect();
		DatabaseQueryManager dbQueryManager= new DatabaseQueryManager(DbConnector);
		
		dbQueryManager.executeQuery("insertUser", "uhukoenig123", "troll22", "Markus", "Cohenheim", "blubla@gmx.de");
		dbQueryManager.executeQuery("insertUser", "uhukönig123", "troll22", "Reiner", "Mahan", "Reiner_Mahan@gmx.de");
		dbQueryManager.executeQuery("insertUser", "FRUITSPONGYSAMURAISAN", "troll22", "Sebastian", "Rosenthal", "emo_lord89@gmx.de");
		dbQueryManager.executeQuery("insertUser", "PakoElectronics", "troll222", "Herbert", "Kranz", "PakoElectronixs@gmx.de");
		
		dbQueryManager.executeQuery("insertPost", "Was ist Text?", "Text schmerz mich", LocalDateTime.of(2015, 2, 15, 5, 15), "0090/99913213", 1);
		dbQueryManager.executeQuery("insertPost", "Mein supertolles neues Fahrrad", "Mir wurde zu Weihnachten ein supertolles neues Fahrrad geschenkt, welches ich aber nicht brauche, also wollte ich es verkaufen.", LocalDateTime.of(2015, 2, 15, 5, 20), "Schlachthausstraße 19", 3);
		dbQueryManager.executeQuery("insertPost", "Gamer PC DDDDD FFFFF 00000 1 x 109191000hz TKraft 73", "", LocalDateTime.of(2015, 2, 15, 8, 1), "Burgumstraße 20", 4);
		dbQueryManager.executeQuery("insertPost", "Schärfe-Wettessen", "Beispiel-Text zur Demonstration des Forums.", LocalDateTime.of(2015, 2, 15, 17, 15), "I-wo beliebiges", 2);
		
		dbQueryManager.executeQuery("insertComment", 3, "nicht weiter!", LocalDateTime.of(2015, 2, 15, 5, 17), 0, 1);
		dbQueryManager.executeQuery("insertComment", 1, "lol", LocalDateTime.of(2015, 2, 15, 5, 18), 1, 1);
		
		
		DbConnector.close();
	}
}
>>>>>>> branch 'master' of https://github.com/PraktischeInformatik/LitePost.git
