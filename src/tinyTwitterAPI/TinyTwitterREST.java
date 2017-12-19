package tinyTwitterAPI;

import tinyTwitterAPI.PMF;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.Transaction;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;






import com.google.appengine.api.datastore.Query.SortDirection;

import javax.jdo.Query;
import javax.inject.Named;
import javax.jdo.PersistenceManager;

@Api(name = "tinyTwitterEndpoint", 
	 namespace = @ApiNamespace(ownerDomain = "TinyTwitter.com", 
	 						   ownerName = "TinyTwitter.com",
	 						   packagePath = "services"))
public class TinyTwitterREST {

	// Retourne la timeline du user "userId"
	@ApiMethod(name = "getTimeline")
	public List<MessageEntity> getTimeline(@Named("userId") Long userId, @Named("messageLimit") Long messageLimit) {		
				
		PersistenceManager mgr = getPersistenceManager();
		
		Query query = mgr.newQuery(MessageIndexEntity.class);

		query.setFilter("receivers == " + userId);
		query.setOrdering("timestamp desc");
		query.setRange(0, messageLimit); 

		List<MessageIndexEntity> userIndexes = (List<MessageIndexEntity>) query.execute();

		ArrayList<MessageEntity> results = new ArrayList<MessageEntity>();

		for (MessageIndexEntity msgIndexEntity : userIndexes)
			results.add(msgIndexEntity.getMessage());
		
//		// Tri messages par timestamp
//		Collections.sort(results, new Comparator<MessageEntity>() {
//	        @Override
//	        public int compare(MessageEntity message1, MessageEntity message2)
//	        {
//
//	            return  message2.timestamp.compareTo(message1.timestamp);
//	        }
//	    });
				
		return results;
	}
	
	// Renvoie la liste de tous les users
	@ApiMethod(name="listUsers")
	public List<UserEntity> listUsers(){
		PersistenceManager mgr = getPersistenceManager();
		
		Query users = mgr.newQuery(UserEntity.class);
		
		return (List<UserEntity>)users.execute();
	}
	
	// Ajoute un nouveau message dans le datastore
	@ApiMethod(name = "insertNewMessage")
	public MessageEntity insertNewMessage(@Named("message") String message, @Named("userId") Long userId) {
		MessageEntity me = new MessageEntity(message);
		PersistenceManager mgr = getPersistenceManager();
				
		UserEntity e = mgr.getObjectById(UserEntity.class,userId);
		
		// Encapsulation du message dans un MessageIndexEntity
		MessageIndexEntity mIndex = new MessageIndexEntity(me,userId);
		mIndex.addAllReceivers(e.getFollowers());
		mIndex.addReceiver(userId);
		
		
		// Transaction pour assurer le bon déroulement conjoint des deux ajouts (MessageEntity et MessageIndexEntity)
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
	
	// Ajoute un nouvel utilisateur dans le datastore
	@ApiMethod(name = "insertNewUser")
	public UserEntity insertNewUser(@Named("username") String username) {
		
		PersistenceManager mgr = getPersistenceManager();
		
		Query query = mgr.newQuery(UserEntity.class);	
		query.setFilter("username == uname");
		query.declareParameters("String uname");
		
		List<UserEntity> listResult;
				
		listResult = (List<UserEntity>) query.execute(username);		
		
		UserEntity e = null;
		
		if(listResult.isEmpty()){		
			e = new UserEntity(username);
			mgr.makePersistent(e);
		}
				
		mgr.close();
		return e;
	}
	
	// Ajoute un follower "followId" au user "userId"
	@ApiMethod(name = "addFollow")
	public UserEntity addFollow(@Named("userId") Long userId, @Named("followId") Long followId) {
		PersistenceManager mgr = getPersistenceManager();
		UserEntity e = mgr.getObjectById(UserEntity.class,followId);
		
		e.addFollower(userId);		
		
		
		// Rétroactivité du follow :		
		Query query = mgr.newQuery(MessageIndexEntity.class);

		query.setFilter("userId == " + followId);

		List<MessageIndexEntity> messageIndexEntities = (List<MessageIndexEntity>) query.execute();
		
		for(MessageIndexEntity m : messageIndexEntities){
			m.addReceiver(userId);
		}
		
		mgr.close();
		return e;
	}
	
	// Connecte le user défini par son username
	@ApiMethod(name = "connectUser")
	public UserEntity connectUser(@Named("username") String username) {
		PersistenceManager mgr = getPersistenceManager();
		
		Query userQuery = mgr.newQuery(UserEntity.class);
		userQuery.setFilter("username == uname");
		userQuery.declareParameters("String uname");
		
		List<UserEntity> users = (List<UserEntity>) userQuery.execute(username);		
			
		UserEntity e = null;
		
		if(!users.isEmpty()){	
			e = users.get(0);
		}
		
		return e;
	}
	
	private static PersistenceManager getPersistenceManager() {
		return PMF.get().getPersistenceManager();
	}
}
