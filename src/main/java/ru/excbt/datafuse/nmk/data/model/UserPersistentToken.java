package ru.excbt.datafuse.nmk.data.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * Persistent tokens are used by Spring Security to automatically log in users.
 *
 * @see ru.excbt.datafuse.nmk.security.CustomPersistentRememberMeServices
 */
@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL ,name = "user_persistent_token")
@Cache(usage = CacheConcurrencyStrategy.NONE)
public class UserPersistentToken implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final int MAX_USER_AGENT_LEN = 255;

    @Id
    @Getter
    @Setter
    private String series;

    @JsonIgnore
    @NotNull
    @Column(name = "token_value", nullable = false)
    @Getter
    @Setter
    private String tokenValue;

    @Column(name = "token_date")
    @Getter
    @Setter
    private LocalDate tokenDate;

    //an IPV6 address max length is 39 characters
    @Size(min = 0, max = 39)
    @Column(name = "ip_address", length = 39)
    @Getter
    @Setter
    private String ipAddress;

    @Getter
    @Column(name = "user_agent")
    private String userAgent;

    @JsonIgnore
    @Column(name = "user_id")
    @Getter
    @Setter
    private Long userId;


    public void setUserAgent(String userAgent) {
        if (userAgent.length() >= MAX_USER_AGENT_LEN) {
            this.userAgent = userAgent.substring(0, MAX_USER_AGENT_LEN - 1);
        } else {
            this.userAgent = userAgent;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        UserPersistentToken that = (UserPersistentToken) o;

        if (!series.equals(that.series)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return series.hashCode();
    }

    @Override
    public String toString() {
        return "PersistentToken{" +
            "series='" + series + '\'' +
            ", tokenValue='" + tokenValue + '\'' +
            ", tokenDate=" + tokenDate +
            ", ipAddress='" + ipAddress + '\'' +
            ", userAgent='" + userAgent + '\'' +
            "}";
    }
}
