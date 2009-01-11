package org.red5.spring;

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
 * 
 * @author Michael Guymon
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
	
	public Properties getProperties() {
		return properties;
	}
	
	/**
	 * 
	 * @param locations
	 * @throws IOException
	 */
	public void setWildcardLocations( String locations[] ) throws IOException {
		
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
	 * 
	 * @param key
	 * @param val
	 */
	public static synchronized void addPlaceholderProperty( String key, String val ) {
		manualPlaceholderProperties.setProperty( key, val );
	}
	
	/**
	 * 
	 * @return
	 */
	private static synchronized Properties copyOfManualProperties() {
		// return new Properties( runtimeProperties ); returns an empty prop ??
		
		Properties prop = new Properties();
		prop.putAll( manualPlaceholderProperties );
		
		return prop;
	}

}
