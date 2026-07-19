package nhan.demo.repository.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.NonNull;
import nhan.demo.model.User;
import nhan.demo.util.Gender;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification implements Specification<User> {

    private SpecSearchCriteria specSearchCriteria;

    public UserSpecification(SpecSearchCriteria specSearchCriteria) {
        this.specSearchCriteria = specSearchCriteria;
    }

    @Override
    public Predicate toPredicate(@NonNull final Root<User> root, @NonNull final CriteriaQuery<?> query,@NonNull final CriteriaBuilder criteriaBuilder) {
        Object value = specSearchCriteria.getValue();
        Class<?> javaType = root.get(specSearchCriteria.getKeyword()).getJavaType();

        if (javaType.isEnum() && value instanceof String) {
            value = Enum.valueOf((Class<Enum>) javaType, (String) value);
        }
        return switch (specSearchCriteria.getOperation()){
            case EQUALITY -> criteriaBuilder.equal(root.get(specSearchCriteria.getKeyword()), value);
            case NEGATION -> criteriaBuilder.notEqual(root.get(specSearchCriteria.getKeyword()), value);
            case GREATER_THAN -> criteriaBuilder.greaterThan(root.get(specSearchCriteria.getKeyword()), specSearchCriteria.getValue().toString());
            case LESS_THAN -> criteriaBuilder.lessThan(root.get(specSearchCriteria.getKeyword()), specSearchCriteria.getValue().toString());
            case LIKE -> criteriaBuilder.like(root.get(specSearchCriteria.getKeyword()),"%"+ specSearchCriteria.getValue().toString()+"%");
            case STARTS_WITH -> criteriaBuilder.like(root.get(specSearchCriteria.getKeyword()), specSearchCriteria.getValue().toString()+"%");
            case ENDS_WITH -> criteriaBuilder.like(root.get(specSearchCriteria.getKeyword()), "%"+specSearchCriteria.getValue().toString());
            case CONTAINS -> criteriaBuilder.like(root.get(specSearchCriteria.getKeyword()), "%"+specSearchCriteria.getValue().toString()+"%");
        };
    }
}
