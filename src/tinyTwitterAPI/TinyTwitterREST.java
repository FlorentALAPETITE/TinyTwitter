package tinyTwitterAPI;

import tinyTwitterAPI.PMF;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;



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
//	@ApiMethod(name = "getLastMessagesByUser")
//	public Collection<MessageEntity> getLastMessagesByUser(@Named("userId") Long userId, @Named("limit") int limit) {		
//		Collection<MessageEntity> messages = OfyService.ofy().load().type(MessageEntity.class).filter("userId",userId).limit(limit).list();
//		
//		return messages;
//	}
//	
//	
	@ApiMethod(name = "insertNewMessage")
	public MessageEntity insertNewMessage(@Named("message") String message, @Named("userId") Long userId) {
		MessageEntity me = new MessageEntity(message, userId);
		PersistenceManager mgr = getPersistenceManager();
		mgr.makePersistent(me);
		return me;
	}
	
	@ApiMethod(name = "insertNewUser")
	public UserEntity insertNewUser(@Named("username") String username) {
		UserEntity e = new UserEntity(username);
		PersistenceManager mgr = getPersistenceManager();
		mgr.makePersistent(e);
		return e;
	}
//	
//	@ApiMethod(name = "addFollow")
//	public UserEntity addFollow(@Named("userId") Long userId, @Named("followId") Long follow) {
//		UserEntity e = OfyService.ofy().load().type(UserEntity.class).id(userId).now();
//		
//		e.addFollow(follow);
//		
//		OfyService.ofy().save().entity(e);
//		return e;
//	}
	
	private static PersistenceManager getPersistenceManager() {
		return PMF.get().getPersistenceManager();
	}
}
