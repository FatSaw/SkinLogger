package me.bomb.skinlogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.UUID;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonStreamParser;

class ProfileGetter {
	private UUID uuid = null;
	private String value = null;
	private String signature = null;
	private String cape = "NO";
	private boolean reqestvalid = true;
	protected ProfileGetter(String playerid) {
        try {
        	URL url = new URL("https://api.ashcon.app/mojang/v2/user/" + playerid);
        	
        	BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
        	JsonStreamParser jsonparser = new JsonStreamParser(in);
            while (jsonparser.hasNext()) {
            	JsonElement jsonelement = jsonparser.next();
            	if(!jsonelement.isJsonNull()) {
            		if(jsonelement.isJsonObject()) {
            			JsonObject ajsonobj = jsonelement.getAsJsonObject();
                		if (ajsonobj.has("uuid")) {
                			try {
                				uuid = UUID.fromString(ajsonobj.get("uuid").getAsString());
							} catch (IllegalArgumentException e) {
							}
                		}
                		if (ajsonobj.has("textures")) {
                			JsonElement jsontextureselement = ajsonobj.get("textures");
                			if(!jsontextureselement.isJsonNull()) {
			            		if(jsontextureselement.isJsonObject()) {
                        			JsonObject jsontexturesobject = jsontextureselement.getAsJsonObject();
                        			if (jsontexturesobject.has("cape")) {
                        				JsonElement jsoncapeelement = jsontexturesobject.get("cape");
			            				if(!jsoncapeelement.isJsonNull()) {
	    				            		if(jsoncapeelement.isJsonObject()) {
	    				            			JsonObject jsoncapeobject = jsoncapeelement.getAsJsonObject();
	    				            			if(jsoncapeobject.has("url")) {
	    				            				cape = detectCape(jsoncapeobject.get("url").getAsString());
	    				            			}
	    				            		}
			            				}
			            			}
			            			if (jsontexturesobject.has("raw")) {
	                        			JsonElement jsonrawelement = jsontexturesobject.get("raw");
	                        			if(!jsonrawelement.isJsonNull()) {
	    				            		if(jsonrawelement.isJsonObject()) {
	    				            			JsonObject jsoncrawobject = jsonrawelement.getAsJsonObject();
						            			if (jsoncrawobject.has("value")) {
	    				            				value = jsoncrawobject.get("value").getAsString();
	    				            			}
						            			if(jsoncrawobject.has("signature")) {
						            				signature = jsoncrawobject.get("signature").getAsString();
						            			}
	    				            			
	    				            		}
	                        			}
	                        		}
			            		}
			            	}
                		}
            		}
            	}
            }
            in.close();
        } catch (IOException e) {
        	reqestvalid = false;
		}
	}
	private static String detectCape(String url) {
    	switch(url) {
    	case "" : return "NO";
    	case "http://textures.minecraft.net/texture/ca35c56efe71ed290385f4ab5346a1826b546a54d519e6a3ff01efa01acce81" : return "COBALTS";
    	case "http://textures.minecraft.net/texture/e7dfea16dc83c97df01a12fabbd1216359c0cd0ea42f9999b6e97c584963e980" : return "MINECON_2016";
    	case "http://textures.minecraft.net/texture/b0cc08840700447322d953a02b965f1d65a13a603bf64b17c803c21446fe1635" : return "MINECON_2015";
    	case "http://textures.minecraft.net/texture/153b1a0dfcbae953cdeb6f2c2bf6bf79943239b1372780da44bcbb29273131da" : return "MINECON_2013";
    	case "http://textures.minecraft.net/texture/a2e8d97ec79100e90a75d369d1b3ba81273c4f82bc1b737e934eed4a854be1b6" : return "MINECON_2012";
    	case "http://textures.minecraft.net/texture/953cac8b779fe41383e675ee2b86071a71658f2180f56fbce8aa315ea70e2ed6" : return "MINECON_2011";
    	case "http://textures.minecraft.net/texture/5786fe99be377dfb6858859f926c4dbc995751e91cee373468c5fbf4865e7151" : return "MOJANG";
    	case "http://textures.minecraft.net/texture/9e507afc56359978a3eb3e32367042b853cddd0995d17d0da995662913fb00f7" : return "MOJANG_STUDIOS";
    	case "http://textures.minecraft.net/texture/ae677f7d98ac70a533713518416df4452fe5700365c09cf45d0d156ea9396551" : return "MOJIRA_MODERATOR";
    	case "http://textures.minecraft.net/texture/17912790ff164b93196f08ba71d0e62129304776d0f347334f8a6eae509f8a56" : return "REALMS_MAPMAKER";
    	case "http://textures.minecraft.net/texture/d8f8d13a1adf9636a16c31d47f3ecc9bb8d8533108aa5ad2a01b13b1a0c55eac" : return "PRISMARINE";
    	case "http://textures.minecraft.net/texture/2e002d5e1758e79ba51d08d92a0f3a95119f2f435ae7704916507b6c565a7da8" : return "SPADE";
    	case "http://textures.minecraft.net/texture/70efffaf86fe5bc089608d3cb297d3e276b9eb7a8f9f2fe6659c23a2d8b18edf" : return "MILIONTH_CUSTOMER";
    	case "http://textures.minecraft.net/texture/23ec737f18bfe4b547c95935fc297dd767bb84ee55bfd855144d279ac9bfd9fe" : return "SNOWMAN";
    	case "http://textures.minecraft.net/texture/bcfbe84c6542a4a5c213c1cacf8979b5e913dcb4ad783a8b80e3c4a7d5c8bdac" : return "DB";
    	case "http://textures.minecraft.net/texture/ca29f5dd9e94fb1748203b92e36b66fda80750c87ebc18d6eafdb0e28cc1d05f" : return "TRANSLATOR_JAPAN";
    	case "http://textures.minecraft.net/texture/5048ea61566353397247d2b7d946034de926b997d5e66c86483dfb1e031aee95" : return "TURTLE";
    	case "http://textures.minecraft.net/texture/2262fb1d24912209490586ecae98aca8500df3eff91f2a07da37ee524e7e3cb6" : return "TRANSLATOR_CHINA";
    	case "http://textures.minecraft.net/texture/3efadf6510961830f9fcc077f19b4daf286d502b5f5aafbd807c7bbffcaca245" : return "SCROLLS";
    	case "http://textures.minecraft.net/texture/8f120319222a9f4a104e2f5cb97b2cda93199a2ee9e1585cb8d09d6f687cb761" : return "MOJANG_CLASIC";
    	case "http://textures.minecraft.net/texture/1bf91499701404e21bd46b0191d63239a4ef76ebde88d27e4d430ac211df681e" : return "TRANSLATOR";
    	case "http://textures.minecraft.net/texture/2056f2eebd759cce93460907186ef44e9192954ae12b227d817eb4b55627a7fc" : return "BIRTHDAY";
    	case "http://textures.minecraft.net/texture/2340c0e03dd24a11b15a8b33c2a7e9e32abb2051b2481d0ba7defd635ca7a933" : return "MIGRATOR";
    	default : return "OTHER";
    	}
    }
	protected UUID getUUID() {
		return uuid;
	}
	protected String getValue() {
		return value;
	}
	protected String getSignature() {
		return signature;
	}
	protected String getCape() {
		return cape;
	}
	protected boolean isValidReqest() {
		return reqestvalid;
	}
}
