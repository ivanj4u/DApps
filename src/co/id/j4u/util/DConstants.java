package co.id.j4u.util;

public class DConstants {
	
	public interface APP {
		public String KEY = "53agjjxvhs6oiky";
		public String SECRET = "lpv636aa7pmrck6";
		public String TOKEN = "ou3dGNYdOK8AAAAAAAAAfsqzGLcNkUfZ0JM3Qjl3QHohsTY1KiX2gIiP7f-PeNKd";
	}
	
	public interface ALGORITHM {
		public String AES = "AES";
		public String PBE = "PBE";
		public String MD5 = "MD5";
		public String AES_CBC_NOPAD = "AES/CBC/NoPadding";
		public String AES_CBC_PKCS5PAD = "AES/CBC/PKCS5Padding";
		public String PBKDF2_HMACSHA1 = "PBKDF2WithHmacSHA1";
		public String PBE_MD5_DES = "PBEWithMD5AndDES";
	}

	public interface DIR {
		public String DOWNLOAD = "D:/Test/Drop/download/";
		public String UPLOAD = "D:/Test/Drop/upload/";
		public String CONFIG = "D:/Test/Drop/config/";
	}
	
	public interface FILE {
		public String USER = "user.properties";
		public String APP = "app.properties";
		public String TRX = "trx.properties";
		public String KEY = "key";
		public String IV = "iv";
	}
	
	public interface CONFIG {
		public char[] PASS = {'i','v','a','n'};
	}
}
