package me.bomb.skinlogger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

final class ArchivePacker {
	
	protected static HashMap<UUID,HashSet<SkinData>> getplayerskins(File workingdir) {
		HashSet<File> skinfiles = new HashSet<File>();
        for (File capedirectory : workingdir.listFiles()){
        	try {
	            if (!capedirectory.isDirectory() || CapeType.valueOf(capedirectory.getName())==null) {
	            	continue;
	            }
	            for (File skinfile : capedirectory.listFiles()){
            		if (!skinfile.isFile() || !skinfile.getName().endsWith(".skin")) {
            			continue;
            		}
            		skinfiles.add(skinfile);
            	}
        	} catch (IllegalArgumentException e) {
        		continue;
        	}
        }
        HashMap<UUID,HashSet<SkinData>> uuids = new HashMap<UUID,HashSet<SkinData>>();
        for(File skinfile : skinfiles) {
        	try {
        		BufferedReader reader = new BufferedReader(new FileReader(skinfile));
        		String value = reader.readLine();
        		String signature = reader.readLine();
        		reader.close();
    			SkinData data = new SkinData(value,signature);
    			if(data!=null&&data.capetype!=null) {
    				HashSet<SkinData> playerskins = uuids.containsKey(data.uuid) ? uuids.get(data.uuid) : new HashSet<SkinData>();
    				playerskins.add(data);
    	        	uuids.put(data.uuid,playerskins);
    			}
    		} catch (IllegalArgumentException|NullPointerException|IndexOutOfBoundsException|IOException e) {
    		}
        }
		return uuids;
	}
	
	protected static HashMap<UUID,EnumMap<CapeType,ArrayList<SkinData>>> sortplayerskins(HashMap<UUID,HashSet<SkinData>> unsortedplayerskins) {
        HashMap<UUID,EnumMap<CapeType,ArrayList<SkinData>>> playercapes = new HashMap<UUID,EnumMap<CapeType,ArrayList<SkinData>>>();
        for(UUID uuid : unsortedplayerskins.keySet()) {
        	EnumSet<CapeType> hascapetypes = EnumSet.noneOf(CapeType.class);
        	HashSet<SkinData> playerskins = unsortedplayerskins.get(uuid);
        	for(SkinData data : playerskins) {
        		hascapetypes.add(data.capetype);
        	}
        	EnumMap<CapeType,ArrayList<SkinData>> capemap = new EnumMap<CapeType,ArrayList<SkinData>>(CapeType.class);
        	for(CapeType capetype : hascapetypes) {
        		ArrayList<SkinData> skinhistory = new ArrayList<SkinData>();
        		for(SkinData data : playerskins) {
        			if(data.capetype==capetype) {
        				skinhistory.add(data);
        			}
        		}
        		if(skinhistory.size()>1) {
        			boolean sort = true;
            		while(sort) {
    	        		sort = false;
            			for(int i1=0,i2=1;i2<skinhistory.size();++i1,++i2) {
    	        			SkinData data1 = skinhistory.get(i1);
    	        			SkinData data2 = skinhistory.get(i2);
    	        			long timestamp1 = data1.timestamp;
    	        			long timestamp2 = data2.timestamp;
    	        			if(timestamp1>timestamp2) {
    	        				skinhistory.set(i1, data2);
    	        				skinhistory.set(i2, data1);
    	        				sort = true;
    	        			}
    	        		}
            		}
        		}
        		capemap.put(capetype, skinhistory);
        	}
        	playercapes.put(uuid, capemap);
        }
		return playercapes;
	}
	
	protected static int pack(File zipfile,HashMap<UUID,EnumMap<CapeType,ArrayList<SkinData>>> playerscapes) {
		try {
			ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(zipfile, false),Charset.forName("US-ASCII"));
			zipOutputStream.setMethod(ZipOutputStream.DEFLATED);
	        zipOutputStream.setLevel(Deflater.BEST_COMPRESSION);
	        int errors = 0;
	        int uniccapecount = 0;
	        for (UUID uuid : playerscapes.keySet()){
	        	EnumMap<CapeType,ArrayList<SkinData>> playercapes = playerscapes.get(uuid);
	        	for(CapeType capetype : playercapes.keySet()) {
	        		--uniccapecount;
	        		ArrayList<SkinData> capedskinhistory = playercapes.get(capetype);
	        		for(int i=0;i<capedskinhistory.size();++i) {
	        			SkinData capedskin = capedskinhistory.get(i);
	        			ZipEntry entry = new ZipEntry(capetype.name().concat("/").concat(Integer.toString(1+i)).concat("_").concat(uuid.toString()).concat(".skin"));
	        			FileTime time = FileTime.fromMillis(capedskin.timestamp);
	        			entry.setCreationTime(time);
	        			entry.setLastModifiedTime(time);
	        			entry.setLastAccessTime(time);
	        			byte[] dat = capedskin.value.concat("\n").concat(capedskin.signature).getBytes();
	        			try {
	                        zipOutputStream.putNextEntry(entry);
	                        zipOutputStream.write(dat);
	                        zipOutputStream.closeEntry();
	                    } catch (IOException e) {
	                    	++errors;
	                    }
	        			++uniccapecount;
	        		}
	        	}
	        }
	        StringBuilder sb = new StringBuilder("MINECRAFT CAPED SKIN ARCHIVE\nUniqule caped skins: ");
	        sb.append(uniccapecount);
		    sb.append("\nCreate time: ");
	        sb.append(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date(System.currentTimeMillis())));
	        zipOutputStream.setComment(sb.toString());
	        zipOutputStream.close();
	        return errors;
		} catch (IOException e) {
			return -1;
		}
    }
	
}
