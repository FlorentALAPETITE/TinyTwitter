package tinyTwitterAPI;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Index;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.annotations.Unique;

import java.util.Set;
import java.util.HashSet;

@PersistenceCapable(identityType=IdentityType.APPLICATION)
@Unique(name="Unique_Username", members={"username"})
public class UserEntity {	
	@PrimaryKey
	@Persistent(valueStrategy=IdGeneratorStrategy.IDENTITY) Long idUser;
	
	@Index @Persistent String username;
	
	// On conserve les followers du user pour mettre à jour les receivers en cas d'écriture de message
	@Persistent Set<Long> followers = new HashSet<Long>();
	
	// On conserve les personnes follows par le user pour éviter les requêtes superflues côté client (recherche de toutes les personnes que le user follow)
	@Persistent Set<Long> following = new HashSet<Long>();
	
	public UserEntity() {}
	
	public UserEntity(String username) {
		this.username = username;
		idUser = null;		
	}
	
	public Long getIdUser() {
		return idUser;
	}

	public void setIdUser(Long idUser) {
		this.idUser = idUser;
	}

	public void setFollowers(Set<Long> followers) {
		this.followers = followers;
	}

	public void setFollowing(Set<Long> following) {
		this.following = following;
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
	
	
	public void addFollower(Long follower) {
		followers.add(follower);
	}
	
	public Set<Long> getFollowers(){
		return followers;
	}
	
	public void removeFollower(Long follower) {
		followers.remove(follower);
	}
	
	public void addFollowing(Long follow) {
		following.add(follow);
	}
	
	public Set<Long> getFollowing(){
		return following;
	}
	
}