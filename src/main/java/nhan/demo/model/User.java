package nhan.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import nhan.demo.util.Gender;
import nhan.demo.util.UserStatus;
import nhan.demo.util.UserType;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "User")
@Table(name ="tbl_user")
public class User extends AbstractEntity {
    // abstractEntity đã có r
//    @Id
//    private int id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "date_of_birth")
    @Temporal(TemporalType.DATE)
    private Date dateOfBirth;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "gender")
    private Gender gender;

    @Column(name = "phone")
    private String phone;

    @Column(name = "email")
    private String email;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;


    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "type")
    private UserType type;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "status")
    private UserStatus status;

    private Integer age;

    private Boolean activated;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "user")
    @Builder.Default
    private Set<Address> address = new HashSet<>();

    public void saveAddress(Address addresses) {
        if (addresses != null) {
            if (address == null) {
                address = new HashSet<>();
            }
            address.add(addresses);
            addresses.setUser(this);
        }
    }

    // abstractEntity đã có r
//    @Column(name = "created_at")
//    @UpdateTimestamp
//    @Temporal(TemporalType.TIMESTAMP)
//    private Date createdAt;
//
//    @Column(name = "update_at")
//    @CreationTimestamp
//    @Temporal(TemporalType.TIMESTAMP)
//    private Date updateAt;


}
