package com.project.downloadmanager.util.p2p;

import com.project.downloadmanager.model.entity.PeerFileInfo;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CentralServer {

    private static final Map<String, Set<PeerFileInfo>> connectedPeers = new ConcurrentHashMap<>();
    private static final int port = 5000;

    public CentralServer() {
        try {
            runAsServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void runAsServer() throws IOException {
        Map<String, List<PeerFileInfo>> fileRegistry = new ConcurrentHashMap<>();

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening on port " + port);
            startPeerCleanupThread(fileRegistry);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Peer connected: " + socket.getInetAddress() + ":" + socket.getPort());

                new Thread(() -> handlePeer(socket, fileRegistry)).start();
            }
        }
    }

    private static void startPeerCleanupThread(Map<String, List<PeerFileInfo>> fileRegistry) {
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(5 * 60 * 1000);

                    synchronized (fileRegistry) {
                        for (String file : fileRegistry.keySet()) {
                            fileRegistry.get(file).removeIf(peerInfo ->
                                    !isPeerConnected(peerInfo.getAddress(), peerInfo.getPort()));
                        }

                        fileRegistry.entrySet().removeIf(entry -> entry.getValue().isEmpty());
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }).start();
    }

    private static boolean isPeerConnected(String address, int port) {
        try (Socket socket = new Socket(address.split(":")[0], port)) {
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private static void handlePeer(Socket socket, Map<String, List<PeerFileInfo>> fileRegistry) {
        String peerAddress = null;
        try (InputStream inputStream = socket.getInputStream();
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)) {

            String command = (String) objectInputStream.readObject();

            if ("REGISTER".equals(command)) {
                @SuppressWarnings("unchecked")
                List<String> files = (List<String>) objectInputStream.readObject();
                int peerPort = objectInputStream.readInt();
                peerAddress = socket.getInetAddress().getHostAddress() + ":" + peerPort;

                for (String file : files) {
                    PeerFileInfo peerInfo = new PeerFileInfo(peerAddress, peerPort);
                    fileRegistry.computeIfAbsent(file, k -> new ArrayList<>()).add(peerInfo);
                }
                System.out.println("Registered files from peer " + peerAddress + ": " + files);

                String finalPeerAddress = peerAddress;
                connectedPeers.computeIfAbsent(peerAddress, k -> new HashSet<>()).addAll(
                        files.stream().map(file -> new PeerFileInfo(finalPeerAddress, peerPort)).toList()
                );
            } else if ("SEARCH".equals(command)) {
                String fileName = (String) objectInputStream.readObject();
                String requesterAddress = socket.getInetAddress().getHostAddress() + ":" +
                        objectInputStream.readInt();

                List<PeerFileInfo> peers = fileRegistry.getOrDefault(fileName, Collections.emptyList())
                        .stream()
                        .filter(p -> !p.getAddress().equals(requesterAddress))
                        .toList();

                objectOutputStream.writeObject(peers);
                objectOutputStream.flush();
                System.out.println("Search request for file '" + fileName + "' returned peers: " + peers);
            } else if ("FILES".equals(command)) {
                String requesterAddress = socket.getInetAddress().getHostAddress() + ":" +
                        objectInputStream.readInt();
                List<PeerFileInfo> allFiles = new ArrayList<>();
                fileRegistry.values().forEach(allFiles::addAll);

                List<PeerFileInfo> returnedFiles = new ArrayList<>();

                for (PeerFileInfo peerInfo : allFiles.stream().filter(p -> !p.getAddress().equals(requesterAddress)).toList()) {
                    for (Map.Entry<String, List<PeerFileInfo>> entry : fileRegistry.entrySet()) {
                        if (entry.getValue().contains(peerInfo)) {
                            if (!returnedFiles.contains(new PeerFileInfo(entry.getKey(), peerInfo.getAddress(), peerInfo.getPort()))) {
                                returnedFiles.add(new PeerFileInfo(entry.getKey(), peerInfo.getAddress(), peerInfo.getPort()));
                            }
                        }
                    }
                }

                objectOutputStream.writeObject(returnedFiles);
                objectOutputStream.flush();
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error handling peer: " + e.getMessage());
        } finally {
            if (peerAddress != null) {
                connectedPeers.remove(peerAddress);
            }
        }
    }
}
