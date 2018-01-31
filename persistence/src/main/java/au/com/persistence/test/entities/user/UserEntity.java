package au.com.persistence.test.entities.user;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "USER_POC")
@NamedQueries({
        @NamedQuery(name = "user.findByLogin", query = "SELECT u FROM UserEntity u WHERE u.login = :login")
})
@SequenceGenerator(sequenceName = "USER_POC_SEQ", allocationSize = 1, name = "POC_SEQ")
public class UserEntity implements Serializable {
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "POC_SEQ")
    @Column(name = "ID", nullable = false)
    private int id;

    @Basic
    @Column(name = "LOGIN", nullable = true, length = 100)
    private String login;

    @Basic
    @Column(name = "PASSWORD", nullable = true, length = 100)
    private String password;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserEntity that = (UserEntity) o;

        if (id != that.id) return false;
        if (login != null ? !login.equals(that.login) : that.login != null) return false;
        if (password != null ? !password.equals(that.password) : that.password != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (login != null ? login.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        return result;
    }
}
