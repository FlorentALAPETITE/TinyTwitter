package tinyTwitterAPI;

import java.sql.Timestamp;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Index;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable(identityType=IdentityType.APPLICATION)
public class MessageEntity {
	@PrimaryKey
	@Persistent(valueStrategy=IdGeneratorStrategy.IDENTITY) Key idMessage;
	
	@Persistent String message;	
	
	// On conserve l'ID et l'username pour éviter les queries superflues (getUsername par exemple)
	// --> Perte d'espace mais gain de temps d'execution
	@Persistent String username;	
	@Persistent Long userId;
	
	
	public MessageEntity() {}
	
	public MessageEntity(String message, Long userId, String username) {
		this.message=message;		
		
		this.idMessage = null;
		this.userId = userId;
		this.username=username;
	}
	
	public Key getIdMessage() {
		return idMessage;
	}
	
	public void setIdMessage(Key id) {
		idMessage = id;
	}
	
	
	public String getMessage() {
		return message;		
	}
	
	public void setMessage(String m) {
		message=m;
	}
	
	public String getUsername() {
		return username;		
	}
	
	public void setUsername(String u) {
		username=u;
	}
	
	public Long getUserId() {
		return userId;		
	}
	
	public void setUserId(Long id) {
		userId=id;
	}
		
	
	
}
