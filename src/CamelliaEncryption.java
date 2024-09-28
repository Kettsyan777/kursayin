import org.bouncycastle.crypto.engines.CamelliaEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

import java.security.SecureRandom;

public class CamelliaEncryption {

    private static final int KEY_SIZE = 256;
    private static final int BLOCK_SIZE = 128;
    private static final int IV_SIZE = BLOCK_SIZE / 8;

    public static byte[] encrypt(byte[] data, byte[] key) throws Exception {
        byte[] iv = generateIV();
        PaddedBufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new CamelliaEngine()));
        KeyParameter keyParam = new KeyParameter(key);
        cipher.init(true, new ParametersWithIV(keyParam, iv));
        byte[] output = new byte[cipher.getOutputSize(data.length)];
        int length = cipher.processBytes(data, 0, data.length, output, 0);
        cipher.doFinal(output, length);
        byte[] encrypted = new byte[output.length + IV_SIZE];
        System.arraycopy(iv, 0, encrypted, 0, IV_SIZE);
        System.arraycopy(output, 0, encrypted, IV_SIZE, output.length);
        return encrypted;
    }

    public static byte[] decrypt(byte[] data, byte[] key) throws Exception {
        byte[] iv = new byte[IV_SIZE];
        System.arraycopy(data, 0, iv, 0, IV_SIZE);
        PaddedBufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new CamelliaEngine()));
        KeyParameter keyParam = new KeyParameter(key);
        cipher.init(false, new ParametersWithIV(keyParam, iv));
        byte[] output = new byte[cipher.getOutputSize(data.length - IV_SIZE)];
        int length = cipher.processBytes(data, IV_SIZE, data.length - IV_SIZE, output, 0);
        cipher.doFinal(output, length);
        return output;
    }

    private static byte[] generateIV() {
        byte[] iv = new byte[IV_SIZE];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        return iv;
    }
}
