package com.bmw.remotecollab.admin.model;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@DynamoDBTable(tableName = "sessionRoom")
@Getter
@Setter
@ToString
public class Recording {

    @DynamoDBHashKey
    @DynamoDBAutoGeneratedKey
    private String id;

    @DynamoDBAttribute(attributeName = "recordingId")
    private String recordingId;

    @DynamoDBAttribute(attributeName = "createdOn")
    @DynamoDBAutoGeneratedTimestamp(strategy=DynamoDBAutoGenerateStrategy.CREATE)
    private Date createdOn;

    @DynamoDBAttribute(attributeName = "stopped")
    private Date stopped;

}
