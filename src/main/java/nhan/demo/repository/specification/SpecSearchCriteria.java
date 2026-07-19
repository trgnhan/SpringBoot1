package nhan.demo.repository.specification;

import lombok.Getter;

@Getter
public class SpecSearchCriteria {
    private String keyword;
    private SearchOperation operation;
    private Object value;
    private Boolean orPredicate;

    public SpecSearchCriteria(String keyword, SearchOperation operation, Object value) {
        super();
        this.keyword = keyword;
        this.operation = operation;
        this.value = value;
    }

    public SpecSearchCriteria(String keyword, SearchOperation operation, Object value, String orPredicate) {
        super();
        this.keyword = keyword;
        this.operation = operation;
        this.value = value;
        this.orPredicate = orPredicate!=null && orPredicate.equals(SearchOperation.OR_PEDICATE_FLAG);
    }

    public SpecSearchCriteria(String keyword, String operation, String value, String prefix, String suffix) {
    SearchOperation oper = SearchOperation.getSimpleOperation(operation.charAt(0));
    if(oper== SearchOperation.EQUALITY){
        boolean startWithAsterisk = prefix!= null && prefix.contains(SearchOperation.ZERO_OR_MORE_REGEX);
        boolean endWithAsterisk = suffix!= null && suffix.contains(SearchOperation.ZERO_OR_MORE_REGEX);

        if(startWithAsterisk && endWithAsterisk){
            oper = SearchOperation.CONTAINS;
        }else if(startWithAsterisk){
            oper = SearchOperation.ENDS_WITH;
        }else if(endWithAsterisk){
            oper = SearchOperation.STARTS_WITH;
        }

    }
    this.keyword = keyword;
    this.operation = oper;
    this.value = value;

    }
}
