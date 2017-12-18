package tinyTwitterAPI;

import tinyTwitterAPI.PMF;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import javax.jdo.Transaction;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;



import javax.jdo.Query;


import javax.inject.Named;
import javax.jdo.PersistenceManager;

@Api(name = "tinyTwitterEndpoint", 
	 namespace = @ApiNamespace(ownerDomain = "TinyTwitter.com", 
	 						   ownerName = "TinyTwitter.com",
	 						   packagePath = "services"))
public class TinyTwitterREST {

//	@ApiMethod(name = "getMessageById")
//	public MessageEntity getMessageById(@Named("id") Long id) {	
//		PersistenceManager mgr = getPersistenceManager();
//		MessageEntity message = OfyService.ofy().load().type(MessageEntity.class).id(id).now();
//		
//		return message;
//	}
//	
	@ApiMethod(name = "getTimeline")
	public Collection<MessageEntity> getTimeline(@Named("userId") Long userId) {		
				
		PersistenceManager mgr = getPersistenceManager();
		
		Query query = mgr.newQuery(MessageIndexEntity.class);

		query.setFilter("receivers == " + userId);

		List<MessageIndexEntity> userIndexes = (List<MessageIndexEntity>) query.execute();

		ArrayList<MessageEntity> results = new ArrayList<MessageEntity>();

		for (MessageIndexEntity msgIndexEntity : userIndexes)
			results.add(msgIndexEntity.getMessage());

				
		return results;
	}
	
	@ApiMethod(name="listUsers")
	public List<UserEntity> listUsers(){
		PersistenceManager mgr = getPersistenceManager();
		
		Query users = mgr.newQuery(UserEntity.class);
		
		return (List<UserEntity>)users.execute();
	}
	
	
	@ApiMethod(name = "insertNewMessage")
	public MessageEntity insertNewMessage(@Named("message") String message, @Named("userId") Long userId) {
		MessageEntity me = new MessageEntity(message, userId);
		PersistenceManager mgr = getPersistenceManager();
				
		UserEntity e = mgr.getObjectById(UserEntity.class,userId);
		
		MessageIndexEntity mIndex = new MessageIndexEntity(me);
		mIndex.addAllReceivers(e.getFollowers());
		mIndex.addReceiver(userId);
		
		Transaction tx = mgr.currentTransaction();

		try
		{
			tx.begin();
			mgr.makePersistent(mIndex);
			tx.commit();
		}
		finally
		{
			if (tx.isActive())
			{
				tx.rollback();
			}
		}
	
		
		mgr.close();
		return me;
	}
	
	@ApiMethod(name = "insertNewUser")
	public UserEntity insertNewUser(@Named("username") String username) {
		UserEntity e = new UserEntity(username);
		PersistenceManager mgr = getPersistenceManager();
		mgr.makePersistent(e);
		
		mgr.close();
		return e;
	}
	
	@ApiMethod(name = "addFollow")
	public UserEntity addFollow(@Named("userId") Long userId, @Named("followId") Long followId) {
		PersistenceManager mgr = getPersistenceManager();
		UserEntity e = mgr.getObjectById(UserEntity.class,followId);
		
		e.addFollower(userId);		
		
		mgr.close();
		return e;
	}
	
	private static PersistenceManager getPersistenceManager() {
		return PMF.get().getPersistenceManager();
	}
}
