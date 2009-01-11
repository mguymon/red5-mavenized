package org.red5;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.beans.factory.access.BeanFactoryLocator;
import org.springframework.context.access.ContextSingletonBeanFactoryLocator;

public class SpringTest {

	@Test
	public void springContextWillLoad() {
		System.setProperty("red5.deployment.type", "test");
		BeanFactoryLocator locator = ContextSingletonBeanFactoryLocator.getInstance("classpath:red5.xml");
		locator.useBeanFactory("red5.common");
		assertNotNull( locator );
	}
}
