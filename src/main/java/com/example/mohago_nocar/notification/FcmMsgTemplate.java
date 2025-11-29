package com.example.mohago_nocar.notification;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Map;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.SIMPLE_NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = TravelCourseMsgTemplate.class,name = "travelCourseFcmMsg")
})
@AllArgsConstructor
@ToString
@Getter
@NoArgsConstructor
public abstract class FcmMsgTemplate {

    String title;

    String body;

    abstract Map<String,String> getAllCustomData();

}
