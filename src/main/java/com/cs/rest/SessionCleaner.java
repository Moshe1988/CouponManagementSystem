package com.cs.rest;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class SessionCleaner implements Runnable {

	private static final long SESSION_LENGTH_MIN = 30;
	private static final long MIN_LENGTH_MILLIS = 60_000;
	private static final long SESSION_LENGTH_MILLIS = SESSION_LENGTH_MIN * MIN_LENGTH_MILLIS;

	private boolean isWorking;
	/**
	 * This map defined in 'RestConfiguration' in order to to access the same map
	 * from everywhere in the application.
	 */
	private Map<String, ClientSession> tokensMap;
	/**
	 * This map is a copy map of the above map ('tokensMap') so the while loop in
	 * the function 'run' will run on this map and the original map keep on getting
	 * new items otherwise the loop over the map stack in exception when new item is
	 * added.
	 */
	private Map<String, ClientSession> tokensMapCopy;

	@Autowired
	public SessionCleaner(@Qualifier("tokens") Map<String, ClientSession> tokensMap) {
		this.tokensMap = tokensMap;
		tokensMapCopy = new HashMap<>();
	}

	/**
	 * This function will clean sessions from the 'tokensMap' if the session is pass
	 * the time that determined above.
	 * 
	 * the process is explained here:
	 * 
	 * This function set 'isWorking' to true so the 'stop' method below can stop the
	 * running when needed, the function begin by copy the original 'tokensMap' to
	 * the 'tokensMapCopy' and check that the size is the same to verify the copy
	 * complete. the function will check if the 'getLastAccessedMillis'
	 * (see @ClientSession) is pass the time that determined in the field
	 * 'SESSION_LENGTH_MILLIS' above, and delete it from both maps if it did.
	 */
	@Override
	public void run() {
		isWorking = true;

		while (isWorking) {

			tokensMapCopy.putAll(tokensMap);

			while (tokensMapCopy.size() == tokensMap.size()) {

				for (Iterator<Map.Entry<String, ClientSession>> entryIt = tokensMapCopy.entrySet().iterator(); entryIt
						.hasNext();) {

					Map.Entry<String, ClientSession> entry = entryIt.next();

					long lastAccessedMillis = entry.getValue().getLastAccessedMillis();

					if (System.currentTimeMillis() - lastAccessedMillis >= SESSION_LENGTH_MILLIS) {
						tokensMap.remove(entry.getKey());
						entryIt.remove();
					}
				}
			}
		}
	}

	public void stop() {
		isWorking = false;
	}

}
