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
		
		dbQueryManager.executeQuery("insertPost", "What is love?", "Baby don't hurt me, don't hurt me...", LocalDateTime.of(2015, 2, 15, 5, 15), "0090/99913213", 1);
		dbQueryManager.executeQuery("insertPost", "Mein supertolles neues Fahrrad", "Mir wurde zu Weihnachten ein supertolles neues Fahrrad geschenkt, welches ich aber nicht brauche, also wollte ich es verkaufen.", LocalDateTime.of(2015, 2, 15, 5, 20), "Schlachthausstraße 19", 3);
		dbQueryManager.executeQuery("insertPost", "Gamer PC Computer AMD FX 8320 8 x 4.000 Mhz Geforce GT 730 4GB 16GB Ram Gaming", "Prozessor AMD FX-8320 8-Core 3.5GHz AM3+nVidia GeForce GT 730 4GB GrafikAnschlüsse: HDMI - DVI - VG DirectX 11 Festplatte 	1000 GB Festplatte - SATA III - 7200 rpm...", LocalDateTime.of(2015, 2, 15, 8, 1), "Burgumstraße 20", 4);
		dbQueryManager.executeQuery("insertPost", "Schärfe-Wettessen: Jeder Biss eine Qual", "Der Mund brennt wie Feuer, Schweißperlen tropfen auf den Tisch und jeder Löffel Soße ist eine Qual: In Bad Schwartau bei Lübeck haben sich am Sonnabend 40 Männer und Frauen bei der deutschen Meisterschaft im Schärfe-Wettessen gemessen. Mit dabei: der amtierende Weltmeister Stephan Kühne aus Berlin.", LocalDateTime.of(2015, 2, 15, 17, 15), "In Bad Schwartau bei Lübeck", 2);
		
		dbQueryManager.executeQuery("insertComment", 3, "no more!", LocalDateTime.of(2015, 2, 15, 5, 17), 0, 1);
		dbQueryManager.executeQuery("insertComment", 1, "lol", LocalDateTime.of(2015, 2, 15, 5, 18), 1, 1);
		
		
		DbConnector.close();
	}
}
