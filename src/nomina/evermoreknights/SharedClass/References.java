package nomina.evermoreknights.SharedClass;

public class References {
	
	public static class CustomLoginData{		
		public static String Authentication_Type = "Authentication_Type";	// Value: Login, Register
		public static String Register_Email = "Email";	
		public static String Register_Password = "Password";	
	}
	
	public static class DatabaseSettings{
		public static String MongoDB_Database = "evermoreknight";  // the name of the database in which the username is defined
		public static String Username = "smartfoxserver";  
		public static String Password = "smartfoxserver";  
		public static String Host = "127.0.0.1";  
		public static int Port = 27017;  
	}
	
	public static class DatabaseCollection{
		public static String Players = "players";  
		public static String Currency = "currency";  
		public static String Currency_Receipt = "currency_receipt";  
	}

}
