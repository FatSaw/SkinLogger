package me.bomb.skinlogger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

final class SkinData {
	private final static PublicKey publicKey;
	private final static AtomicInteger id;
	static {
		PublicKey apublicKey = null;
	    try {
	    	apublicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(new byte[] {48, -126, 2, 34, 48, 13, 6, 9, 42, -122, 72, -122, -9, 13, 1, 1, 1, 5, 0, 3, -126, 2, 15, 0, 48, -126, 2, 10, 2, -126, 2, 1, 0, -54, 80, 120, 7, -87, -71, -105, 62, -29, -62, -73, 5, -49, -95, 93, -3, -7, -33, 82, 23, 47, -105, 27, 19, 74, 126, 100, 32, -81, -10, 104, 97, -75, 11, 121, 12, -53, -123, 99, -121, 9, 113, 23, -96, 69, 81, 41, -33, -58, 19, 108, -64, -8, -31, -35, -104, -83, -112, 28, 68, 0, 86, 54, -26, -20, -28, 36, 84, 112, 31, 5, 64, -10, 103, 112, -115, -85, 33, 92, -126, -19, 71, -93, -86, 116, -75, -41, 21, 92, -108, 60, -123, 17, 44, -28, -106, -88, 99, 65, -75, -67, 60, 16, -34, 95, 50, 113, -68, -120, 82, -32, 16, -110, 1, 51, 27, 61, 6, -15, 119, -77, 127, -59, -122, -31, -14, 60, -88, -107, 77, -103, -105, 99, 69, -104, -31, -100, -104, -86, -6, 10, 71, -27, -29, 105, 52, 79, 62, -16, 108, 13, -82, 22, 35, 107, -75, -49, 21, -52, 120, 121, 104, 117, 61, -27, -100, 44, -43, -65, 35, -104, 4, -75, -53, -24, 34, 96, 7, 43, -114, -59, -57, 9, -14, 25, -41, 124, 95, 114, 99, -127, 116, 105, -30, -12, -13, 66, 115, -1, 127, -124, 4, -47, -68, 68, -18, 1, -54, 27, 65, 127, -125, 114, -16, -44, 58, -78, -43, -84, -115, -9, -108, 34, -15, -5, 109, 79, -8, -52, 38, 28, 96, -22, -72, 90, -78, 39, 92, 122, -110, -14, -86, -18, 14, 98, 37, -20, -2, 87, 92, 103, 26, 110, -20, -48, -78, -45, -33, -2, 29, -126, 68, 5, 41, 63, -13, -27, 29, 119, 12, -106, -9, -80, -114, 97, -108, -24, -57, -61, 43, -23, 98, 125, 39, 13, 99, 8, -49, -59, 26, 56, -100, -92, -58, 21, -105, -1, -47, -86, -121, 10, 31, -60, 2, -60, 50, 44, -86, -50, 33, -124, 68, -46, 80, -58, -35, -68, -63, -121, -54, 32, 32, -62, -64, -70, 11, 0, 124, 22, -6, 51, 121, -86, 10, -94, -102, 76, -64, 31, -3, 80, 35, -121, -111, 69, 38, 120, -46, -5, -74, -60, -7, -74, 68, 87, 74, -80, 107, 29, 66, 18, -125, -49, 36, -67, 95, 33, 8, -50, 75, -52, -29, -45, -50, -96, -114, -111, -111, 10, -44, -78, -9, -26, -45, 61, 55, -45, 71, -12, -119, -7, 85, 1, -28, 109, -120, 120, 34, 45, -112, -47, -23, 37, 125, -78, -126, -120, -59, 103, -39, -7, 22, 120, -5, 71, -42, 103, 56, -49, -117, -14, 124, -120, -116, -5, -73, 105, -29, -17, -75, -54, -41, -105, 34, -31, 31, -8, 111, 57, -14, 14, -116, -4, 78, -106, -91, -66, 59, -108, 48, 72, 83, -8, 56, 71, 50, 44, 5, 32, 13, -43, 32, -55, 109, -6, 103, -103, -127, -37, -50, 110, 41, 111, -30, -126, 28, -39, -80, -28, -34, -67, 85, 6, -39, 12, 3, 34, -45, -101, 33, 94, -94, -15, 16, -79, 21, -77, 118, -96, 49, -14, 61, -93, -71, -113, 91, 83, 104, 19, 114, 86, -33, 2, 3, 1, 0, 1}));
	    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
	    }
	    publicKey = apublicKey;
	    id = new AtomicInteger();
	}
	private final int srcid;
	protected final String value;
	protected final String signature;
	protected final long timestamp;
	protected final UUID uuid;
	protected final String profilename;
	protected final String skinurl;
	protected final String capeurl;
	protected final CapeType capetype;
	private SkinData(int srcid,String value,String signature,long timestamp,UUID uuid,String profilename,String skinurl,String capeurl,CapeType capetype) {
        this.srcid = srcid;
		this.value = value;
        this.signature = signature;
        this.timestamp = timestamp;
        this.uuid = uuid;
        this.profilename = profilename;
        this.skinurl = skinurl;
        this.capeurl = capeurl;
        this.capetype = capetype;
	}
	protected SkinData(String value,String signature) throws IllegalArgumentException,NullPointerException {
		if(value==null||signature==null) throw new NullPointerException();
		boolean valid = false;
		try {
            final Signature sig = Signature.getInstance("SHA1withRSA");
            sig.initVerify(publicKey);
            sig.update(value.getBytes());
            valid = sig.verify(Base64.getDecoder().decode(signature));
        }
		catch (IllegalArgumentException e) {
		}
        catch (NoSuchAlgorithmException e) {
        }
        catch (InvalidKeyException e2) {
        }
        catch (SignatureException e3) {
        }
		if(valid) {
			this.srcid = id.incrementAndGet();
            this.value = value;
            this.signature = signature;
            String decodedValue = new String(Base64.getDecoder().decode(value));
            UUID uuid = null;
            long timestamp = 0L;
            String profilename = null;
            String skinurl = null;
            String capeurl = null;
            CapeType capetype = null;
            
            JsonObject element = new JsonParser().parse(decodedValue).getAsJsonObject();
            JsonObject textures = element.get("textures").getAsJsonObject();
            timestamp = element.get("timestamp").getAsLong();
        	profilename = element.get("profileName").getAsString();
            try {
                String suuid = element.get("profileId").getAsString();
            	StringBuilder sb = new StringBuilder();
                sb.append(suuid.substring(0, 8));
                sb.append('-');
                sb.append(suuid.substring(8, 12));
                sb.append('-');
                sb.append(suuid.substring(12, 16));
                sb.append('-');
                sb.append(suuid.substring(16, 20));
                sb.append('-');
                sb.append(suuid.substring(20, 32));
                suuid = sb.toString();
                try{
                	uuid = UUID.fromString(suuid);
                } catch (IllegalArgumentException e) {
                }
            } catch (IndexOutOfBoundsException e) {
            }
            if(textures.has("SKIN")) {
            	skinurl = textures.get("SKIN").getAsJsonObject().get("url").getAsString();
            }
            if(textures.has("CAPE")) {
				capeurl = textures.get("CAPE").getAsJsonObject().get("url").getAsString();
				capetype = CapeType.detectCape(capeurl);
            }
            
            this.timestamp = timestamp;
            this.uuid = uuid;
            this.profilename = profilename;
            this.skinurl = skinurl;
            if(capetype!=null&&capetype!=CapeType.OTHER) capeurl = null;
            this.capeurl = capeurl;
            this.capetype = capetype;
            
		} else throw new IllegalArgumentException("Signature invalid! " + value + ":" + signature);
	}
	protected final SkinData clone() {
		return new SkinData(srcid, value, signature, timestamp, uuid, profilename, skinurl, capeurl, capetype);
	}
	@Override
	public boolean equals(Object object) {
		if(object==null) return false;
		SkinData data = null;
		return this == object || object instanceof SkinData && (data = (SkinData)object) != null && (data.srcid==this.srcid||(data.value==this.value&&data.signature==this.signature)||(data.value.equals(this.value)&&data.signature.equals(this.signature)));
	}
	protected static SkinData read(File file) throws IllegalArgumentException,NullPointerException,IndexOutOfBoundsException,IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String value = reader.readLine();
		String signature = reader.readLine();
		reader.close();
		return new SkinData(value,signature);
	}
	protected void write(File file) throws IOException {
		if(file==null) {
			return;
		}
		StringBuffer sb = new StringBuffer();
		sb.append(value);
		sb.append("\n");
		sb.append(signature);
		FileOutputStream fos = new FileOutputStream(file,false);
		fos.write(sb.toString().getBytes());
		fos.flush();
		fos.close();
	}
}
