package me.bomb.skinlogger;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.UUID;


public class SkinVisualisator extends Thread {
	protected String last = "";
	protected SkinVisualisator() {
		setName("e8");
		start();
	}
	public void run() {
		try {
			try(ServerSocket socket = new ServerSocket(25755)) {
				byte[] favicon = Files.readAllBytes(new File("favicon.ico").toPath());
				byte[] skinview = Files.readAllBytes(new File("skinview.html").toPath());
				byte[] skinviewscript = Files.readAllBytes(new File("skinview.js").toPath());
				byte[] panorama = Files.readAllBytes(new File("panorama.png").toPath());
				
				System.out.println("Server started...");
				while(true) {
					try(Socket client = socket.accept()) {
						InputStreamReader inputstreamreader = new InputStreamReader(client.getInputStream());
						BufferedReader bufferedreader = new BufferedReader(inputstreamreader);
						StringBuilder reqest = new StringBuilder();
						try {
							String line = bufferedreader.readLine();
							byte l = 0;
							while(line!=null && !line.isEmpty() && l<32) {
								++l;
								reqest.append(line + "\r\n");
								line = bufferedreader.readLine();
							}
						} catch (IOException e) {
							client.close();
						}
						try {
							String reqesttype = reqest.toString().split("\n")[0];
							String responsetype = reqesttype.split(" ")[0];
							String response = reqesttype.split(" ")[1];
							switch (reqesttype.split(" ")[2]) {
							case "HTTP/1.1\r" :
								//System.out.println("--REQEST--");
								//System.out.println(reqest.toString());
								OutputStream clientout = client.getOutputStream();
								clientout.write("HTTP/1.1 200 OK\r\n".getBytes());
								clientout.write("\r\n".getBytes());
								switch(responsetype) {
								case "POST" :
									switch (response) {
									case "/getlast" :
										synchronized(last) {
											clientout.write(last.getBytes());
										}
									break;
									case "/getskin" : 
										try {
											StringBuilder payload = new StringBuilder();
											byte i = 0;
											while(bufferedreader.ready() && i < 48){
												++i;
												payload.append((char) bufferedreader.read());
											}
											if(payload.toString().contains(":")) {
												String[] getrandom = payload.toString().split(":",3);
												byte cape = Byte.decode(getrandom[0]);
												int pos = Integer.decode(getrandom[1]);
												UUID uuid = UUID.fromString(getrandom[2]);
												File skinfile = new File(SkinLogger.path.concat(File.separator).concat(detectCape(cape)).concat(File.separator).concat(Integer.toString(pos)).concat("_").concat(uuid.toString()).concat(".skin"));
												if(skinfile.isFile()) {
													if(skinfile.canRead()) {
														try {
															byte[] skindata = Files.readAllBytes(skinfile.toPath());
															clientout.write(skindata);
														} catch (IOException e) {
														}
													}
												}
											}
										} catch (IllegalArgumentException | IndexOutOfBoundsException e) {
										}
										break;
									}
									
								case "GET" :
									if(response.startsWith("/texture/")) {
										String texture = response.substring(9);
										if(texture.length()>64) texture.substring(0, 64);
										URL url = new URL("http://textures.minecraft.net/texture/" + texture);
										System.out.println("Reqest texture: " + texture);
										try {
											InputStream in = new BufferedInputStream(url.openStream());
											byte[] buf = new byte[4096];
											int n = 0;
											while (-1!=(n=in.read(buf)))
											{
												clientout.write(buf, 0, n);
											}
											in.close();
									    } catch (IOException e) {
									        e.printStackTrace();
									    }
									} else {
										switch (response) {
										case "/skinview.js" : 
											clientout.write(skinviewscript);
										break;
										case "/panorama.png" : 
											clientout.write(panorama);
										break;
										case "/favicon.ico" :
											clientout.write(favicon);
										break;
										default : 
											clientout.write(skinview);
										}
									}
								break;
								}
								clientout.flush();
							default : client.close();
							}
						} catch (ArrayIndexOutOfBoundsException e) {
						}
					}
				}
			}
		} catch (Exception e) {
		}
	}
	private static String detectCape(byte capeid) {
    	switch(capeid) {
    	case 0x00 : return "NO";
    	case 0x01 : return "COBALTS";
    	case 0x02 : return "MINECON_2016";
    	case 0x03 : return "MINECON_2015";
    	case 0x04 : return "MINECON_2013";
    	case 0x05 : return "MINECON_2012";
    	case 0x06 : return "MINECON_2011";
    	case 0x07 : return "MOJANG";
    	case 0x08 : return "MOJANG_STUDIOS";
    	case 0x09 : return "MOJIRA_MODERATOR";
    	case 0x0a : return "REALMS_MAPMAKER";
    	case 0x0b : return "PRISMARINE";
    	case 0x0c : return "SPADE";
    	case 0x0d : return "MILIONTH_CUSTOMER";
    	case 0x0e : return "SNOWMAN";
    	case 0x0f : return "DB";
    	case 0x10 : return "TRANSLATOR_JAPAN";
    	case 0x11 : return "TURTLE";
    	case 0x12 : return "TRANSLATOR_CHINA";
    	case 0x13 : return "SCROLLS";
    	case 0x14 : return "MOJANG_CLASIC";
    	case 0x15 : return "TRANSLATOR";
    	case 0x16 : return "BIRTHDAY";
    	case 0x17 : return "MIGRATOR";
    	default : return "OTHER";
    	}
    }
}
