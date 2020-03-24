/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author camil
 */
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;

import com.sun.org.apache.xml.internal.security.utils.Base64;

import java.util.Base64.*;


public class Cryp {

	/**
	 * @param args
	 */
	
	final String encryptedValue = "I saw the real you" ;
	String secKey = "ubutru";

	
	public static void main(String[] args) {
		
		String encryptedVal = null;
                //message a coder
		String valueEnc = "aazzaa";

	    try {
	    		    //demande clés AES	
                    KeyGenerator generator = KeyGenerator.getInstance("AES");
	            generator.init(128);
	            SecretKey key = generator.generateKey();
	            Cipher cipher = Cipher.getInstance("AES");
                    //chiffrement
	            cipher.init(Cipher.ENCRYPT_MODE, key);
                    //transforme la valeur en bit
	            byte[] res = cipher.doFinal(valueEnc.getBytes());
	            String res_str =  Base64.encode(res);//new String(res);
	           
                    //dechiffrement
	            cipher.init(Cipher.DECRYPT_MODE, key);
	               
	            byte[] res2 = cipher.doFinal(Base64.decode(res_str));
	                //byte[] res2 = cipher.doFinal(res_str.getBytes("utf-8"));
	            String res_str2 =  new String(res2);
	           
	            System.out.println("source:"+valueEnc);
	            System.out.println("enc:"+res_str);
	            System.out.println("dec:"+res_str2);
	           
	            byte[] enck = key.getEncoded();
	            System.out.println(Base64.encode(enck));
	           
	           
	            String encoded = "r1peJOWYRRod8IibmrYoPA==";
	            String key_str = "+WHQtDsr9LJQ05/2MHZkQQ==";
	           
	            byte[] kb = Base64.decode(key_str.getBytes());
	            SecretKeySpec ksp = new SecretKeySpec(kb, "AES");
	           
	            Cipher cipher2 = Cipher.getInstance("AES");
	            cipher2.init(Cipher.DECRYPT_MODE, ksp);
	            byte[] res3 = cipher2.doFinal(Base64.decode(encoded));
	            String res_str3 =  new String(res3);
	            System.out.println("obtained: "+res_str3);
	        
	       
	    } catch(Exception ex) {
	        System.out.println("The Exception is=" + ex);
	    }
	}

}
