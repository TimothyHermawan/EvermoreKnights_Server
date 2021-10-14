package nomina.evermoreknights.SharedClass;

import com.smartfoxserver.v2.exceptions.IErrorCode;

public enum CustomErrors implements IErrorCode
{
   USERNAME_TAKEN(1000),
   EMAIL_TAKEN(1001),
   INVALID_CREDENTIAL(1002),
   TRANSACTION_FAILED(1003),
   FAIL_UPDATE_TOKEN(1004);
   
   private CustomErrors(int id)
   {
      this.id = (short) id;
   }
   
   private short id;
   
   @Override
   public short getId()
   {
	   return id;
   }
   
   public static CustomErrors getEnum(short value){
       for (CustomErrors e:CustomErrors.values()) {
           if(e.getId() == value)
               return e;
       }
       return CustomErrors.USERNAME_TAKEN;//For values out of enum scope
   }
   
}