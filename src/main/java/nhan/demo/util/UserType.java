package nhan.demo.util;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum UserType {
    @JsonProperty("ower")
    OWER,
    @JsonProperty("admin")
    ADMIN,
    @JsonProperty("user")
    USER
}
