package tinyTwitterAPI;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import java.util.Set;
import java.util.HashSet;

@PersistenceCapable(identityType=IdentityType.APPLICATION)
public class UserEntity {	
	@PrimaryKey
	@Persistent(valueStrategy=IdGeneratorStrategy.IDENTITY) Long idUser;
	
	@Persistent String username;
	
	@Persistent Set<Long> follows = new HashSet<Long>();
	
	public UserEntity() {}
	
	public UserEntity(String username) {
		this.username = username;
		idUser = null;		
	}
	
		
	public Long getId() {
		return idUser;
	}
	
	public void setId(Long id) {
		idUser = id;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String n) {
		username = n;
	}
	
	public void addFollow(Long follow) {
		follows.add(follow);
	}
	
	public void removeFollow(Long follow) {
		follows.remove(follow);
	}
}