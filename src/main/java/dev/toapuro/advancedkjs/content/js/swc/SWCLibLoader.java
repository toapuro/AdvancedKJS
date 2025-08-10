package dev.toapuro.advancedkjs.content.js.swc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;

public class SWCLibLoader {
    private static final String LIB_DOWNLOAD_BASE_URL = "https://github.com/swc-project/swc/releases/latest/download/";
    private static final Logger LOGGER = LoggerFactory.getLogger(SWCLibLoader.class);

    public SWCLibLoader() {
    }

    public static String getSwcExecutableName() {
        String os = detectOS();
        String arch = detectArch();
        String libc = "";

        if (os.equals("linux")) {
            libc = detectLibc();
            return String.format("swc-linux-%s-%s", arch, libc);
        } else if (os.equals("darwin")) {
            return String.format("swc-darwin-%s", arch);
        } else if (os.equals("win32")) {
            return String.format("swc-win32-%s-msvc.exe", arch);
        } else {
            throw new UnsupportedOperationException("Unsupported OS: " + os);
        }
    }

    private static String detectOS() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win")) return "win32";
        if (osName.contains("mac") || osName.contains("darwin")) return "darwin";
        if (osName.contains("nux") || osName.contains("linux")) return "linux";
        throw new UnsupportedOperationException("Unknown OS: " + osName);
    }

    private static String detectArch() {
        String arch = System.getProperty("os.arch").toLowerCase();
        if (arch.equals("amd64") || arch.equals("x86_64")) return "x64";
        if (arch.equals("aarch64") || arch.equals("arm64")) return "arm64";
        if (arch.equals("x86") || arch.equals("i386")) return "ia32";
        if (arch.startsWith("arm")) return "arm-gnueabihf";
        throw new UnsupportedOperationException("Unknown architecture: " + arch);
    }

    private static String detectLibc() {
        try {
            Process proc = new ProcessBuilder("ldd", "--version")
                    .redirectErrorStream(true)
                    .start();
            try (java.io.BufferedReader reader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(proc.getInputStream()))) {
                String firstLine = reader.readLine();
                if (firstLine != null && firstLine.toLowerCase().contains("musl")) {
                    return "musl";
                }
            }
        } catch (Exception ignored) {
        }
        return "gnu";
    }

    public static Path downloadSwcFile() {
        String binaryName = getSwcExecutableName();

        URL libDownloadUrl = null;
        try {
            libDownloadUrl = new URL(LIB_DOWNLOAD_BASE_URL + binaryName);
            ReadableByteChannel readableByteChannel = Channels.newChannel(libDownloadUrl.openStream());

            File file = File.createTempFile(binaryName, "");
            try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                LOGGER.info("Downloading swc file from {} to {}", libDownloadUrl, file);
                fileOutputStream.getChannel()
                        .transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
            }

            return file.toPath();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
