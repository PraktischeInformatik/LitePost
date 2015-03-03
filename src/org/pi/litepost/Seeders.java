package org.pi.litepost;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;

import org.pi.litepost.databaseAccess.DatabaseQueryManager;
import org.pi.litepost.databaseAccess.IDatabaseSeeder;

public class Seeders {
	public static Collection<IDatabaseSeeder> getAll() {
		return Arrays.asList(
			new Users(),
			new Messages(),
			new Posts(),
			new Comments()
		);
	}
}

class Users implements IDatabaseSeeder {
	
	@Override
	public void seed(DatabaseQueryManager db) throws Exception{

		db.executeQuery("insertUser", "uhukoenig123", PasswordHash.createHash("troll22"), "Markus", "Cohenheim", "blubla@gmx.de");
		db.executeQuery("insertUser", "uhukönig123", PasswordHash.createHash("troll22"), "Reiner", "Mahan", "Reiner_Mahan@gmx.de");
		db.executeQuery("insertUser", "FRUITSPONGYSAMURAISAN", PasswordHash.createHash("troll22"), "Sebastian", "Rosenthal", "emo_lord89@gmx.de");
		db.executeQuery("insertUser", "PakoElectronics", PasswordHash.createHash("troll222"), "Herbert", "Kranz", "PakoElectronixs@gmx.de");
		db.executeQuery("insertUser", "admin", PasswordHash.createHash("admin"), "Jens", "Stahlträger", "jens@mcfit.de");
		
		db.executeQuery("verifyEmail", 1);
		db.executeQuery("verifyEmail", 2);
		db.executeQuery("verifyEmail", 3);
		db.executeQuery("verifyEmail", 4);
		db.executeQuery("verifyEmail", 5);
		
		db.executeQuery("setAdmin", 5);
	}
}

class Messages implements IDatabaseSeeder {

	@Override
	public void seed(DatabaseQueryManager db) throws Exception {
		db.executeQuery("insertMessage", LocalDateTime.now(), 1, 2, 0, "Test", "Testing");
		db.executeQuery("insertMessage", LocalDateTime.now(), 1, 2, 1, "Test", "Testing");
		
		db.executeQuery("insertMessage", LocalDateTime.now(), 2, 1, 0, "Test2", "Testing2");
		db.executeQuery("insertMessage", LocalDateTime.now(), 2, 1, 1, "Test2", "Testing2");
	}
}

class Posts implements IDatabaseSeeder {

	@Override
	public void seed(DatabaseQueryManager db) throws Exception {
		db.executeQuery("insertPost", "Was ist Text?", "Text schmerz mich", LocalDateTime.of(2015, 2, 15, 5, 15), "0090/99913213", 1);
		
		//Diese beiden sollten dierekt beim ersten request raus fliegen, weil sie zu alt sind
		db.executeQuery("insertPost", "Old but gold", "Oldies but goldies", LocalDateTime.now().minusMonths(1), "0800/666666", 0);
		db.executeQuery("insertPost", "Schnee von gestern", "Party schon vorbei", LocalDateTime.now().minusMonths(1), "Partystraße 5", 0);
		db.executeQuery("makeEvent", 1 , LocalDateTime.of(2015, 3, 15, 5, 15));
		db.executeQuery("makeEvent", 2 , LocalDateTime.now().minusDays(1));
		
		db.executeQuery("insertPost", "Mein supertolles neues Fahrrad", "Mir wurde zu Weihnachten ein supertolles neues Fahrrad geschenkt, welches ich aber nicht brauche, also wollte ich es verkaufen.", LocalDateTime.of(2015, 2, 15, 5, 20), "Schlachthausstraße 19", 3);
		db.executeQuery("insertPost", "Gamer PC DDDDD FFFFF 00000 1 x 109191000hz TKraft 73", "", LocalDateTime.of(2015, 2, 15, 8, 1), "Burgumstraße 20", 4);
		db.executeQuery("insertPost", "Schärfe-Wettessen", "Beispiel-Text zur Demonstration des Forums.", LocalDateTime.of(2015, 2, 15, 17, 15), "I-wo beliebiges", 2);
	}
}

class Comments implements IDatabaseSeeder {

	@Override
	public void seed(DatabaseQueryManager db) throws Exception {
		db.executeQuery("insertComment", 3, "nicht weiter!", LocalDateTime.of(2015, 2, 15, 5, 17), 0, 1);
		db.executeQuery("insertComment", 1, "lol", LocalDateTime.of(2015, 2, 15, 5, 18), 1, 1);
	}
}
