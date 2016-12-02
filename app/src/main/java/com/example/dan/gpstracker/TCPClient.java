package com.example.dan.gpstracker;
import android.util.Log;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class TCPClient
{
    private String serverMessage;
    public static final String SERVERIP = "192.168.2.47";   //ip server
    public static final int SERVERPORT = 4444;              //portul pe care comunicam

    PrintWriter out;        //se va ocupa de vrem sa transmitem serverului
    BufferedReader in;      //se va ocupa de ceea ce primeste aplicatia (va arunca toate mesajele... nu ne folosesc)

    public TCPClient() {}   //constructorul clasei (default)

    //functia de trimitere a mesajului
    public void sendMessage(String message)
    {
        if (out != null && !out.checkError())
        {
            out.println(message);
            out.flush();
        }
    }

    //functia care creeaza conexiunea TCP (pe LAN)
    public void run()
    {
        try
        {
            InetAddress serverAddr = InetAddress.getByName(SERVERIP);   //se face conexiunea la ip-ul serverului
            Socket socket = new Socket(serverAddr, SERVERPORT);         //se face conexiunea intre socket-uri (la nivel transport)
            try
            {
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);  //trimite mesaj la server
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));                            //primeste mesaj de la server

                //aruncam toate mesajele ce le primim pe conexiune.
                while (true)
                {
                    serverMessage = in.readLine();
                    serverMessage = null;
                }
            }
            catch (Exception e)
            {
                Log.e("TCP", "S: Error", e);  //scriem in fisierul de log ce tip de eroare avem
            }
            finally {   socket.close(); }     //inchidem socket-ul

        }
        catch (Exception e)
        {
            Log.e("TCP", "C: Error", e);      //scriem in fisierul de log ce tip de eroare avem
        }
    }
}
