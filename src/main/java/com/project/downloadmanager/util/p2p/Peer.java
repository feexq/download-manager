package com.project.downloadmanager.util.p2p;

import com.project.downloadmanager.config.ConfigLoader;
import com.project.downloadmanager.model.entity.PeerFileInfo;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Peer {

    static String shareDirectory = ConfigLoader.getP2PDirectory();
    static int peerPort = ConfigLoader.getP2PPort();
    static String serverAddress = ConfigLoader.getP2PServerAddress();
    static int port = ConfigLoader.getCentralPort();
    private static volatile boolean isRunning = true;

    public Peer () {
        try {
            runAsPeer();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void runAsPeer() throws IOException {
        File directory = new File(shareDirectory);
        if (!directory.exists() || !directory.isDirectory()) {
            System.err.println("Invalid directory. Exiting.");
            return;
        }

        if (!isPeerServerRunning(peerPort)) {
            try {
                startPeerServer(peerPort, shareDirectory);
            } catch (Exception e) {
                System.err.println("Server already started or error: " + e.getMessage());
            }
        }

        List<String> filesToShare = Arrays.asList(Objects.requireNonNull(directory.list()));
        try (Socket socket = new Socket(serverAddress, port);
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream())) {

            objectOutputStream.writeObject("REGISTER");
            objectOutputStream.writeObject(filesToShare);
            objectOutputStream.writeInt(peerPort);
            objectOutputStream.flush();

            System.out.println("Files registered: " + filesToShare);
        } catch (Exception e) {
            throw new IOException();
        }
    }

    public static List<PeerFileInfo> getAllFiles() throws IOException{
        try (Socket socket = new Socket(serverAddress, port);
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream())) {

            objectOutputStream.writeObject("FILES");
            objectOutputStream.writeInt(peerPort);
            objectOutputStream.flush();

            @SuppressWarnings("unchecked")
            List<PeerFileInfo> peers = (List<PeerFileInfo>) objectInputStream.readObject();

            if (peers.isEmpty()) {
                System.out.println("No files found.");
            }
            return peers;
        } catch (Exception e) {
            System.err.println("Error during search files: " + e.getMessage());
            throw new IOException(e);
        }
    }

    public static List<PeerFileInfo> searchFileByName(String fileName) throws IOException {
        try (Socket socket = new Socket(serverAddress, port);
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream())) {

            objectOutputStream.writeObject("SEARCH");
            objectOutputStream.writeObject(fileName);
            objectOutputStream.writeInt(peerPort);
            objectOutputStream.flush();

            @SuppressWarnings("unchecked")
            List<PeerFileInfo> peers = (List<PeerFileInfo>) objectInputStream.readObject();

            if (peers.isEmpty()) {
                System.out.println("No peers have the requested file.");
                throw new IOException("No peers have the requested file.");
            }
            return peers;
        } catch (Exception e) {
            System.err.println("Error during search or download: " + e.getMessage());
            throw new IOException(e);
        }
    }

    private static boolean isPeerServerRunning(int port) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress("localhost", port), 1000); // 1000 ms timeout
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static void downloadFileFromPeer(String peerAddress, int peerPort, String fileName) {
        try (Socket socket = new Socket(peerAddress.split(":")[0], peerPort);
             DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
             InputStream inputStream = socket.getInputStream()) {

            dataOutputStream.writeUTF(fileName);
            dataOutputStream.flush();

            File file = new File(shareDirectory, fileName);
            try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                System.out.println("Downloading file...");
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, bytesRead);
                }
                System.out.println("File downloaded successfully and saved to: " + file.getAbsolutePath());
            }
        } catch (IOException e) {
            System.err.println("Error during file download: " + e.getMessage());
        }
    }

    private static void startPeerServer(int port, String shareDirectory) {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                System.out.println("Peer server is listening on port " + port);

                while (isRunning) {
                    try (Socket socket = serverSocket.accept();
                         DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                         OutputStream outputStream = socket.getOutputStream()) {

                        String requestedFile = dataInputStream.readUTF();
                        File file = new File(shareDirectory, requestedFile);

                        if (file.exists()) {
                            try (FileInputStream fileInputStream = new FileInputStream(file)) {
                                System.out.println("Uploading file: " + requestedFile);
                                byte[] buffer = new byte[4096];
                                int bytesRead;
                                while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                                    outputStream.write(buffer, 0, bytesRead);
                                }
                            }
                            System.out.println("File uploaded successfully.");
                        } else {
                            System.err.println("Requested file not found: " + requestedFile);
                        }
                    } catch (IOException e) {
                        System.err.println("Error handling file request: " + e.getMessage());
                    }
                }
            } catch (IOException e) {
                System.err.println("Peer server error: " + e.getMessage());
            }
        }).start();
    }

    public static void stopPeerServer() {
        isRunning = false;
        System.out.println("Peer server stopped.");
    }

}
