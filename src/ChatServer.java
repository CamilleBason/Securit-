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
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class ChatServer implements Runnable {

    PrintWriter out;
    BufferedReader in;
    Socket s;
    Scanner keyboard;
    int index;
    String input;
    boolean doRun = true;
    static SecretKey key;

    public ChatServer(Socket a, int u) {
        s = a;
        keyboard = new Scanner(System.in);
        index = u;
    }

    public void run() {
        File f = new File("cle.txt");
        FileReader reader;
        try {
            //com.sun.org.apache.xml.internal.security.Init.init();
            Cipher cipher = Cipher.getInstance("AES");
            //on récupère la clé du fichier cle.txt
            reader = new FileReader(f.getAbsoluteFile());
            BufferedReader text = new BufferedReader(reader);
            String cle = text.readLine();
            byte[] ByteKey = Base64.decode(cle);
            //on initialise key, qui seras la clé AES 
            key = new SecretKeySpec(ByteKey, 0, ByteKey.length, "AES");
            text.close();
            reader.close();
            
            
            try {              
                in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                out = new PrintWriter(s.getOutputStream());

                System.out.println("connexion de " + s.getInetAddress().toString() + " sur le port " + s.getPort());
                //recupère le message codé
                String talk = in.readLine();
                while (doRun) {
                    while (talk == null) {
                        talk = in.readLine();
                    }
                    //affiche le message codé recu
                    System.out.println("Le message codé reçu est : " + talk);
                    //on crée un cypher en mode décryptage, avec la clé key
                    cipher.init(Cipher.DECRYPT_MODE, key);
                    byte[] messageDecodeByt = cipher.doFinal(Base64.decode(talk));
                    talk = new String(messageDecodeByt);
                    //on affiche le message décrypté
                    System.out.println("Le message décodé est : " + talk);
                    // si le message est bye on coupe la communication
                    if (talk.compareToIgnoreCase("bye") == 0) {
                        System.out.println("shutting down following remote request");
                        doRun = false;
                    } else {
                        // si le message est différent de bye la communication peut continuer
                        System.out.print("to client#" + index + "> ");
                        //on récupère le message a envoyer
                        input = keyboard.nextLine();
                        //on crée un cipher en mode cryptage et on crypte le message
                        cipher.init(Cipher.ENCRYPT_MODE, key);
                        byte[] messageByte = cipher.doFinal(input.getBytes("UTF-8"));
                        String messageEncode = Base64.encode(messageByte);
                        System.out.println("Message crypté");

                        // on envoie le message crypté
                        out.println(messageEncode);
                        out.flush();
                        // si le message est bye on coupe la communication
                        if (input.compareToIgnoreCase("bye") == 0) {
                            System.out.println("server shutting down");
                            doRun = false;
                        } else {
                            talk = in.readLine();
                        }
                    }
                }
            
            s.close();
        } catch (Base64DecodingException | IOException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
                System.out.println("raaah! what did u forget this time?");
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ChatServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | NoSuchAlgorithmException | NoSuchPaddingException | Base64DecodingException ex) {
            Logger.getLogger(ChatServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
