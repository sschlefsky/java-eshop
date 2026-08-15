package net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import common.IEShop;
import domain.EShop;

public class EShopServer {
    public static void main(String[] args) throws IOException {
        IEShop eShop = new EShop();

        ServerSocket ss = new ServerSocket(33333);
        System.out.println("Server läuft und wartet auf eingehende Verbindungen!");

        while(true) {
            Socket s = ss.accept();

            ClientRequestProcessor c = new ClientRequestProcessor(s, eShop);

            Thread t = new Thread(c);
            t.start();

            System.err.println("Client verbunden!");
        }
    }
}