package me.bomb.skinlogger;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.UploadBuilder;
import com.dropbox.core.v2.files.WriteMode;

final class SkinLogger extends Thread {
	private final String ACCESS_TOKEN;
	private final String path;
	private final byte getterthreads;
	private final short minimalgettime;
	private boolean getskins;
	private boolean run;
	private UUID[] uuids;

    public static void main(String[] args) {
    	SkinLogger skinlogger = new SkinLogger("DROPBOX_API_KEY_HERE","SKINLOGGER",(byte)4,(short)350);
    	skinlogger.start();
    	BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    	boolean run = true;
		System.out.println("Starting...!");
    	while(skinlogger.isAlive()) {
    		try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
    		if(run) {
    			try {
    				if(reader.ready()) {
    					String cmd = reader.readLine();
    					if(cmd!=null) {
    						if(cmd.equals("pause")) {
    							skinlogger.getskins=false;
    							System.out.println("Skin get disabled!");
    						}
    						if(cmd.equals("start")) {
    							skinlogger.getskins=true;
    							System.out.println("Skin get enabled!");
    						}
    						if(cmd.equals("pack")) {
    							boolean enabledget = skinlogger.getskins;
    							if(enabledget) {
    								skinlogger.getskins = false;
        							System.out.println("Skin get disabled!");
    							}
    							skinlogger.pack("CAPE_SKINS.zip");
    							if(enabledget) {
    								skinlogger.getskins = true;
        							System.out.println("Skin get enabled!");
    							}
    						}
    						if(cmd.equals("count")) {
    							boolean enabledget = skinlogger.getskins;
    							if(enabledget) {
    								skinlogger.getskins = false;
        							System.out.println("Skin get disabled!");
    							}
    							skinlogger.count();
    							if(enabledget) {
    								skinlogger.getskins = true;
        							System.out.println("Skin get enabled!");
    							}
    						}
    						if(cmd.equals("stop")) {
    							skinlogger.getskins=false;
    							skinlogger.end();
    							reader.close();
    							System.out.println("Stopping...");
    							run = false;
    						}
    					}
    				}
    			} catch (IOException e) {
    			}
    		}
    	}
    	System.out.println("Stopped by stop command!");
	}
    protected SkinLogger(String ACCESS_TOKEN,String path,byte getterthreads,short minimalgettime) {
    	this.ACCESS_TOKEN = ACCESS_TOKEN;
    	this.path = path;
    	this.getterthreads = getterthreads;
    	this.minimalgettime = minimalgettime;
    	this.getskins = true;
	}
    public void run() {
    	run = true;
		while (true) {
			int i = readlast();
			readUUIDS();
			if(i >= uuids.length || i < 0) {
				i=0;
			}
			while(i < uuids.length) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
				}
				while(getskins&&SkinGetter.runcount()<getterthreads&&i<uuids.length) {
					new SkinGetter(uuids[i],i,minimalgettime);
					++i;
				}
				int n = save(uuids.length);
				if(n!=Integer.MAX_VALUE) {
					writelast(n);
				}
				if(!run&&!getskins) {
					break;
				}
			}
			byte w=0;
			while(SkinGetter.runcount()!=0&&w!=-1) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
				}
				++w;
			}
			save(uuids.length);
			if(!run) {
				return;
			}
			writelast(0);
			zip();
			syncdropbox();
		}
	}
    protected void end() {
    	run = false;
    }
	private int readlast() {
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
	private void writelast(int index) {
		try {
			try(FileWriter writer = new FileWriter(path + File.separator + "LAST.txt", false)) {
	            writer.write(String.valueOf(index));
	            writer.flush();
	            writer.close();
	        }
		} catch (IOException e) {
		}
	}
	private void readUUIDS () {
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
		this.uuids = lines.toArray(new UUID[lines.size()]);
	}
	private void syncdropbox() {
        File file = new File(path + File.separator + "SKINLOGGER.zip");
        if(!file.isFile()) {
        	System.out.println("SKIP SYNCHRONIZATION");
        	return;
        }
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
	}
	private void zip() {
		System.out.println("START ZIPPING");
		pack("SKINLOGGER.zip");
		System.out.println("END ZIPPING");
		/*try {
			System.out.println("START ZIPPING");
			File file = new File(path + File.separator + "SKINLOGGER.zip");
			if(file.exists()) {
				file.delete();
			}
			file.createNewFile();
			ZipOutputStream zipOutputStream = new ZipOutputStream(Files.newOutputStream(file.toPath()));
	        zipOutputStream.setMethod(8);
	        zipOutputStream.setLevel(5);
	        File workingdirectory = new File(path);
	        for (File capedirectory : workingdirectory.listFiles()){
	        	String capedirectoryname = null;
	            if (!capedirectory.isDirectory() || CapeType.valueOf(capedirectoryname = capedirectory.getName())==null) {
	            	continue;
	            }
	            for (File skinfile : capedirectory.listFiles()){
            		String skinfileentry = null;
            		if (!skinfile.isFile() || !(skinfileentry = capedirectoryname.concat("/").concat(skinfile.getName())).endsWith(".skin")) {
            			continue;
            		}
                    try {
                        zipOutputStream.putNextEntry(new ZipEntry(skinfileentry));
                        Files.copy(skinfile.toPath(), zipOutputStream);
                        zipOutputStream.closeEntry();
                    } catch (IOException e) {
                    }
            	}
	        }
	        zipOutputStream.close();
			System.out.println("END ZIPPING");
		} catch (IOException e) {
			e.printStackTrace();
		}*/
    }
	private void pack(String archivename) {
		File workingdir = new File(path);
		File zipfile = new File(path, archivename);
		System.out.print("Reading files... ");
		HashMap<UUID, HashSet<SkinData>> unsortedskins = ArchivePacker.getplayerskins(workingdir);
    	System.out.println("OK!");
    	System.out.print("Sorting... ");
		HashMap<UUID, EnumMap<CapeType, ArrayList<SkinData>>> sortedskins = ArchivePacker.sortplayerskins(unsortedskins);
    	System.out.println("OK!");
    	System.out.print("Packing ".concat(zipfile.getPath()).concat(" ... "));
        int errcount = ArchivePacker.pack(zipfile, sortedskins);
        if(errcount==-1) {
        	System.out.println("FAILED!");
        } else if(errcount==0) {
        	System.out.println("OK!");
        } else {
        	System.out.println("OK! ERRORS: ".concat(Integer.toString(errcount)));
        }
	}
	private void count() {
		File workingdir = new File(path);
		System.out.print("Reading files... ");
		HashMap<UUID, HashSet<SkinData>> unsortedskins = ArchivePacker.getplayerskins(workingdir);
    	System.out.println("OK!");
    	System.out.print("Sorting... ");
		HashMap<UUID, EnumMap<CapeType, ArrayList<SkinData>>> sortedskins = ArchivePacker.sortplayerskins(unsortedskins);
    	System.out.println("OK!");
    	unsortedskins=null;
        EnumMap<CapeType,Long> capecounts = new EnumMap<CapeType,Long>(CapeType.class);
        long totalcapes = 0;
        long totaluniccapes = 0;
        for(UUID uuid : sortedskins.keySet()) {
        	EnumMap<CapeType, ArrayList<SkinData>> capemap = sortedskins.get(uuid);
        	for(CapeType capetype : capemap.keySet()) {
        		ArrayList<SkinData> sorteddata = capemap.get(capetype);
        		int sortedsize = sorteddata.size();
        		long capecount = capecounts.containsKey(capetype) ? capecounts.get(capetype) : 0L,uniccapecount = capecount;
        		capecount&=0x00000000FFFFFFFFL;
        		uniccapecount&=0xFFFFFFFF00000000L;
        		uniccapecount = uniccapecount>>32;
        		capecount+=sortedsize;
        		totalcapes+=sortedsize;
        		uniccapecount+=sortedsize;
        		totaluniccapes+=sortedsize;
        		--uniccapecount;
        		--totaluniccapes;
        		capecount|=uniccapecount<<32;
        		capecounts.put(capetype, Long.valueOf(capecount));
        	}
        }
        sortedskins=null;
        StringBuffer sb = new StringBuffer("Cape_name: count|uniccount\n");
        sb.append("Total: ");
        sb.append(totalcapes);
        sb.append("|");
        sb.append(totaluniccapes);
        for(CapeType capetype : capecounts.keySet()) {
        	sb.append("\n");
        	sb.append(capetype.name());
        	sb.append(": ");
        	long capecount = capecounts.get(capetype),uniccapecount = capecount;
        	capecount&=0x00000000FFFFFFFFL;
    		uniccapecount&=0xFFFFFFFF00000000L;
    		uniccapecount = uniccapecount>>32;
        	sb.append(capecount);
	        sb.append("|");
	        sb.append(uniccapecount);
        }
		System.out.println(sb.toString());
	}
	private int save(int length) {
		HashSet<SkinGetter> removesgs = new HashSet<SkinGetter>();
		synchronized (SkinGetter.loaded) {
			int j = Integer.MAX_VALUE;
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
				SkinData skindata = skingetter.getSkinData();
				String skinurl = skindata.skinurl;
				String capeurl = skindata.capeurl;
				CapeType capetype = skindata.capetype;
    			System.out.print(capetype.name());
    			File capedir = new File(path + File.separator + capetype.name() + File.separator);
    			if(!capedir.exists()) {
        			capedir.mkdirs();
    			}
    			short o = 1;
    			File previousfile = null;
    			while(o<Short.MAX_VALUE) {
    				File file = new File(capedir,Short.toString(o).concat("_").concat(uuid.toString()).concat(".skin"));
    				if(file.exists()) {
    					previousfile = file;
    				} else {
    					o=Short.MAX_VALUE;
            			System.out.print("! CHANGES: ");
    					if(previousfile!=null) {
    						try {
    							SkinData previousdata = SkinData.read(previousfile);
    							String previousskinurl = previousdata.skinurl;
    							String previouscapeurl = previousdata.capeurl;
    							CapeType previouscapetypel = previousdata.capetype;
    							if(skinurl.equals(previousskinurl)&&(capetype==previouscapetypel||capeurl.equals(previouscapeurl))) {
    								System.out.println("NO!");
	            					break;
    							} else {
	            					System.out.print("YES!");
	            				}
    						} catch (IllegalArgumentException|NullPointerException|IndexOutOfBoundsException|IOException e) {
    							System.out.println("FILED TO CHECK!");
            					break;
    						}
    					} else {
        					System.out.print("FIRST RUN!");
    					}
    					try{
        					skindata.write(file);
        					System.out.println(" SAVE: OK!");
    					} catch(IOException ex) {
        					System.out.println(" SAVE: FILED!");
            	            System.out.println(ex.getMessage());
            	        }
        	            break;
    				}
    				++o;
    			}
				if(j>n) {
    				j=n;
    			}
			}
			SkinGetter.loaded.removeAll(removesgs);
			return j;
		}
	}
}