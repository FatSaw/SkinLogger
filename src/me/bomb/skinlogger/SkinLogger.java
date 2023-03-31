package me.bomb.skinlogger;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Base64;
import java.util.Date;
import java.util.HashSet;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.UploadBuilder;
import com.dropbox.core.v2.files.WriteMode;

public class SkinLogger {
	private static final String ACCESS_TOKEN = "DROPBOX_ACCESS_TOKEN_HERE";
	protected static final String path = "SKINLOGGER";
	private final static byte getterthreads = 4;
	private final static short timeout = 333;

    public static void main(String[] args) {
    	SkinGetter.timeout = timeout;
		new Thread() {
			public void run() {
				while (true) {
					int i = readlast();
					UUID[] uuids = readUUIDS();
					while(i < uuids.length) {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
						}
						while(SkinGetter.runcount()<getterthreads) {
							new SkinGetter(uuids[i],i);
							++i;
						}
						save(uuids.length);
					}
					try {
						Thread.sleep(timeout);
					} catch (InterruptedException e) {
					}
					save(uuids.length);
					writelast(0);
					zip();
					syncdropbox();
				}
			}
		}.start();
	}
	private static int readlast() {
		int last = 0;
		try {
			BufferedReader slnreader = new BufferedReader(new FileReader(path + File.separator + "LAST.txt"));
			String slnreadedline = slnreader.readLine();
			last = Integer.parseInt(slnreadedline);
			slnreader.close();
		} catch (IOException | NumberFormatException e) {
		}
		return last;
	}
	private static void writelast(int index) {
		try {
			try(FileWriter writer = new FileWriter(path + File.separator + "LAST.txt", false))
	        {
	            writer.write(String.valueOf(index));
	            writer.flush();
	            writer.close();
	        }
		} catch (IOException e) {
		}
	}
	private static UUID[] readUUIDS () {
		HashSet<UUID> lines = new HashSet<UUID>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(path + File.separator + "UUIDS.txt"));
			String line;
			while ((line = reader.readLine()) != null) {
				try {
					UUID uuid = UUID.fromString(line);
					lines.add(uuid);
				} catch (IllegalArgumentException e) {
				}
	        }
			reader.close();
		} catch (IOException e) {
		}
		return lines.toArray(new UUID[lines.size()]);
	}
	private static void syncdropbox() {
        File file = new File(path + File.separator + "SKINLOGGER.zip");
        if(file.isFile()) {
        	System.out.println("START SYNCHRONIZATION");
        	DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/java-tutorial").build();
            DbxClientV2 client = new DbxClientV2(config, ACCESS_TOKEN);
        	try (InputStream in = new FileInputStream(file)) {
        		UploadBuilder metadata = client.files().uploadBuilder("/" + file.getName());
                metadata.withMode(WriteMode.OVERWRITE);
                metadata.withClientModified(new Date(file.lastModified()));
                metadata.withAutorename(false);
                metadata.uploadAndFinish(in);
            } catch (IOException | DbxException e) {
    		}
        	System.out.println("END SYNCHRONIZATION");
        } else {
        	System.out.println("SKIP SYNCHRONIZATION");
        }
        
		
	}
	private static void zip() {
		try {
			System.out.println("START ZIPPING");
			File file = new File(path + File.separator + "SKINLOGGER.zip");
			if(file.exists()) {
				file.delete();
			}
			file.createNewFile();
			ZipOutputStream zipOutputStream = new ZipOutputStream(Files.newOutputStream(file.toPath()));
	        zipOutputStream.setMethod(8);
	        zipOutputStream.setLevel(5);
	        File dir1 = new File(path);
	        for (File dir2 : dir1.listFiles()){
	            if (dir2.isDirectory()) {
	            	for (File dir3 : dir2.listFiles()){
	            		if (dir3.isFile()) {
	            			ZipEntry zipEntry = new ZipEntry(dir2.getName() + "/" + dir3.getName().toString());
	                        try {
	                            zipOutputStream.putNextEntry(zipEntry);
	                            Files.copy(dir3.toPath(), zipOutputStream);
	                            zipOutputStream.closeEntry();
	                        } catch (IOException e) {
	                        }
	            		}
	            	}
	            }
	        }
	        zipOutputStream.close();
			System.out.println("END ZIPPING");
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
	private static void save(int length) {
		HashSet<SkinGetter> removesgs = new HashSet<SkinGetter>();
		synchronized (SkinGetter.loaded) {
			for(SkinGetter skingetter : SkinGetter.loaded) {
				removesgs.add(skingetter);
				int n = skingetter.n;
				System.out.print("[");
				System.out.print(Integer.toString(1+skingetter.n));
				System.out.print("/");
				System.out.print(Integer.toString(length));
				System.out.print("] UUID: ");
				UUID uuid = skingetter.getUUID();
				System.out.print(uuid.toString());
				if(!skingetter.isValid()) {
        			System.out.println("! GET: FAIL!");
					continue;
				}
				if(!skingetter.isOk()) {
        			System.out.println("! GET: OK! CAPE: NO!");
					continue;
				}
    			System.out.print("! GET: OK! CAPE: ");
				CapeType cape = skingetter.getCape();
				String value = skingetter.getValue();
				String data = value.concat(":").concat(skingetter.getSignature());
    			System.out.print(cape.name());
    			File capedir = new File(path + File.separator + cape.name() + File.separator);
    			if(!capedir.exists()) {
        			capedir.mkdirs();
    			}
    			short o = 1;
    			File oldfile = null;
    			while(o<Short.MAX_VALUE) {
    				File file = new File(capedir,Short.toString(o).concat("_").concat(uuid.toString()).concat(".skin"));
    				if(file.exists()) {
    					oldfile = file;
    				} else {
    					o=Short.MAX_VALUE;
            			System.out.print("! CHANGES: ");
    					if(oldfile!=null) {
            				try {
            					BufferedReader reader = new BufferedReader(new FileReader(oldfile));
            					String readedline = reader.readLine();
            					reader.close();
	            				String previousvalue = readedline.substring(0, readedline.length()-685);
	            				String decodedvalue = new String(Base64.getDecoder().decode(value));
	            				String previousdecodedvalue = new String(Base64.getDecoder().decode(previousvalue));
	            				if(previousdecodedvalue.substring(previousdecodedvalue.indexOf("  \"textures\" : {")).equals(decodedvalue.substring(decodedvalue.indexOf("  \"textures\" : {")))) {
	            					System.out.println("NO!");
	            					break;
	            				} else {
	            					System.out.print("YES!");
	            				}
            				} catch (NullPointerException | IndexOutOfBoundsException | IOException e1) {
            					System.out.println("FILED TO CHECK!");
            					break;
            				}
    					} else {
        					System.out.print("FIRST RUN!");
    					}
    					try(FileWriter writer = new FileWriter(file, false)) {
            	            writer.write(data);
            	            writer.flush();
            	            writer.close();
        					System.out.println(" SAVE: OK!");
            	        } catch(IOException ex) {
        					System.out.println(" SAVE: FILED!");
            	            System.out.println(ex.getMessage());
            	        }
        	            break;
    				}
    				++o;
    			}
				writelast(n);
			}
			SkinGetter.loaded.removeAll(removesgs);
		}
	}
}