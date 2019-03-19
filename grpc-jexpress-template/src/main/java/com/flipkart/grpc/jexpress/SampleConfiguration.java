package com.flipkart.grpc.jexpress;

import com.flipkart.gjex.core.GJEXConfiguration;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
public class SampleConfiguration extends GJEXConfiguration {

    @Valid
    @NotNull
    private Database database;

}
