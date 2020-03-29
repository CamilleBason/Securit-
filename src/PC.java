/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author camil
 */
import com.sun.org.apache.xml.internal.security.utils.Base64;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
//import java.net.UnknownHostException;

public class PC {
	static  ServerSocket server;
	static int clientID = 0;
        static SecretKey key;
	
	public static void main(String ard[]){
		
		try{
                        File f = new File("cle.txt");
                        System.out.println("Fichier clé crée");
                        //on genere une cle grâce a Key generator en utilisant AES
                        KeyGenerator generatorkey = KeyGenerator.getInstance("AES");
                        generatorkey.init(128);
                        SecretKey cle = generatorkey.generateKey();
                        byte[] ByteKey = cle.getEncoded();
                        String finalKey = Base64.encode(ByteKey);
                        //On inscrit la clé générée dans le fichier cle.txt
                        FileWriter writer = new FileWriter(f.getAbsoluteFile());
                        BufferedWriter buff = new BufferedWriter(writer);
                        buff.write(finalKey);
                        //On affiche la clé
                        System.out.println("Notre clé est : " + finalKey);
                        buff.close();
                        writer.close();
                        
                        server = new ServerSocket(4444, 5);//5 connexions clientes au plus
			go();
		}catch(Exception e){}
	}
	
	
	public static void go() {	
		
		
		try{			
			Thread t = new Thread(new Runnable(){
				public void run(){
					while(true)//
					{
						try{
							Socket client = server.accept();
							// Faire tourner le socket qui s'occupe de ce client dans son propre thread et revenir en attente de la prochaine connexion
							// Le chat avec l'entité connectée est encapsulé par une instance de ChatServer
							Thread tAccueil = new Thread(new ChatServer(client, clientID));
							tAccueil.start();
							clientID++;
						}catch(Exception e){}
					}
				}
			});
			t.start();

		}
		catch(Exception i){
			System.out.println("Impossible d'écouter sur le port 4444: serait-il occupé?");		
			i.printStackTrace();
		}
	}
}

