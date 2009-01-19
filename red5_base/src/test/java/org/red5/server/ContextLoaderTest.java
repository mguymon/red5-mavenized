package org.red5.server;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static org.junit.Assert.*;

public class ContextLoaderTest {

	protected ContextLoader loader;
	
	@Before
	public void buildContextLoader() {
		loader = new ContextLoader();
		loader.setApplicationContext( new ClassPathXmlApplicationContext( "/org/red5/server/parent_context_loader_test.xml" ) );
		loader.setParentContext( new ClassPathXmlApplicationContext( "/org/red5/server/parent_context_loader_test.xml" ) );
		loader.setContextsConfig( "classpath:/org/red5/server/classpath_context_loader_test.properties" );
		
	}

	@Test
	public void testInitContextInPropertiesByClassPath() throws Exception {
		loader.init();
		ApplicationContext context = loader.getContext( "test_context" );
		assertNotNull( context.getBean( "testSerializer" ) );
		assertNotNull( context.getBean( "testDeserializer" ) );
	}

	@Test
	public void testSystemPropertiesReplacePlacementHoldersInContextXml() throws Exception {
		System.setProperty( "red5.root", "/org/red5/server" );
		System.setProperty( "red5.config_root", "/context_loader_test.xml" );
		loader.setContextsConfig( "classpath:/org/red5/server/sysprop_context_loader_test.properties" );
		loader.init();
		
		ApplicationContext context = loader.getContext( "test_context" );
		
		assertNotNull( context.getBean( "testSerializer" ) );
		assertNotNull( context.getBean( "testDeserializer" ) );
	}
	
}
