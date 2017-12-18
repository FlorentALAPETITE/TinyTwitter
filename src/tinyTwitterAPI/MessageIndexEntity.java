package tinyTwitterAPI;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable(identityType=IdentityType.APPLICATION)
public class MessageIndexEntity {
	@PrimaryKey
	@Persistent(valueStrategy=IdGeneratorStrategy.IDENTITY) Long indiceIndexEntity;
	
	@Persistent MessageEntity messageEntity;
	@Persistent Set<Long> receivers = new HashSet<Long>();
	
	public MessageIndexEntity(MessageEntity m){
		messageEntity = m;
		this.indiceIndexEntity=null;
	}
	
	public void addReceiver(Long idReceiver){
		receivers.add(idReceiver);
	}
	
	public void removeReceiver(Long idReceiver){
		receivers.remove(idReceiver);
	}
	
	public void addAllReceivers(Collection<Long> c){
		receivers.addAll(c);
	}
	
	public MessageEntity getMessage(){
		return messageEntity;
	}
}
