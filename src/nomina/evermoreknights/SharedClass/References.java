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
		public static String Stamina = "stamina";  
	}
	
	public static class SmartfoxCMD{
		public static String Prefix_Transaction = "transaction";
		public static String Prefix_PlayerData = "player";
		public static String Prefix_Stamina = "stamina";
		
		public static String Transaction_DoTransaction = "doTransaction";
		public static String Transaction_Get_Invoice = "getInvoice";
		
		public static String Player_GetPlayerData= "getPlayerData";
		
		public static String Stamina_AdjustStamina= "adjustStamina";
		public static String Stamina_CheckRegen= "checkRegen";
		
		public static String Ping = "ping";
		
		public static String Update_Currency = "updateCurrency";
		public static String Update_Stamina = "updateStamina";
	}
	
	public static class StaminaSettings{
		public static int Max_Value = 100;		
		public static int RegenTimeInMinute = 10;		
		public static int RegenAmount = 10;
	}

}
