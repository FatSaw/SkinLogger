package me.bomb.skinlogger;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

final class SkinGetter extends Thread {
	private static final AtomicInteger runcount = new AtomicInteger();
	protected final static HashSet<SkinGetter> loaded = new HashSet<SkinGetter>();
	private final UUID uuid;
	protected final int n;
	protected final short minimalgettime;
	SkinData skindata = null;
	private boolean ok;
	protected SkinGetter(UUID uuid,int n,short minimalgettime) {
		this.uuid = uuid;
		this.n = n;
		this.minimalgettime = minimalgettime;
		try {
			start();
			runcount.incrementAndGet();
		} catch (IllegalThreadStateException e) {
			e.printStackTrace();
		}
	}
	public void run() {
		long time = -1L;
		String value = null;
		String signature = null;
		try {
			URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/".concat(uuid.toString()).concat("?unsigned=false"));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            time = System.currentTimeMillis();
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            if (connection.getResponseCode() != 200) {
        		sleepremain(time);
            	synchronized (loaded) {
            		loaded.add(this);
            		runcount.decrementAndGet();
                    return;
            	}
            }

            InputStream input = connection.getInputStream();
            if (input == null) {
        		sleepremain(time);
            	synchronized (loaded) {
            		loaded.add(this);
            		runcount.decrementAndGet();
                	return;
            	}
            }
            InputStreamReader reponse = new InputStreamReader(input);
            
            JsonObject jsonproperty = new JsonParser().parse(reponse).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();
            value = jsonproperty.get("value").getAsString();
            signature = jsonproperty.get("signature").getAsString();

            reponse.close();
            input.close();
            connection.disconnect();
		} catch (IOException exception) {
			sleepremain(time);
            synchronized (loaded) {
                System.err.println(exception);
        		loaded.add(this);
        		runcount.decrementAndGet();
            	return;
            }
		}
		try {
			skindata = new SkinData(value,signature);
		} catch (NullPointerException|IllegalArgumentException e) {
			sleepremain(time);
			synchronized (loaded) {
	    		loaded.add(this);
        		runcount.decrementAndGet();
				return;
			}
		}
		if(uuid==null||(skindata.capeurl==null&&skindata.capetype==null)||!skindata.uuid.equals(this.uuid)) {
			sleepremain(time);
        	synchronized (loaded) {
        		loaded.add(this);
        		runcount.decrementAndGet();
            	return;
        	}
		}
		ok=true;
		sleepremain(time);
		synchronized (loaded) {
    		runcount.decrementAndGet();
			loaded.add(this);
		}
	}
	private void sleepremain(long time) {
		if(time!=-1L) {
			try {
				Thread.sleep(minimalgettime);
			} catch (InterruptedException e) {
			}
			return;
		}
		time = System.currentTimeMillis() - time;
		short sleeptime = (short) (time<0?minimalgettime:time>minimalgettime?0:minimalgettime-time);
		try {
			Thread.sleep(sleeptime);
		} catch (InterruptedException e) {
		}
	}
	protected UUID getUUID() {
		return uuid;
	}
	protected SkinData getSkinData() {
		return skindata;
	}
	protected boolean isOk() {
		return ok;
	}
	protected boolean isValid() {
		return skindata!=null;
	}
	protected static byte runcount() {
		return (byte) runcount.get();
	}
}
