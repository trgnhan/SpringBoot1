package nhan.demo.repository.criteria;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SearchCriteria {

    private String key;     // first name , last name, id, email, phone, ...
    private String operator;    // : , < , >
    private Object value;


}
