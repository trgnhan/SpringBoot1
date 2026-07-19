package nhan.demo.repository.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import nhan.demo.model.User;
import nhan.demo.util.Gender;
import org.springframework.data.jpa.domain.Specification;

public class UserSpec{
    public static Specification<User> hasFirstName(String firstName){
       return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("firstName"), "%"+firstName+"%");
    }

    public static Specification<User> notEqualGender(Gender gender){
        return ((root, query, criteriaBuilder) -> criteriaBuilder.notEqual(root.get("gender"), gender));
    }
}
