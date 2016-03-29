package co.id.j4u.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class DEncrypt {
	
	/**
	 * 
	 * @param password = password untuk generate key
	 * @return key = generated key 
	 */
	public static SecretKey generateKey(String algorithm, String password) throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
		SecretKey key = null;
		// generate random key
		if (algorithm.equals(DConstants.ALGORITHM.AES)) {
			key = getSecretKeyGenerator(algorithm);
		} else {
			key = getSecretKeyFactory(algorithm, password);
		}

		// tulis key ke file untuk keperluan dekripsi
        File dir = new File(DConstants.DIR.CONFIG);
        if (!dir.exists())
        	dir.mkdirs();
		File file = new File(dir.getAbsolutePath() + "/" + DConstants.FILE.KEY);
		if (!file.exists())
			file.createNewFile();
		DFile.doWriteFile(file, Base64.encodeBase64String(key.getEncoded()).getBytes());
		
		return key;
	}
	
	/**
	 * @param cipher = cipher yg dipakai encrypt
	 */
	public static void generateIV(Cipher cipher) throws GeneralSecurityException, IOException {
        byte[] iv = cipher.getParameters().getParameterSpec(IvParameterSpec.class).getIV();
        File file = new File(DConstants.DIR.CONFIG + DConstants.FILE.IV);
		if (!file.exists())
			file.createNewFile();
        DFile.doWriteFile(file, Base64.encodeBase64String(iv).getBytes());
    }
	
	/**
	 * @param srcFile = File yg akan diencrypt
	 * @param outFile = File output decrypt srcFile
	 * @param cipher = cipher yg sudah install iv & key
	 */
	public static void doEncryptFile(File srcFile, File outFile, Cipher cipher) throws GeneralSecurityException, IOException {
		// Output file encrypt
		CipherOutputStream writer = new CipherOutputStream(new FileOutputStream(outFile), cipher);
		
		// Encrypt isi file
		FileReader reader = new FileReader(srcFile);
		int data;
		while ((data = reader.read()) != -1) {
			System.out.println((char) data);
			writer.write(data);
		}
		reader.close();
		writer.close();
	}
	
	/**
	 * Dipakai untuk enrypt 
	 */
	public static Cipher createCipherEncrypt(String algorithmEncrypt, SecretKey key) throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException {
		// inisialisasi Cipher
		Cipher cipher = Cipher.getInstance(algorithmEncrypt);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        
        return cipher;
	}
	
	/**
	 * Dipakai untuk decrypt 
	 */
	public static Cipher createCipherDecrypt(String algorithmKey, String algorithmEncrypt, String key, String iv) throws GeneralSecurityException, UnsupportedEncodingException, NoSuchAlgorithmException {
		// inisialisasi Spec utk Cipher
		SecretKeySpec keySpec = new SecretKeySpec(Base64.decodeBase64(key), algorithmKey);
		IvParameterSpec ivSpec = new IvParameterSpec(Base64.decodeBase64(iv));
		
		// inisialisasi Cipher
		Cipher cipher = Cipher.getInstance(algorithmEncrypt);
		cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
	
		return cipher;
	}
	
	public static Cipher createCipherDecrypt(String algorithmEncrypt, SecretKeySpec keySpec, IvParameterSpec ivSpec) throws GeneralSecurityException, UnsupportedEncodingException, NoSuchAlgorithmException {
		// inisialisasi Cipher
		Cipher cipher = Cipher.getInstance(algorithmEncrypt);
		cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
	
		return cipher;
	}
	
	/**
	 * @param srcFile = File yg akan diencrypt
	 * @param outFile = File output decrypt srcFile
	 * @param cipher = cipher yg sudah install iv & key
	 */
	public static void doDecryptFile (File scrFile, File outFile, Cipher cipher) throws IOException {
		// output hasil dekripsi
		FileWriter writer = new FileWriter(outFile);

		// membaca dan dekrip isi file
		CipherInputStream input = new CipherInputStream(new FileInputStream(scrFile), cipher);
		int data;
		while ((data = input.read()) != -1) {
			writer.write(data);
		}
		input.close();
		writer.close();
	}
	
	/**
	 * Simple keyGenerator only using Algorithm 
	 */
	public static SecretKey getSecretKeyGenerator(String algorithm) throws NoSuchAlgorithmException {
		KeyGenerator keygen = KeyGenerator.getInstance(algorithm);
		keygen.init(128);
		return keygen.generateKey();
	}
	
	/**
	 * Keygenerator (Tidak bisa AES)
	 * @param algorithm
	 * @param password
	 */
	public static SecretKey getSecretKeyFactory(String algorithm, String password) throws InvalidKeySpecException, NoSuchAlgorithmException {
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(algorithm);
        SecretKey key = keyFactory.generateSecret(new PBEKeySpec(password.toCharArray()));
        System.out.println(key.getEncoded());
        return key;
	}
	
	/**
	 * Get Key from generated File 
	 */
	public static SecretKeySpec getKeyFromFile(File file, String algorithm) throws IOException {
		List<String> keys = Files.readAllLines(file.toPath(),Charset.forName("UTF-8"));
		String key = keys.get(0);
		SecretKeySpec keySpec = new SecretKeySpec(Base64.decodeBase64(key), algorithm);
		
		return keySpec;
	}
	
	/**
	 * Get IV from generated File 
	 */
	public static IvParameterSpec getIVFromFile(File file) throws IOException {
		List<String> ivs = Files.readAllLines(file.toPath(),Charset.forName("UTF-8"));
		String iv = ivs.get(0);
		IvParameterSpec ivSpec = new IvParameterSpec(Base64.decodeBase64(iv));
		
		return ivSpec;
	}
	
	/**
	 * Proses encrypt per text
	 */
	public static byte[] encrypt(String algorithm, SecretKeySpec key, byte[] data) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        // method doFinal untuk decrypt ato encrypt
        byte[] encrypted = cipher.doFinal(data);
        return encrypted;
    }
	
	/**
	 * Proses encrypt per text
	 */
	public static byte[] encrypt(String algorithm, SecretKeySpec key, IvParameterSpec iv, byte[] data) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        // method doFinal untuk decrypt ato encrypt
        byte[] encrypted = cipher.doFinal(data);
        return encrypted;
    }
	
	public static byte[] encrypt(Cipher cipher, byte[] data) throws GeneralSecurityException {
        // method doFinal untuk decrypt ato encrypt
        byte[] encrypted = cipher.doFinal(data);
        return encrypted;
    }

	/**
	 * Proses decrypt per text
	 */
    public static byte[] decrypt(String algorithm, SecretKey key, byte[] data) throws GeneralSecurityException {
    	Cipher cipher = Cipher.getInstance(algorithm);
    	cipher.init(Cipher.DECRYPT_MODE, key);
        // method doFinal untuk decrypt ato encrypt
    	byte[] decrypted = cipher.doFinal(data);
        return decrypted;
    }
    
	/**
	 * Proses decrypt per text
	 */
    public static byte[] decrypt(String algorithm, SecretKey key, IvParameterSpec iv, byte[] data) throws GeneralSecurityException, IOException {
    	Cipher cipher = Cipher.getInstance(algorithm);
    	cipher.init(Cipher.DECRYPT_MODE, key, iv);
        // method doFinal untuk decrypt ato encrypt
    	byte[] decrypted = cipher.doFinal(data);
        return decrypted;
    }
    
    public static byte[] decrypt(Cipher cipher, byte[] data) throws GeneralSecurityException {
        // method doFinal untuk decrypt ato encrypt
    	byte[] decrypted = cipher.doFinal(data);
        return decrypted;
    }
    
    private static void showEncode(byte[] b) {
		System.out.println("Generated Key : " + Base64.encodeBase64(b));
	}

}
