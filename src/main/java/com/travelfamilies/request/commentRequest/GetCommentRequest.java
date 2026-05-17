package com.travelfamilies.request.commentRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetCommentRequest {

    private int targetType;

    @JsonSerialize(using = ToStringSerializer.class)
    private long targetId;

    private int requestPage;

    private int requestNum;

}
