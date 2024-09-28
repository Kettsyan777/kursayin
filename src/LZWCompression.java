import java.util.*;

public class LZWCompression {

    private static final int MAX_TABLE_SIZE = 4096;

    public static byte[] compress(byte[] data) {
        Map<String, Integer> dictionary = new HashMap<>();
        for (int i = 0; i < 256; i++) {
            dictionary.put("" + (char) i, i);
        }
        int tableSize = 256;
        List<Integer> compressed = new ArrayList<>();
        String current = "";
        for (byte b : data) {
            String next = current + (char) (b & 0xFF);
            if (dictionary.containsKey(next)) {
                current = next;
            } else {
                compressed.add(dictionary.get(current));
                if (tableSize < MAX_TABLE_SIZE) {
                    dictionary.put(next, tableSize++);
                }
                current = "" + (char) (b & 0xFF);
            }
        }
        if (!current.equals("")) {
            compressed.add(dictionary.get(current));
        }
        byte[] output = new byte[compressed.size() * 2];
        for (int i = 0; i < compressed.size(); i++) {
            output[i * 2] = (byte) (compressed.get(i) >> 8);
            output[i * 2 + 1] = (byte) compressed.get(i).intValue();
        }
        return output;
    }

    public static byte[] decompress(byte[] data) {
        Map<Integer, String> dictionary = new HashMap<>();
        for (int i = 0; i < 256; i++) {
            dictionary.put(i, "" + (char) i);
        }
        int tableSize = 256;
        List<Integer> compressed = new ArrayList<>();
        for (int i = 0; i < data.length; i += 2) {
            compressed.add(((data[i] & 0xFF) << 8) | (data[i + 1] & 0xFF));
        }
        String current = "" + (char) (int) compressed.remove(0);
        StringBuilder output = new StringBuilder(current);
        for (int code : compressed) {
            String entry;
            if (dictionary.containsKey(code)) {
                entry = dictionary.get(code);
            } else if (code == tableSize) {
                entry = current + current.charAt(0);
            } else {
                throw new IllegalArgumentException("Invalid compressed code: " + code);
            }
            output.append(entry);
            if (tableSize < MAX_TABLE_SIZE) {
                dictionary.put(tableSize++, current + entry.charAt(0));
            }
            current = entry;
        }
        byte[] outputBytes = new byte[output.length()];
        for (int i = 0; i < output.length(); i++) {
            outputBytes[i] = (byte) output.charAt(i);
        }
        return outputBytes;
    }
}
