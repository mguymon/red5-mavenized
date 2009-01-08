package ${groupId};

// Red5 Flash Media Server
import org.red5.server.adapter.MultiThreadedApplicationAdapter;
import org.red5.server.api.IClient;
import org.red5.server.api.IConnection;
import org.red5.server.api.IScope;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SampleApp extends MultiThreadedApplicationAdapter {
	
	private static Logger logger = LoggerFactory.getLogger( SampleApp.class );
	
	@Override
	public boolean appStart (IScope app ) {
		super.appStart( app );
	    logger.info( "App started" );
	    return true;
	}

	@Override
	public void appStop (IScope app ) {		
		logger.info( "App stopped" );
		super.appStop( app );
	}

	@Override
	public boolean appConnect( IConnection connection, Object[] params ) {
	    super.appConnect( connection, params );
	    
	    logger.info( "Client connected" );
	    
	    String path = connection.getPath();
	    IClient client = connection.getClient();
	    
	    return true;
	}

	
	@Override
	public void appDisconnect( IConnection connection) {
		logger.info( "Client disconnected" );
	    super.appDisconnect(connection);
	}
}
