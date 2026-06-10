package nhan.demo.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import nhan.demo.dto.response.PageResponse;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Repository
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
}
