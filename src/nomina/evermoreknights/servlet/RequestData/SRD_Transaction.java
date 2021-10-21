package nomina.evermoreknights.servlet.RequestData;

public class SRD_Transaction {
	public String secret;
	public String pid;  // receiver
	public int type;
	public double amount;
	public String message;
	public boolean updateClient = true;
}
