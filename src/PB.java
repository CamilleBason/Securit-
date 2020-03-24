/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author camil
 */
import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;



public class PB {
    
    static SecretKey key;
    
	public static void main(String[] args) throws FileNotFoundException, IOException, Base64DecodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		InetAddress addr;
		Socket client;
		PrintWriter out;
		BufferedReader in;
		String input;
		String userInput;
		boolean doRun = true;
                // creer un fichier contenant la clé
                File f = new File("cle.txt");
                FileReader reader = new FileReader(f.getAbsoluteFile());
                BufferedReader text = new BufferedReader(reader);
                String cle = text.readLine();
                //transformer le clé en byte
		byte[] ByteKey = Base64.decode(cle);
                
                //genere une clef AES
                key = new SecretKeySpec(ByteKey, 0, ByteKey.length, "AES");
		reader.close();
                text.close();
		Scanner k = new Scanner(System.in);
		try{    
                        // creer un algo AES
                        Cipher cipher = Cipher.getInstance("AES");
                        com.sun.org.apache.xml.internal.security.Init.init();
			client = new Socket("localhost", 4444);
			in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			out = new PrintWriter(client.getOutputStream(), true);
			
			System.out.print("enter msg> ");
			userInput = k.nextLine();
			out.println(userInput);
                        //crypte le message
                        cipher.init(Cipher.ENCRYPT_MODE, key);
                        byte[] ByteMessage = cipher.doFinal(userInput.getBytes("UTF-8"));
                        String MessageCrypte = Base64.encode(ByteMessage);
                        // affiche message crypté
                        out.println(MessageCrypte);
			out.flush();
			System.out.println("Message crypté !");
			
			if(userInput.compareToIgnoreCase("bye")==0)
			{
				System.out.println("shutting down");
				doRun = false;
			}else
			{
				while(doRun){
					input = in.readLine();
                                        //On execute un cypher en mode décrypte
                                        cipher.init(Cipher.DECRYPT_MODE, key);
					while(input == null) input = in.readLine();
					System.out.println("Les message codé reçu est" +input);
                                        byte[] messageDecode = cipher.doFinal(Base64.decode(input));
                                        input = new String(messageDecode);
                                        System.out.println("Le message décodé est : "+ input);
                                        //si le message est bye on etteint 
					if(input.compareToIgnoreCase("bye")==0)
					{
						System.out.println("client shutting down from server request");
						doRun = false;
					}else
					{
                                            //l'utilisateur peut repondre
						System.out.print("enter msg> ");
						userInput = k.nextLine();
						out.println(userInput);
						out.flush();
						if(userInput.compareToIgnoreCase("bye")==0)
						{
							System.out.println("shutting down");
							doRun = false;
						}
						
					}
				}
			}
			client.close();
			k.close();
		}catch(UnknownHostException e){
			e.printStackTrace();
		}
		catch(IOException ioe){
			System.out.println(ioe.getMessage());
		}
	}
}
