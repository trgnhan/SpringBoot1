package nhan.demo.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.*;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import nhan.demo.dto.response.PageResponse;
import nhan.demo.model.Address;
import nhan.demo.model.User;
import nhan.demo.repository.criteria.SearchCriteria;
import nhan.demo.repository.criteria.UserSearchCriteriaQueryConsumer;
import nhan.demo.repository.specification.SpecSearchCriteria;
import nhan.demo.util.Gender;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Repository
@Slf4j
public class SearchRepository {

    @PersistenceContext
    private EntityManager entityManager;


    public PageResponse<?> getAllUsersWithSortByColumnsAndSearch(int pageNo, int pageSize, String search, String sorts) {

//        StringBuilder sqlQuery = new StringBuilder("select u from User u where 1=1");
        StringBuilder sqlQuery = new StringBuilder("select new nhan.demo.dto.response.UserDetailResponse(u.id, u.firstName, u.lastName, u.phone, u.email) from User u where 1=1");

        if(StringUtils.hasLength(search)) {
            sqlQuery.append(" and lower(u.firstName) like lower(:firstName)");
            sqlQuery.append(" or lower(u.lastName) like lower(:lastName)");
            sqlQuery.append(" or lower(u.email) like lower(:email)");

        }

        if (StringUtils.hasLength(sorts)) {
            if(StringUtils.hasLength(sorts)){
                // firstName: asc|desc
                Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)");
                Matcher matcher = pattern.matcher(sorts);
                if(matcher.find()){
                    sqlQuery.append(String.format(" order by u.%s %s", matcher.group(1), matcher.group(3)));
                }

            }
        }



        Query selectQuery = entityManager.createQuery(sqlQuery.toString());
        selectQuery.setFirstResult(pageNo);
        selectQuery.setMaxResults(pageSize);
        if(StringUtils.hasLength(search)) {
            selectQuery.setParameter("firstName", "%"+search+"%" );
            selectQuery.setParameter("lastName", "%"+search+"%" );
            selectQuery.setParameter("email", "%"+search+"%" );
        }

        List users = selectQuery.getResultList();
    //    users.stream().map()


        System.out.println(users);
        //query ra list user

        //query ra so record
        StringBuilder sqlCountQuery = new StringBuilder("select count(*) from User u where 1=1");
        if(StringUtils.hasLength(search)) {
            sqlCountQuery.append(" and lower(u.firstName) like lower(?1)");
            sqlCountQuery.append(" or lower(u.lastName) like lower(?2)");
            sqlCountQuery.append(" or lower(u.email) like lower(?3)");
        }
        Query selectCountQuery = entityManager.createQuery(sqlCountQuery.toString());
        if(StringUtils.hasLength(search)) {
            selectCountQuery.setParameter(1, "%"+search+"%" );
            selectCountQuery.setParameter(2, "%"+search+"%" );
            selectCountQuery.setParameter(3, "%"+search+"%" );
        }
        Long totalElements = (Long) selectCountQuery.getSingleResult();
        System.out.println(totalElements);




        Page<?> page = new PageImpl<Object>(users, PageRequest.of(pageNo,pageSize), totalElements);
        return PageResponse.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPages(page.getTotalPages())
                .items(page.stream().toList())
                .build();
    }

    public PageResponse<?> advanceSearchByCriteria(int pageNo, int pageSize, String sortBy, String address,String... search) {

        List<SearchCriteria> criteriaList = new ArrayList<>();

        // lay ra ds user
        if(search!=null){
            for (String s : search) {
                Pattern pattern = Pattern.compile("(\\w+?)(:|>|<)(.*)");
                Matcher matcher = pattern.matcher(s);
                if(matcher.find()){
                    criteriaList.add(new SearchCriteria(matcher.group(1),matcher.group(2),matcher.group(3)));
                }
            }
        }


        // lay ra so luong ban ghi // phan trang
        List<User> users = getUsers(pageNo, pageSize, criteriaList, sortBy, address);

        Long totalElements= getTotalElements(criteriaList, address);

        return PageResponse.builder()
                .pageNo(pageNo) // offset = vi tri ban ghi trong danh sach
                .pageSize(pageSize) //
                .totalPages(totalElements.intValue()) // total elements
                .items(users)
                .build();
    }

    private Long getTotalElements(List<SearchCriteria> criteriaList, String address) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = criteriaBuilder.createQuery(Long.class);
        Root<User> root = query.from(User.class);

        // Xu ly cac dieu kien tim kiem
        Predicate predicate = criteriaBuilder.conjunction();
        UserSearchCriteriaQueryConsumer searchConsumer = new UserSearchCriteriaQueryConsumer(criteriaBuilder,predicate,root);

        if(StringUtils.hasLength(address)){
            Join<Address,User> addressJoin = root.join("address");
            Predicate addressPredicate = criteriaBuilder.like(addressJoin.get("city"), "%"+address+"%");
            query.select(criteriaBuilder.count(root));
            query.where(predicate,addressPredicate);
        }else{
            criteriaList.forEach(searchConsumer);
            predicate = searchConsumer.getPredicate();
            query.select(criteriaBuilder.count(root));
            query.where(predicate);
        }
        return entityManager.createQuery(query).getSingleResult();
    }

    private List<User> getUsers(int pageNo, int pageSize, List<SearchCriteria> criteriaList, String sortBy, String address) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> query = criteriaBuilder.createQuery(User.class);
        Root<User> root = query.from(User.class);

        // Xu ly cac dieu kien tim kiem
        Predicate predicate = criteriaBuilder.conjunction();
        UserSearchCriteriaQueryConsumer queryConsumer = new UserSearchCriteriaQueryConsumer(criteriaBuilder,predicate,root);

        if(StringUtils.hasLength(address)){
            Join<Address,User> addressJoin = root.join("address");
            Predicate addressPredicate = criteriaBuilder.like(addressJoin.get("city"), "%"+address+"%");
            query.where(predicate,addressPredicate);
        }else{
            criteriaList.forEach(queryConsumer);
            predicate = queryConsumer.getPredicate();
            query.where(predicate);
        }
        //sort
        if(StringUtils.hasLength(sortBy)){
            Pattern pattern = Pattern.compile("(\\w+?)(:)(asc|desc)");
            Matcher matcher = pattern.matcher(sortBy);
            if(matcher.find()){
                String column = matcher.group(1);
                if(matcher.group(3).equalsIgnoreCase("desc")){
                    query.orderBy(criteriaBuilder.desc(root.get(column)));
                }else {
                    query.orderBy(criteriaBuilder.asc(root.get(column)));
                }
            }
        }

        return entityManager.createQuery(query).setFirstResult(pageNo).setMaxResults(pageSize).getResultList();
    }
    public PageResponse getUsersJoinedAddress(Pageable pageable, String[] user, String[] address) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> query = criteriaBuilder.createQuery(User.class);
        Root<User> userRoot = query.from(User.class);

        Join<Address,User> addressJoin = userRoot.join("address");

        //build query
        List<Predicate> userPre = new ArrayList<>();
        List<Predicate> addressPre = new ArrayList<>();

        Pattern pattern = Pattern.compile("(\\w+?)([<:>~!])(.*)(\\p{Punct}?)(\\p{Punct}?)");
        for (String u : user) {
            Matcher matcher = pattern.matcher(u);
            if(matcher.find()){
                SpecSearchCriteria criteria = new SpecSearchCriteria(matcher.group(1), matcher.group(2),matcher.group(3),matcher.group(4),matcher.group(5));
                Predicate predicate = toUserPredicate(userRoot, criteriaBuilder ,criteria);
                if(userPre!=null) userPre.add(predicate);
            }
        }
        for (String a : address) {
            Matcher matcher = pattern.matcher(a);
            if(matcher.find()){
                SpecSearchCriteria criteria = new SpecSearchCriteria(matcher.group(1), matcher.group(2),matcher.group(3),matcher.group(4),matcher.group(5));
                Predicate predicate = toAddressPredicate(addressJoin, criteriaBuilder ,criteria);
                if(addressPre!=null) addressPre.add(predicate);
            }
        }

        Predicate userPredicate = criteriaBuilder.or(userPre.toArray(new Predicate[userPre.size()]));
        Predicate addressPredicate = criteriaBuilder.or(addressPre.toArray(new Predicate[addressPre.size()]));
        Predicate finalPredicate = criteriaBuilder.and(userPredicate, addressPredicate);

        query.where(finalPredicate);
        List<User> resultList = entityManager.createQuery(query)
                .setFirstResult(pageable.getPageNumber())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        long count = count(user,address);


        return PageResponse.builder()
                .pageNo(pageable.getPageNumber())
                .pageSize(pageable.getPageSize())
                .totalPages(count)
                .items(resultList)
                .build();
    }

    private long count(String[] user, String[] address ) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = criteriaBuilder.createQuery(Long.class);
        Root<User> userRoot = query.from(User.class);

        Join<Address,User> addressJoin = userRoot.join("address");

        //build query
        List<Predicate> userPre = new ArrayList<>();
        List<Predicate> addressPre = new ArrayList<>();

        Pattern pattern = Pattern.compile("(\\w+?)([<:>~!])(.*)(\\p{Punct}?)(\\p{Punct}?)");
        for (String u : user) {
            Matcher matcher = pattern.matcher(u);
            if(matcher.find()){
                SpecSearchCriteria criteria = new SpecSearchCriteria(matcher.group(1), matcher.group(2),matcher.group(3),matcher.group(4),matcher.group(5));
                Predicate predicate = toUserPredicate(userRoot, criteriaBuilder ,criteria);
                if(userPre!=null) userPre.add(predicate);
            }
        }
        for (String a : address) {
            Matcher matcher = pattern.matcher(a);
            if(matcher.find()){
                SpecSearchCriteria criteria = new SpecSearchCriteria(matcher.group(1), matcher.group(2),matcher.group(3),matcher.group(4),matcher.group(5));
                Predicate predicate = toAddressPredicate(addressJoin, criteriaBuilder ,criteria);
                if(addressPre!=null) addressPre.add(predicate);
            }
        }

        Predicate userPredicate = criteriaBuilder.or(userPre.toArray(new Predicate[userPre.size()]));
        Predicate addressPredicate = criteriaBuilder.or(addressPre.toArray(new Predicate[addressPre.size()]));
        Predicate finalPredicate = criteriaBuilder.and(userPredicate, addressPredicate);

        query.select(criteriaBuilder.count(userRoot));
        query.where(finalPredicate);
        return entityManager.createQuery(query).getSingleResult();

    }

    private Predicate toUserPredicate(@NonNull final Root<User> root, @NonNull final CriteriaBuilder criteriaBuilder, SpecSearchCriteria specSearchCriteria) {
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
    private Predicate toAddressPredicate(@NonNull final Join<Address,User> root, @NonNull final CriteriaBuilder criteriaBuilder, SpecSearchCriteria specSearchCriteria) {
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
