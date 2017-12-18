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
	@Index @Persistent Long userId;
	@Index @Persistent String timestamp;
	
	public MessageEntity() {}
	
	public MessageEntity(String message, Long userId) {
		this.message=message;
		this.userId=userId;
		
		timestamp = new Timestamp(System.currentTimeMillis()).toString();
		
		this.idMessage = null;
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
	
	
	public Long getUser(){
		return userId;
	}
	
	public void setUser(Long k) {
		userId=k;
	}
	
	
	public void setTimestamp(String t) {
		timestamp = t;
	}
	
	public String getTimestamp() {
		return timestamp;
	}
}
