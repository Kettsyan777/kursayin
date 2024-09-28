import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Client{
    private static final String SECRET_KEY = "mysecretpassword";
    private static final int BUFFER_SIZE = 1024;

    public static void main(String[] args) throws Exception {
        String fileName = "test.txt";
        Socket socket = new Socket("localhost", 8080);
        System.out.println("Connected to server.");

        OutputStream outputStream = socket.getOutputStream();
        byte[] buffer = fileName.getBytes();
        outputStream.write(buffer, 0, buffer.length);
        System.out.println("Sent file name: " + fileName);

        InputStream inputStream = socket.getInputStream();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] compressedBuffer = new byte[BUFFER_SIZE];
        int bytesRead;
        while ((bytesRead = inputStream.read(compressedBuffer)) != -1) {
            byteArrayOutputStream.write(compressedBuffer, 0, bytesRead);
        }
        byte[] compressedBytes = byteArrayOutputStream.toByteArray();
        System.out.println("Received compressed data.");

        byte[] encryptedBytes = LZWCompression.decompress(compressedBytes);
        System.out.println("Data decompressed.");

        byte[] decryptedBytes = CamelliaEncryption.decrypt(encryptedBytes, SECRET_KEY.getBytes());
        System.out.println("Data decrypted");

        Files.write(Paths.get("decrypted_" + fileName), decryptedBytes);
        System.out.println("Data written to disk.");

        socket.close();
    }
}
