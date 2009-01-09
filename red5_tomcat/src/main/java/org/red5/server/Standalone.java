package org.red5.server;

/*
 * RED5 Open Source Flash Server - http://www.osflash.org/red5
 * 
 * Copyright (c) 2006-2008 by respective authors (see below). All rights reserved.
 * 
 * This library is free software; you can redistribute it and/or modify it under the 
 * terms of the GNU Lesser General Public License as published by the Free Software 
 * Foundation; either version 2.1 of the License, or (at your option) any later 
 * version. 
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along 
 * with this library; if not, write to the Free Software Foundation, Inc., 
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA 
 */

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.red5.server.api.Red5;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.context.access.ContextSingletonBeanFactoryLocator;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Entry point from which the server config file is loaded.
 * 
 * @author The Red5 Project (red5@osflash.org)
 * @author Dominick Accattato (Dominick@gmail.com)
 * @author Luke Hubbard, Codegent Ltd (luke@codegent.com)
 */
public class Standalone {

	protected static String red5Config = "classpath:red5.xml";

	//public static DebugPooledByteBufferAllocator allocator;

	static {
		//set to use our logger
		System.setProperty("logback.ContextSelector", "org.red5.logging.LoggingContextSelector");
		
		//install the slf4j bridge (mostly for JUL logging)
		SLF4JBridgeHandler.install();
	}
	
	/**
     * Re-throws exception
     * @param e               Exception
     * @throws Throwable      Re-thrown exception
     */
	public static void raiseOriginalException(Throwable e) throws Throwable {
		// Search for root exception
		while (e.getCause() != null) {
			e = e.getCause();
		}
		throw e;
	}

	/**
	 * Main entry point for the Red5 Server usage Java Standalone.
	 * 
	 * @param args			String passed in that points to a red5.xml config file
     * @throws Throwable    Base type of all exceptions
	 */
	public static void main(String[] args) throws Throwable {
		
		//System.setProperty("DEBUG", "true");

		/*
		if (false) {
			allocator = new DebugPooledByteBufferAllocator(true);
			ByteBuffer.setAllocator(allocator);
		}
		*/

		if (args.length == 1) {
			red5Config = args[0];
		}

		long t1 = System.nanoTime();
		
		//we create the logger here so that it is instanced inside the expected 
		//classloader
		Logger log = LoggerFactory.getLogger(Standalone.class);

		log.info("{} (http://www.osflash.org/red5)", Red5.getVersion());
		log.info("Loading Red5 global context from: {}", red5Config);

		// Setup system properties so they can be evaluated by Jetty
		Properties props = new Properties();

        // Load properties
		props.load( Standalone.class.getResourceAsStream( "/red5.properties" ) );

        for (Object o : props.keySet()) {
            String key = (String) o;
            if (key != null && !key.equals("")) {
                System.setProperty(key, props.getProperty(key));
            }
        }

		try {
			ContextSingletonBeanFactoryLocator.getInstance(red5Config)
					.useBeanFactory("red5.common");
		} catch (Exception e) {
			// Don't raise wrapped exceptions as their stacktraces may confuse
			// people...
			//raiseOriginalException(e);
			e.printStackTrace();
		} finally {
			long t2 = System.nanoTime();
			log.info("Startup done in: {} ms", ((t2 - t1) / 1000000));
		}

	}

}
