package org.red5.spring;

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


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.Resource;

/**
 * An extension of {@link PropertyPlaceholderConfigurer}. Provides wildcard lookups using {@link #setWildcardLocations(String[])},
 * using {@link PathMatchingResourcePatternResolver} for matching locations. Additional global properties can manually
 * be added using {@link #addPlaceholderProperty(String, String), but must happen *before* the being loaded into a 
 * {@link ApplicationContext}. 
 * 
 * @author Michael Guymon (michael.guymon@gmail.com)
 *
 */
public class ExtendedPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {

	private static Logger logger = LoggerFactory.getLogger( ExtendedPropertyPlaceholderConfigurer.class );
	private static Properties manualPlaceholderProperties = new Properties();
	
	private Properties properties;
	
	@Override
	protected void processProperties(
			ConfigurableListableBeanFactory beanFactoryToProcess,
			Properties props) throws BeansException {
		props.putAll( copyOfManualProperties() );
		logger.debug( "Placeholder props: {}", props.toString() );
		
		this.properties = props;
		
		super.processProperties( beanFactoryToProcess, props );
	}
	
	/**
	 * Merged {@link Properties} 
	 * 
	 * @return {@link Properties}
	 */
	public Properties getProperties() {
		return properties;
	}
	
	/**
	 * String[] of locations of properties that are converted to Resources[] using
	 * using {@link PathMatchingResourcePatternResolver}
	 * 
	 * @param locations String[]
	 * @throws IOException
	 */
	public void setWildcardLocations( String[] locations ) throws IOException {
		
		List<Resource> resources = new ArrayList<Resource>();
		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver( this.getClass().getClassLoader() );
		
		for( String location: locations ) {
			logger.debug( "Loading location {}", location );
			try {
				Resource[] configs = resolver.getResources(location);
				if ( configs != null && configs.length > 0) {
					for ( Resource resource: configs ) {
						logger.debug( "Loading {} for location {}", resource.getFilename(), location );
						resources.add( resource );
					}
				} else {
					logger.info( "Wildcard location does not exist: {}", location );
				}
			} catch (IOException ioException) {
				logger.error( "Failed to resolve location: {} - {}", location, ioException );
			}
		}
		
		this.setLocations( ( Resource[] )resources.toArray( new Resource[resources.size()] ) );
	}
	
	/**
	 * Add a property to be merged
	 * 
	 * @param key String
	 * @param val String
	 */
	public static synchronized void addPlaceholderProperty( String key, String val ) {
		manualPlaceholderProperties.setProperty( key, val );
	}
	
	/**
	 * Copy of the manual properties
	 * 
	 * @return {@link Properties}
	 */
	private static synchronized Properties copyOfManualProperties() {
		// return new Properties( runtimeProperties ); returns an empty prop ??
		
		Properties prop = new Properties();
		prop.putAll( manualPlaceholderProperties );
		
		return prop;
	}

}
