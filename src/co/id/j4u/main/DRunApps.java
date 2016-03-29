package co.id.j4u.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Locale;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

import co.id.j4u.util.DConstants;
import co.id.j4u.util.DEncrypt;
import co.id.j4u.util.DFile;

import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxEntry;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxWebAuthNoRedirect;
import com.dropbox.core.DbxWriteMode;

/**
 * Aplikasi connect ke Dropbox
 * @author ivan_j4u
 */
public class DRunApps {

	public static void main(String[] args) {
		File file = null;
		FileInputStream in = null;
		FileOutputStream out = null;
		DbxClient client = null;
		Cipher cipher = null;
		String password = "jau";
		String text = "aku lapar sekali";
		try {
//			DbxRequestConfig config = new DbxRequestConfig("JavaTutorial/1.0", Locale.getDefault().toString());
//			client = getClient(config, DConstants.APP.TOKEN);
//			doUploadFile(file, in, "read.txt", client, false);
//			doDownloadFile(out, "read.txt", client);
			
			DEncrypt.generateKey(DConstants.ALGORITHM.AES, password);
			file = new File(DConstants.DIR.CONFIG + DConstants.FILE.KEY);
			SecretKeySpec keySpec = DEncrypt.getKeyFromFile(file, DConstants.ALGORITHM.AES);
			System.out.println("Key : " + new String(Base64.encodeBase64(keySpec.getEncoded())));
			cipher = DEncrypt.createCipherEncrypt(DConstants.ALGORITHM.AES_CBC_PKCS5PAD, keySpec);
			DEncrypt.generateIV(cipher);
			
			file = new File(DConstants.DIR.CONFIG + DConstants.FILE.IV);
			IvParameterSpec ivSpec = DEncrypt.getIVFromFile(file);
			System.out.println("IV : " + new String(Base64.encodeBase64(ivSpec.getIV())));
			
			byte[] rawByte = text.getBytes();
			byte[] data = Base64.encodeBase64(rawByte);
			byte[] encrypt = DEncrypt.encrypt(DConstants.ALGORITHM.AES_CBC_PKCS5PAD, keySpec, ivSpec, data);
			System.out.println("Text Encrypt : " + new String(Base64.encodeBase64(encrypt)));
			byte[] decrypt = DEncrypt.decrypt(DConstants.ALGORITHM.AES_CBC_PKCS5PAD, keySpec, ivSpec, encrypt);
			rawByte = Base64.decodeBase64(decrypt);
			text = new String(rawByte);
			System.out.println("Text Decrypt : " + text);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static DbxClient getClient(DbxRequestConfig config, String token) {
		return new DbxClient(config, DConstants.APP.TOKEN);
	}
	
	private static void doListeningFolder(DbxClient client) throws Exception {
		DbxEntry.WithChildren list = client.getMetadataWithChildren("/");
		System.out.println("Files in the root path :");
		for (DbxEntry child : list.children) {
			System.out.println("  " + child.name + " : " + child.toString());
		}
	}
	
	private static void doUploadFile(File file, FileInputStream in, String fileName, DbxClient client, boolean isUpdate) throws IOException, DbxException {
		File dir = new File(DConstants.DIR.UPLOAD);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		file = new File(dir.getAbsolutePath() + "/" + fileName);
		in = new FileInputStream(file);
		// Uploading File
		DbxEntry.File uploadFile = client.uploadFile(DConstants.DIR.CONFIG + fileName, 
				(isUpdate ? DbxWriteMode.update(null) : DbxWriteMode.add()),
				file.length(), in);
		System.out.println("Data Uploaded" + uploadFile.toString());
		if (in != null)
			in.close();
	}

	private static void doDownloadFile(FileOutputStream out, String fileName, DbxClient client) throws IOException, DbxException {
		File dir = new File(DConstants.DIR.DOWNLOAD);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		out = new FileOutputStream(dir.getAbsolutePath() + "/" + fileName);
		// Downloading file
		DbxEntry.File downloadFile = client.getFile(DConstants.DIR.CONFIG + fileName, null, out);
		System.out.println("Data Downloaded" + downloadFile.toString());
		if (out != null)
			out.close();
	}
	
	private static void doReadWrite(FileInputStream in, FileOutputStream out, String fileName) throws IOException {
		File fileDownload = new File(DConstants.DIR.DOWNLOAD + fileName);
		File fileUpload = new File(DConstants.DIR.UPLOAD + fileName);
		if (!fileUpload.exists())
			fileUpload.createNewFile();
		in = new FileInputStream(fileDownload);
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		out = new FileOutputStream(fileUpload);
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
		String s;
		StringBuffer sb = new StringBuffer();
		while ((s = reader.readLine())!= null) {
			System.out.println("Reading..." + s);
			sb.append(s);
		}
		writer.write(sb.toString());
		writer.flush();
		
		System.out.println("Writing...");
		if (reader != null)
			reader.close();
		if (writer != null)
			writer.close();
		if (in != null)
			in.close();
		if (out != null)
			out.close();
		System.out.println("File Done...");
	}
	
	/**
	 * Testing method DropBox
	 */
	private static void testing() throws IOException, DbxException {
		DbxAppInfo appInfo = new DbxAppInfo(DConstants.APP.KEY, DConstants.APP.SECRET);
		DbxRequestConfig config = new DbxRequestConfig("JavaTutorial/1.0", Locale.getDefault().toString());
		
		// Authorize by web
		DbxWebAuthNoRedirect webAuth = new DbxWebAuthNoRedirect(config, appInfo);
		String authorizeUrl = webAuth.start();
		// Have the user sign in and authorize this token
		System.out.println("1. Go to this URL : " + authorizeUrl);
		System.out.println("2. Click \"Allow\" (you might have to log in first)");
		System.out.println("3. Copy the authorization code and paste here ");
		String code = new BufferedReader(new InputStreamReader(System.in)).readLine().trim();
		System.out.println("4. Authorize Code : " + code);
		
		// Exchange code with access token (jika token belum digenerate)
		DbxAuthFinish authFinish = webAuth.finish(code);
		String accessToken = authFinish.accessToken;
		System.out.println("5. Authorize Token : " + accessToken);
		
		// User Account (digunakan untuk upload dan download)
		DbxClient client = new DbxClient(config, DConstants.APP.TOKEN);
		System.out.println("Linked Account : " + client.getAccountInfo().displayName);
	}
	
}
