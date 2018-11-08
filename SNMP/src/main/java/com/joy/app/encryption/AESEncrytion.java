package com.joy.app.encryption;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * 
 * @author joy
 *	
 *	class used for AES encryption 
 *	keys can be better placed in config files
 *	NOTE: publicizing this code at github will publicize the key too. 
 */
public class AESEncrytion {

	Cipher cipher;
	Key aesKey;
	private String key="test@snmp8765432";
	private String ivstr="randomIV(*&^%$#@";
	
public AESEncrytion() {
	// TODO Auto-generated constructor stub
	
	try {
		
		cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
		aesKey = new SecretKeySpec(key.getBytes(), "AES");
		
	} catch (NoSuchAlgorithmException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (NoSuchPaddingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}


public String encrypt(String text){
	
	try {
		
		
		cipher.init(Cipher.ENCRYPT_MODE, aesKey, new IvParameterSpec(ivstr.getBytes()));
		byte[] encrypted = cipher.doFinal(text.getBytes());
		
		return new String(Base64.getEncoder().encodeToString(encrypted));
		
	} catch (InvalidKeyException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IllegalBlockSizeException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (BadPaddingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (InvalidAlgorithmParameterException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return null;
	
	
	
}
	
	
public String decrypt(String encrypted){
	
	try {
		
		byte[] enc = Base64.getDecoder().decode(encrypted);
		
		cipher.init(Cipher.DECRYPT_MODE, aesKey, new IvParameterSpec(ivstr.getBytes()));
		String decrypted = new String(cipher.doFinal(enc));
		
		return decrypted;
		
	} catch (InvalidKeyException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IllegalBlockSizeException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (BadPaddingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (InvalidAlgorithmParameterException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return null;
	
}

	
}
