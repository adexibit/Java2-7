

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * @author ilnaz-92@yandex.ru
 * Created on 2019-04-30
 */
public class ClientHandler extends Thread {
    String nick;
    private ServerCore serverCore;
    private DataInputStream dis;
    private DataOutputStream dos;
    private static int clientsCount = 0;

    public ClientHandler(Socket socket, ServerCore server) {
        try {
            clientsCount++;
            serverCore = server;
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();
            dis = new DataInputStream(inputStream);
            dos = new DataOutputStream(outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        try {
            serverCore.sendMessageToAllClients("We have a new clients in our chat!");
            serverCore.sendMessageToAllClients("Clients count in chat = " + clientsCount);
            while (true) {
                String message = dis.readUTF();
                if (message.equalsIgnoreCase("EXIT")) {
                    break;
                }
                if (message.charAt(0) == '/' && message.charAt(1) == 'n' && message.charAt(2) == ' ') {
                    char[] nickofchars = new char[message.length() - 2];
                    for (int i = 3; i < message.length(); i++) {
                        nickofchars[i - 3] = message.charAt(i);
                    }
                    nick = new String(nickofchars);
                }
                if (message.charAt(0) == '/' && message.charAt(1) == 'w' && message.charAt(2) == ' ' && message.charAt(3) != ' ') {
                    char[] recieverNickOfChars = new char[message.length() - 3];
                    char[] messageOfChsrs = new char[message.length() - 4];
                    int m = 0;

                    for (int i = 3; i < message.length(); i++) {
                        recieverNickOfChars[m] = message.charAt(i);
                        m++;
                        if (message.charAt(i) == ' ') {
                            int k = 0;
                            for (int j = i; j < message.length(); j++) {

                                messageOfChsrs[k] = message.charAt(j);
                                k++;
                            }
                            break;
                        }

                    }
                    String recieverNick = new String(recieverNickOfChars);
                    String privMessage = new String(messageOfChsrs);
                    serverCore.sendPrivateMessage(recieverNick, privMessage);
                }


                System.out.println(message);
                serverCore.sendMessageToAllClients(message);
                Thread.sleep(100);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close();
        }


    }


    private void close() {
        serverCore.removeClient(this);
        clientsCount--;
        serverCore.sendMessageToAllClients("Clients count in chat = " + clientsCount);
    }

    public void sendMessage(String message) {
        try {
            dos.writeUTF(message);
            dos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
