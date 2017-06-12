package se.claremont.backend.user.repository.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="users")
public class User {

	@Id
	@Column(name="id")
	@GeneratedValue (strategy= GenerationType.SEQUENCE, generator="users_id_seq")
	@SequenceGenerator(name = "users_id_seq", sequenceName = "users_id_seq")
	private Long id;
	
	@Column(name="username")
	private String username;

	public User() {}

	public User(String username) {
	    this.username = username;
    }
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
}
