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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
        File f = new File("cle.txt");
        // recupérer la clé dans le fichier cle.txt
        try (FileReader reader = new FileReader(f.getAbsoluteFile()); BufferedReader text = new BufferedReader(reader)) {
            String cle = text.readLine();
            //transformer le clé en byte
            byte[] ByteKey = Base64.decode(cle);

            //genere une clef AES a partir de la cle du fichier
            key = new SecretKeySpec(ByteKey, 0, ByteKey.length, "AES");
        }

        Scanner k = new Scanner(System.in);
        try {
            // creer un cipher AES
            Cipher cipher = Cipher.getInstance("AES");
            client = new Socket("localhost", 4444);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new PrintWriter(client.getOutputStream(), true);
            //on récupère le message entré par l'utilisateur
            System.out.print("enter msg> ");
            userInput = k.nextLine();
            //crypte le message grâce a un cipher en mode cryptage
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] ByteMessage = cipher.doFinal(userInput.getBytes("UTF-8"));
            String MessageCrypte = Base64.encode(ByteMessage);
            // affiche message crypté
            out.println(MessageCrypte);
            out.flush();
            System.out.println("Message crypté !");
            //si le message non crypté en bye, on coupe la communication
            if (userInput.compareToIgnoreCase("bye") == 0) {
                System.out.println("shutting down");
                doRun = false;
            } else {
                while (doRun) {
                    //si un message est recu
                    input = in.readLine();
                    //On execute un cypher en mode décrypte
                    cipher.init(Cipher.DECRYPT_MODE, key);
                    while (input == null) {
                        input = in.readLine();
                    }
                    //on affiche le message codé
                    System.out.println("Les message codé reçu est" + input);
                    //on décrypte le message
                    byte[] messageDecode = cipher.doFinal(Base64.decode(input));
                    input = new String(messageDecode);
                    //on affiche le message décrypté
                    System.out.println("Le message décodé est : " + input);
                    //si le message est bye on etteint la communication
                    if (input.compareToIgnoreCase("bye") == 0) {
                        System.out.println("client shutting down from server request");
                        doRun = false;
                    } else {
                        //l'utilisateur peut repondre
                        System.out.print("enter msg> ");
                        userInput = k.nextLine();
                        cipher.init(Cipher.ENCRYPT_MODE,key);
                        byte[] messageByte = cipher.doFinal(userInput.getBytes());
                        String messageCode = Base64.encode(messageByte);                        
                        out.println( messageCode);
                        out.flush();
                        if (userInput.compareToIgnoreCase("bye") == 0) {
                            System.out.println("shutting down");
                            doRun = false;
                        }

                    }
                }
            }
            client.close();
            k.close();
        } catch (UnknownHostException e) {
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
    }
}
