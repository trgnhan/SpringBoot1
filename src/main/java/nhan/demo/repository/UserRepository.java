package nhan.demo.repository;

import nhan.demo.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User,Long>, JpaSpecificationExecutor<User> {

    // -- Tu join --
    // @Query(value = "select * from User u inner join Adress a on u.id = a.userId where a.city=:city")
    // List<User> getAllUser(String city);

//  ---- Query Methods ----
    // -- Distinct --
    //muon phan trang tra ve page
    //Page<User> findDistinctByFirstNameAndLastName();
    // ben duoi se giong ben tren -> @Query(value = "select distinct from User u where u.firstName=:firstName and u.lastName=:lastName")
    //neu k muon phan trang thi tra ve List
    List<User> findDistinctByFirstNameAndLastName(String firstName, String lastName);

    // -- Single field --
    // ben duoi se giong ben tren -> @Query(value = "select * from User u where u.email=:email")
    List<User> findByEmail(String email);

    // -- Or --
    // ben duoi se giong ben tren -> @Query(value = "select * from User u where u.firstName=:name or u.lastName=:name")
    List<User> findByFirstNameOrLastName(String name);

    // -- Is, Equals --
    // @Query(value = "select * from User u where u.firstName=:name")

    List<User> findByFirstNameIs(String name);
    List<User> findByFirstNameEquals(String name);
    List<User> findByFirstName(String name);

    // -- Between --
    // @Query(value = "select * from User u where u.createdAt=:createdAt between ?1 and 2?")

    List<User>  findByCreatedAtBetween(Date start, Date end);

    // -- Less than--
    // @Query(value = "select * from User u where u.age<:age")

    List<User> findByAgeLessThan(int age);
    List<User> findByAgeLessThanEqual(int age);
    // @Query(value = "select * from User u where u.age>:age")

    List<User> findByAgeGreaterThan(int age);
    List<User> findByAgeGreaterThanEqual(int age);

    // -- Before and After--
    // @Query(value = "select * from User u where u.createdAt<:beforeDay")

    List<User> findByCreatedAtBefore(Date beforeDay);
    // @Query(value = "select * from User u where u.createdAt>:afterDay")
    List<User> findByCreatedAtAfter(Date afterDay);

    // -- IsNull and IsNotNull --
    // @Query(value = "select * from User u where u.age is null")
    List<User> findByAgeIsNull();
    // @Query(value = "select * from User u where u.age is not null")
    List<User> findByAgeIsNotNull();

    // -- Like --
    // @Query(value = "select * from User u where u.firstName like %:firstName%")
    // phai them "%" + firstName + "%"
    List<User> findByFirstNameLike(String firstName);
    // tuong tu NotLike
    List<User> findByFirstNameNotLike(String firstName);

    // -- StartingWith
    // @Query(value = "select * from User u where u.firstName like :firstName%")
    List<User> findByFirstNameStartingWith(String firstName);

    // -- EndingWith
    // @Query(value = "select * from User u where u.firstName like %:firstName")
    List<User> findByFirstNameEndingWith(String firstName);

    // -- Containing
    // @Query(value = "select * from User u where u.firstName like %:firstName%")
    // giong like
    List<User> findByFirstNameContaining(String firstName);

    // -- Not --
    // @Query(value = "select * from User u where u.firstName <> :firstName")
    List<User> findByFirstNameNot(String firstName);

    // -- In --
    // @Query(value = "select * from User u where u.age in (18,25,30)")
    List<User> findByAgeIn(Collection<Integer> ages);

    // -- NotIn --
    // @Query(value = "select * from User u where u.age not in (18,25,30)")
    List<User> findByAgeNotIn(Collection<Integer> ages);

    // -- True/False --
    // @Query(value = "select * from User u where u.activated = true") / false
    List<User> findByActivatedTrue();
    List<User> findByActivatedFalse();
    // tuy theo ben trong no se lay ra true/false
    List<User> findByActivated(Boolean activated);

    // -- IgnoreCase --
    // @Query(value = "select * from User u where LOWER(u.firstName) =:LOWER(firstName)")
    List<User> findByFirstNameIgnoreCase(String firstName);


    // -- OrderBy --
    //DESC
    List<User> findByFirstNameOrderByCreatedAtDesc(String firstName);

    //
    List<User> findByFirstNameAndLastNameIgnoreCase(String firstName, String lastName);

}
