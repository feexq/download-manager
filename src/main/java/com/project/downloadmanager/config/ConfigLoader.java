package com.project.downloadmanager.config;

import java.io.*;
import java.util.Properties;

public class ConfigLoader {

    private static final String CONFIG_FILE = "src/main/resources/config.properties";
    private static Properties properties = new Properties();

    static {
        loadConfig();
    }

    private static void loadConfig() {
        try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
            properties.load(fis);
        } catch (IOException e) {
            properties.setProperty("download.directory", System.getProperty("user.home") + "/Downloads");
            properties.setProperty("download.maxSpeed", "1048576");
            properties.setProperty("p2p.directory", System.getProperty("user.home") + "/P2PShared");
            properties.setProperty("p2p.port", "5000");
            properties.setProperty("p2p.serverAddress", "localhost");
            properties.setProperty("p2p.central.port", "5000");
        }
    }

    public static void saveConfig(String downloadDirectory, int maxSpeed,
            String p2pDirectory, int p2pPort,
            String p2pServerAddress, int p2pServerPort) {
        properties.setProperty("download.directory", downloadDirectory);
        properties.setProperty("download.maxSpeed", String.valueOf(maxSpeed));
        properties.setProperty("p2p.directory", p2pDirectory);
        properties.setProperty("p2p.port", String.valueOf(p2pPort));
        properties.setProperty("p2p.serverAddress", p2pServerAddress);
        properties.setProperty("p2p.central.port", String.valueOf(p2pServerPort));

        try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE)) {
            properties.store(fos, "Download Manager Configuration");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getP2PDirectory() {
        return properties.getProperty("p2p.directory");
    }

    public static int getP2PPort() {
        return Integer.parseInt(properties.getProperty("p2p.port"));
    }

    public static String getP2PServerAddress() {
        return properties.getProperty("p2p.serverAddress");
    }

    public static String getDownloadDirectory() {
        return properties.getProperty("download.directory");
    }

    public static int getMaxDownloadSpeed() {
        return Integer.parseInt(properties.getProperty("download.maxSpeed"));
    }

    public static int getCentralPort() {
        return Integer.parseInt(properties.getProperty("p2p.central.port"));
    }
}