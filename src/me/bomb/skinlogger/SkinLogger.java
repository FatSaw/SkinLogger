package me.bomb.skinlogger;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
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
	private static PublicKey publicKey;
	private static final String ACCESS_TOKEN = "DROPBOX_ACCESS_TOKEN_HERE";
	protected static final String path = "SKINLOGGER";
	static {
	    try {
		publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(new byte[] {48, -126, 2, 34, 48, 13, 6, 9, 42, -122, 72, -122, -9, 13, 1, 1, 1, 5, 0, 3, -126, 2, 15, 0, 48, -126, 2, 10, 2, -126, 2, 1, 0, -54, 80, 120, 7, -87, -71, -105, 62, -29, -62, -73, 5, -49, -95, 93, -3, -7, -33, 82, 23, 47, -105, 27, 19, 74, 126, 100, 32, -81, -10, 104, 97, -75, 11, 121, 12, -53, -123, 99, -121, 9, 113, 23, -96, 69, 81, 41, -33, -58, 19, 108, -64, -8, -31, -35, -104, -83, -112, 28, 68, 0, 86, 54, -26, -20, -28, 36, 84, 112, 31, 5, 64, -10, 103, 112, -115, -85, 33, 92, -126, -19, 71, -93, -86, 116, -75, -41, 21, 92, -108, 60, -123, 17, 44, -28, -106, -88, 99, 65, -75, -67, 60, 16, -34, 95, 50, 113, -68, -120, 82, -32, 16, -110, 1, 51, 27, 61, 6, -15, 119, -77, 127, -59, -122, -31, -14, 60, -88, -107, 77, -103, -105, 99, 69, -104, -31, -100, -104, -86, -6, 10, 71, -27, -29, 105, 52, 79, 62, -16, 108, 13, -82, 22, 35, 107, -75, -49, 21, -52, 120, 121, 104, 117, 61, -27, -100, 44, -43, -65, 35, -104, 4, -75, -53, -24, 34, 96, 7, 43, -114, -59, -57, 9, -14, 25, -41, 124, 95, 114, 99, -127, 116, 105, -30, -12, -13, 66, 115, -1, 127, -124, 4, -47, -68, 68, -18, 1, -54, 27, 65, 127, -125, 114, -16, -44, 58, -78, -43, -84, -115, -9, -108, 34, -15, -5, 109, 79, -8, -52, 38, 28, 96, -22, -72, 90, -78, 39, 92, 122, -110, -14, -86, -18, 14, 98, 37, -20, -2, 87, 92, 103, 26, 110, -20, -48, -78, -45, -33, -2, 29, -126, 68, 5, 41, 63, -13, -27, 29, 119, 12, -106, -9, -80, -114, 97, -108, -24, -57, -61, 43, -23, 98, 125, 39, 13, 99, 8, -49, -59, 26, 56, -100, -92, -58, 21, -105, -1, -47, -86, -121, 10, 31, -60, 2, -60, 50, 44, -86, -50, 33, -124, 68, -46, 80, -58, -35, -68, -63, -121, -54, 32, 32, -62, -64, -70, 11, 0, 124, 22, -6, 51, 121, -86, 10, -94, -102, 76, -64, 31, -3, 80, 35, -121, -111, 69, 38, 120, -46, -5, -74, -60, -7, -74, 68, 87, 74, -80, 107, 29, 66, 18, -125, -49, 36, -67, 95, 33, 8, -50, 75, -52, -29, -45, -50, -96, -114, -111, -111, 10, -44, -78, -9, -26, -45, 61, 55, -45, 71, -12, -119, -7, 85, 1, -28, 109, -120, 120, 34, 45, -112, -47, -23, 37, 125, -78, -126, -120, -59, 103, -39, -7, 22, 120, -5, 71, -42, 103, 56, -49, -117, -14, 124, -120, -116, -5, -73, 105, -29, -17, -75, -54, -41, -105, 34, -31, 31, -8, 111, 57, -14, 14, -116, -4, 78, -106, -91, -66, 59, -108, 48, 72, 83, -8, 56, 71, 50, 44, 5, 32, 13, -43, 32, -55, 109, -6, 103, -103, -127, -37, -50, 110, 41, 111, -30, -126, 28, -39, -80, -28, -34, -67, 85, 6, -39, 12, 3, 34, -45, -101, 33, 94, -94, -15, 16, -79, 21, -77, 118, -96, 49, -14, 61, -93, -71, -113, 91, 83, 104, 19, 114, 86, -33, 2, 3, 1, 0, 1}));
	    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
	    }
	}
	/*private static byte capetoid(String capename) {
    	switch(capename) {
    	case "NO" : return 0x00;
    	case "COBALTS" : return 0x01;
    	case "MINECON_2016" : return 0x02;
    	case "MINECON_2015" : return 0x03;
    	case "MINECON_2013" : return 0x04;
    	case "MINECON_2012" : return 0x05;
    	case "MINECON_2011" : return 0x06;
    	case "MOJANG" : return 0x07;
    	case "MOJANG_STUDIOS" : return 0x08;
    	case "MOJIRA_MODERATOR" : return 0x09;
    	case "REALMS_MAPMAKER" : return 0x0a;
    	case "PRISMARINE" : return 0x0b;
    	case "SPADE" : return 0x0c;
    	case "MILIONTH_CUSTOMER" : return 0x0d;
    	case "SNOWMAN" : return 0x0e;
    	case "DB" : return 0x0f;
    	case "TRANSLATOR_JAPAN" : return 0x10;
    	case "TURTLE" : return 0x11;
    	case "TRANSLATOR_CHINA" : return 0x12;
    	case "SCROLLS" : return 0x13;
    	case "MOJANG_CLASIC" : return 0x14;
    	case "TRANSLATOR" : return 0x15;
    	case "BIRTHDAY" : return 0x16;
    	case "MIGRATOR" : return 0x17;
    	default : return 0x7f;
    	}
    }*/
    public static void main(String[] args) {
		if(publicKey!=null) {
			//SkinVisualisator sv = new SkinVisualisator();
			new Thread() {
				public void run() {
					while (true) {
						try {
							
							/*
							File nf = new File(path + File.separator + "NAMES.txt");
							if(nf.exists()) {
								System.out.println("NAMES.txt detected!");
								BufferedReader readernames = null;
								try {
									readernames = new BufferedReader(new FileReader(path + "\\NAMES.txt"));
								} catch (FileNotFoundException e1) {
									e1.printStackTrace();
								}
								System.out.print("Removing invalid chars... ");
								String names = readernames.readLine();
								names = names.replace(" ", "|");
								names = names.replaceAll("[^A-Za-z0-9_|]", "");
								System.out.println("Success");
								ArrayList<String> namelines = new ArrayList<String>();
								System.out.print("Conventing to ArrayList... ");
								while(names.contains("|")) {
									int in = names.indexOf("|");
									String name = names.substring(0, in);
									names = names.substring(in+1);
									namelines.add(name);
								}
								System.out.println("Success");
								ArrayList<String> nuifbuffer = new ArrayList<String>();
								File nuif = new File(path + File.separator + "UUIDS.txt");
								if(nuif.exists()) {
									BufferedReader nuireader = null;
									try {
										nuireader = new BufferedReader(new FileReader(path + "\\UUIDS.txt"));
									} catch (FileNotFoundException e1) {
										e1.printStackTrace();
									}
									String nuifline;
									while ((nuifline = nuireader.readLine()) != null) {
										nuifbuffer.add(nuifline);
							        }
									nuireader.close();
								}
								try(FileWriter writer = new FileWriter(path + File.separator + "UUIDS.txt", false))
						        {
									int c = 1; 
									for(String aname : namelines) {
										if(c>nuifbuffer.size()) {
											System.out.print("Getting uuid (" + c + "/" + namelines.size() + ") for " + aname + " ... ");
											
											ProfileGetter profilegetter = new ProfileGetter(aname);
								            UUID uuid = profilegetter.getUUID();
								            
								            writer.append(uuid.toString());
										} else {
											System.out.println("Skipping uuid (" + c + "/" + namelines.size() + ") for " + aname + " ... ");
											String uuid = "";
											uuid = nuifbuffer.get(c-1);
											writer.append(uuid);
										}
										writer.append("\n");
										writer.flush();
											
										c=c+1;
									}
									writer.close();
						        }
						        catch(IOException ex){
						            System.out.println(ex.getMessage());
						        }
							}
							*/
							
							 //UUIDS TO LINES
							int i = readlast();
							UUID[] uuids = readUUIDS();
							while(i < uuids.length) {
								System.out.print("[" + (i+1) + "/" + uuids.length + "]");
				        		ProfileGetter profilegetter = new ProfileGetter(uuids[i].toString());
				        		if(profilegetter.isValidReqest()) {
					            	UUID uuid = profilegetter.getUUID();
						            String value = profilegetter.getValue();
						            String signature = profilegetter.getSignature();
						            String cape = profilegetter.getCape();
						            if(uuid!=null && uuids[i].equals(uuid)) {
						            	System.out.print(" UUID: " + uuid.toString());
						            	System.out.print(" Signature");
						            	if(value!=null && signature!=null) {
						            		if(isSignatureValid(value, signature)) {
						            			System.out.print(" OK! Cape");
						            			if(!cape.equals("NO")) {
							            			System.out.print(" " + cape + "!");
							            			String decodedvalue = decodeBase64(value);
							            			System.out.print(" Checking changes");
							    					String folderpath = path + File.separator + cape + File.separator;
								    				File opf = new File(folderpath);
								    				if(opf.isDirectory() == false && opf.isFile() == false) {
								    					opf.mkdirs();
								    				}
							    	    			int o = 1;
							    	    			boolean ne = false;
							    	    			while(o<Integer.MAX_VALUE && ne == false) {
							    	    				String outpath = folderpath + o + "_" + uuid.toString() + ".skin";
							    	    				++o;
							    	    				String newpath = folderpath + o + "_" + uuid.toString() + ".skin";
							    	    				
							    	    				File f = new File(newpath);
							    	    				String text = value + ":" + signature;
							    	        			if(!f.exists()) {
							    	        				ne = true;
							    	        				File of = new File(outpath);
							    	            			if(of.exists()) {
							    	            				BufferedReader reader2 = null;
							    	            				try {
							    	            					reader2 = new BufferedReader(new FileReader(outpath));
							    	            				} catch (FileNotFoundException e1) {
							    	            					e1.printStackTrace();
							    	            				}
							    	            				String readedline = reader2.readLine();
							    	            				String previousdecodedvalue = decodeBase64(readedline.substring(0, readedline.length()-685));
							    	            				if(previousdecodedvalue.substring(previousdecodedvalue.indexOf("  \"textures\" : {")).equals(decodedvalue.substring(decodedvalue.indexOf("  \"textures\" : {")))) {
							    	            					System.out.println(" NO!");
							    	            				} else {
							    	            					System.out.println(" YES!");
							    	            					try(FileWriter writer = new FileWriter(newpath, false))
							    	                    	        {
							    	                    	            writer.write(text);
							    	                    	            writer.flush();
							    	                    	            writer.close();
							    	                    	        }
							    	                    	        catch(IOException ex){
							    	                    	            System.out.println(ex.getMessage());
							    	                    	        }
							    	            				}
							    	            			} else {
							    	            				System.out.println(" FIRST RUN!");
							    	            				try(FileWriter writer = new FileWriter(outpath, false))
							    	                	        {
							    	                	            writer.write(text);
							    	                	            writer.flush();
							    	                	            writer.close();
							    	                	        }
							    	                	        catch(IOException ex){
							    	                	            System.out.println(ex.getMessage());
							    	                	        }
							    	            			}
							    	            			//synchronized(sv.last) {
							    	            			//	sv.last = String.format("%02X", capetoid(cape)).concat(":").concat(Integer.toHexString(o)).concat(":").concat(uuid.toString());
							    	            			//}
							    	        			}
							    	    			}
							            		} else {
							            			System.out.println(" NO!");
							            		}
						            		} else {
						            			System.out.println(" FAIL!");
						            		}
						            	} else {
						            		System.out.println(" FAIL!");
						            	}
						            } else {
						            	System.out.println(" UUID ERROR!");
						            }
						            writelast(i);
									++i;
					            } else {
					            	System.out.println(" REQEST ERROR!");
					            	try {
										Thread.sleep(5000L);
									} catch (InterruptedException e) {
									}
					            }
							}
							writelast(0);
							zip();
					    	syncdropbox();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}.start();
		} else System.out.println("Public key initialization failed!");
	}
    private static String decodeBase64(String base64string) {
		return new String(Base64.getDecoder().decode(base64string));
	}
	public static boolean isSignatureValid(String value,String signature) {
        try {
            final Signature sig = Signature.getInstance("SHA1withRSA");
            sig.initVerify(publicKey);
            sig.update(value.getBytes());
            return sig.verify(Base64.getDecoder().decode(signature));
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        catch (InvalidKeyException e2) {
            e2.printStackTrace();
        }
        catch (SignatureException e3) {
            e3.printStackTrace();
        }
        return false;
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
}