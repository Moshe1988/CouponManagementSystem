package com.cs.rest;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RestConfiguration {

	/**
	 * This map defined here in order to to access the same map from everywhere in
	 * the application.
	 * 
	 * @return HashMap to store all 'ClientSession'-token pairs for the token based
	 *         login system.
	 */
	@Bean(name = "tokens")
	public Map<String, ClientSession> tokensMap() {
		return new HashMap<>();
	}

}
