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
	public List<MessageEntity> getTimeline(@Named("userId") Long userId, @Named("messageLimitBegin") Long messageLimitBegin, @Named("messageLimitEnd") Long messageLimitEnd) {		
				
		PersistenceManager mgr = getPersistenceManager();
		
		Query query = mgr.newQuery(MessageIndexEntity.class);

		query.setFilter("receivers == " + userId);
		query.setOrdering("timestamp desc");
		query.setRange(messageLimitBegin, messageLimitEnd); 

		List<MessageIndexEntity> userIndexes = (List<MessageIndexEntity>) query.execute();

		ArrayList<MessageEntity> results = new ArrayList<MessageEntity>();

		for (MessageIndexEntity msgIndexEntity : userIndexes)
			results.add(msgIndexEntity.getMessage());
		
				
		return results;
	}
	
	// Renvoie la liste de tous les users
	@ApiMethod(name="listUsers")
	public List<UserEntity> listUsers(@Named("usersLimitBegin") Long usersLimitBegin,@Named("usersLimitEnd") Long usersLimitEnd){
		PersistenceManager mgr = getPersistenceManager();
		
		Query users = mgr.newQuery(UserEntity.class);
		users.setRange(usersLimitBegin, usersLimitEnd);
		
		return (List<UserEntity>)users.execute();
	}
	
	// Ajoute un nouveau message dans le datastore
	@ApiMethod(name = "insertNewMessage")
	public MessageEntity insertNewMessage(@Named("message") String message, @Named("userId") Long userId, @Named("username") String username) {
		MessageEntity me = new MessageEntity(message, userId, username);
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
		
		UserEntity user = mgr.getObjectById(UserEntity.class,userId);
		user.addFollowing(followId);		
		
		
		// Rétroactivité du follow :		
		Query query = mgr.newQuery(MessageIndexEntity.class);

		query.setFilter("userId == " + followId);

		List<MessageIndexEntity> messageIndexEntities = (List<MessageIndexEntity>) query.execute();
		
		for(MessageIndexEntity m : messageIndexEntities){
			if(!m.containsReceiver(userId))
				m.addReceiver(userId);
		}
		
		mgr.close();
		return user;
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
	
	// Créé nbUsers dans le datastore (username : usernameRange + indice)
	@ApiMethod(name = "createXUsers")
	public UserEntity createXUsers(@Named("nbUsers") Long nbUsers, @Named("usernameRange") String usernameRange){
		UserEntity e = new UserEntity(usernameRange);
		
		PersistenceManager mgr = getPersistenceManager();
		
		mgr.makePersistent(e);
		
		for(int i=1; i<nbUsers;++i){			
			mgr.makePersistent(new UserEntity(usernameRange+(i+1)));
		}
		
		return e;
	}
	
	// Modifie le user (userId) pour le faire suivre par (userRangeEnd - userRangeBegin) users dans le datastore
	@ApiMethod(name = "followUserRange")
	public UserEntity followUserRange(@Named("userId") Long userId, @Named("userRangeBegin") Long userRangeBegin, @Named("userRangeEnd") Long userRangeEnd) throws Exception{
		PersistenceManager mgr = getPersistenceManager();
		
		UserEntity e = mgr.getObjectById(UserEntity.class,userId);	
		
		Query userQuery = mgr.newQuery(UserEntity.class);
		userQuery.setRange(userRangeBegin, userRangeEnd);
		
		List<UserEntity> users = (List<UserEntity>) userQuery.execute();		
		
		if(users.size()!= (userRangeEnd-userRangeBegin)){
			throw new Exception("Unable to follow : not enough users.");
		}
		else{				
			
			for(UserEntity current : users){
								
				e.addFollower(current.getId());
				
				current.addFollowing(userId);		
				
					
				// On ne s'occupe pas de la rétroactivité pour optimiser le temps d'execution
			}
		}
		
		mgr.close();		
		return e;
	}
	
	// Modifie le user (userId) pour qu'il suive (userRangeEnd - userRangeBegin) users dans le datastore 
	@ApiMethod(name = "followXUsersRange")
	public UserEntity followXUsersRange(@Named("userId") Long userId, @Named("userRangeBegin") Long userRangeBegin, @Named("userRangeEnd") Long userRangeEnd) throws Exception{
		PersistenceManager mgr = getPersistenceManager();
		
		UserEntity e = mgr.getObjectById(UserEntity.class,userId);	
		
		Query userQuery = mgr.newQuery(UserEntity.class);
		userQuery.setRange(userRangeBegin, userRangeEnd);
		
		List<UserEntity> users = (List<UserEntity>) userQuery.execute();		
		
		if(users.size()!= (userRangeEnd-userRangeBegin)){
			throw new Exception("Unable to follow : not enough users.");
		}
		else{						
						
			for(UserEntity current : users){
								
				current.addFollower(userId);
				
				e.addFollowing(current.getId());		
				
				// On ne s'occupe pas de la rétroactivité pour optimiser le temps d'execution
				
			}
		}
		
		mgr.close();		
		return e;
	}
	
	
	private static PersistenceManager getPersistenceManager() {
		return PMF.get().getPersistenceManager();
	}
}
