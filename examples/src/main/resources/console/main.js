var host = location.host;
$(document).ready(function() {
    var createSchemaEditor;
    var createSchemaReferenceEditor;
    var viewEditor;
    var viewSchemaReferenceEditor;
    var updateSchemaEditor;
    var updateSchemaReferenceEditor;
    var readVersion; //this is used as check and put for edit schema operation
    var updateSchemaId; //this is the schemaId for which we will do the update
    var compareSchemaEditor;
    var compareSchemaReferenceEditor;
    var createEntityEditor;
    var viewEntityEditor;
    var createIndexEditor;
    var viewIndexEditor;
    var editIndexEditor;
    $('#createSchemaSuccessResponse').hide();
    $('#createSchemaErrorResponse').hide();
    $('#editSchemaSuccessResponse').hide();
    $('#editSchemaErrorResponse').hide();
    $('#createEntitySuccessResponse').hide();
    $('#createEntityErrorResponse').hide();
    $('#createIndexSuccessResponse').hide();
    $('#createIndexErrorResponse').hide();
    $('#editIndexSuccessResponse').hide();
    $('#editIndexErrorResponse').hide();
    $("#treeViewSpinner").hide();
    $("form").submit(function(e) {
        e.preventDefault();
    });
    //jquery for toggle sub menus
    $('.sub-btn').click(function() {
        $(this).next('.sub-menu').slideToggle();
        $(this).find('.dropdown').toggleClass('rotate');
    });

    //jquery for expand and collapse the sidebar
    $('.menu-btn').click(function() {
        $('.side-bar').addClass('active');
        $('.menu-btn').css("visibility", "hidden");
    });

    $('.close-btn').click(function() {
        $('.side-bar').removeClass('active');
        $('.menu-btn').css("visibility", "visible");
    });

    $('.sub-item').click(function() {
        console.log("Subitemclicked:" + $(this).text());
        $('#mainHeading').hide();
        if ($(this).attr('id') === "createSchemaSubMenu") {
            $('.userInput').hide();
            $('#createSchema').show();
            if (createSchemaEditor == null) {
                require.config({
                    paths: {
                        vs: 'min/vs'
                    }
                });
                var nestedMessageSchema = "syntax = \"proto3\";package com.flipkart.oms;import \"com.flipkart.oms.CancelMeta.proto\";import \"com.flipkart.oms.CreateMeta.proto\";import \"com.flipkart.oms.ReturnMeta.proto\";import \"com.flipkart.oms.ChangeAddressMeta.proto\";import \"com.flipkart.oms.ChangePaymentMeta.proto\";import \"com.flipkart.oms.ChangeSlotMeta.proto\";import \"com.flipkart.oms.ProcessRefundMeta.proto\";import \"com.flipkart.oms.ReviseMeta.proto\";import \"com.flipkart.oms.DefaultChoreMeta.proto\";import \"com.flipkart.oms.PriceRevisionMeta.proto\";import \"com.flipkart.oms.PriceChangeMeta.proto\";import \"com.flipkart.oms.ReattemptChangeMeta.proto\";import \"com.flipkart.oms.FeeWaiverMeta.proto\";import \"com.flipkart.oms.RoundingOffMeta.proto\";import \"com.flipkart.oms.AddFreebieMeta.proto\";import \"com.flipkart.oms.AddReplenishmentMeta.proto\";import \"com.flipkart.oms.ChangeSlaMeta.proto\";import \"com.flipkart.oms.MiscellaneousChangeMeta.proto\";import \"com.flipkart.oms.OldChangeSlotMeta.proto\";import \"com.flipkart.oms.ProcessReplacementMeta.proto\";import \"com.flipkart.oms.PaymentConfirmationMeta.proto\";option java_outer_classname = \"DefaultChangeOrderProto\";message DefaultChangeOrder {  string chore_id = 1;  string unit_id = 2;  string status = 3;  int64 created_at = 4;  int64 updated_at = 5;  string chore_type = 6;  string flow_type = 7;  ChangeOrderMeta chore_meta = 8;  repeated StatusHistory status_histories = 9;  ChangeOrderContext change_order_context = 10;  message ChangeOrderMeta{    oneof meta {      CancelMeta cancel_meta = 1;      ReturnMeta return_meta = 2;      CreateMeta create_meta = 3;      ChangeAddressMeta change_address_meta = 4;      ChangePaymentMeta change_payment_meta = 5;      ChangeSlotMeta change_slot_meta = 6;      ProcessRefundMeta process_refund_meta = 7;      ReviseMeta revise_meta = 8;      PriceRevisionMeta price_revision_meta = 9;      DefaultChoreMeta default_chore_meta = 10;      PriceChangeMeta price_change_meta = 11;      ReattemptChangeMeta reattempt_change_meta = 12;      FeeWaiverMeta fee_waiver_meta = 13;      RoundingOffMeta rounding_off_meta = 14;      AddFreebieMeta add_freebie_meta = 15;      AddReplenishmentMeta add_replenishment_meta = 16;      ChangeSlaMeta change_sla_meta = 17;      MiscellaneousChangeMeta miscellaneous_change_meta = 18;      ProcessReplacementMeta process_replacement_meta = 19;      OldChangeSlotMeta old_change_slot_meta = 20;      PaymentConfirmationMeta payment_confirmation_meta = 21;    }  }  message StatusHistory {    string status = 1;    string action = 2;    string user_login = 3;    string reason = 4;    string sub_reason = 5;    int64 created_at = 6;  }  message ChangeOrderContext{      int32 aggregation_count = 1;      string callback_id = 2;      bool cod_to_prepaid_new_flow = 3;      bool backward = 4;      bool repickup = 5;      bool saal = 6;      bool cod_to_prepaid_failed = 7;      bool call_backend = 8;      repeated ServicePolicy service_policies = 9;      repeated string order_adjustment_types = 10;      string reason = 11;      string sub_reason = 12;      string comments = 13;      bool status_override = 14;      bool create_action = 15;    }    message ServicePolicy{      repeated string service_outcomes = 1;      string next_service = 2;      repeated ServicePolicy next_service_policies = 3;    }}import \"com.flipkart.entitymanager.schemaregistry.protobuf.SchemaOptions.proto\";option (com.flipkart.entitymanager.schemaregistry.protobuf.schema_id) = \"SCH_5Z61622818098295\";option (com.flipkart.entitymanager.schemaregistry.protobuf.schema_version) = \"1.0\";";
                require(['vs/editor/editor.main'], function() {
                    createSchemaEditor = monaco.editor.create(document.getElementById('createSchemaData'), {
                        value: formatProtoSchema(nestedMessageSchema),
                        language: 'proto',
                        fontSize: "13px"
                    });
                });
            }
            if (createSchemaReferenceEditor == null) {
                require.config({
                    paths: {
                        vs: 'min/vs'
                    }
                });
                require(['vs/editor/editor.main'], function() {
                    createSchemaReferenceEditor = monaco.editor.create(document.getElementById('createSchemaReference'), {
                        value: "",
                        language: 'javascript',
                        fontSize: "13px"
                    });
                });
            }

        }
        if ($(this).attr('id') === "viewSchemaSubMenu") {
            $('.userInput').hide();
            $('#viewSchema').show();
            $('#viewVersion').prop('disabled', false);
            if (viewEditor == null) {
                require.config({
                    paths: {
                        vs: 'min/vs'
                    }
                });
                require(['vs/editor/editor.main'], function() {
                    viewEditor = monaco.editor.create(document.getElementById('viewSchemaData'), {
                        value: "",
                        language: 'proto',
                        readOnly: false,
                        fontSize: "13px"
                    });
                });
            }
            if (viewSchemaReferenceEditor == null) {
                require.config({
                    paths: {
                        vs: 'min/vs'
                    }
                });
                require(['vs/editor/editor.main'], function() {
                    viewSchemaReferenceEditor = monaco.editor.create(document.getElementById('viewSchemaReference'), {
                        value: "",
                        language: 'javascript',
                        readOnly: true,
                        fontSize: "13px"
                    });
                });
            }
        }
        if ($(this).attr('id') === "compareSchemaSubMenu") {
            $('.userInput').hide();
            $('#compareSchema').show();
            if (compareSchemaEditor == null) {
                require.config({
                    paths: {
                        vs: 'min/vs'
                    }
                });
                require(['vs/editor/editor.main'], function() {
                    compareSchemaEditor = monaco.editor.createDiffEditor(document.getElementById('compareSchemaData'));
                });
            }
            if (compareSchemaReferenceEditor == null) {
                require.config({
                    paths: {
                        vs: 'min/vs'
                    }
                });
                require(['vs/editor/editor.main'], function() {
                    compareSchemaReferenceEditor = monaco.editor.createDiffEditor(document.getElementById('compareSchemaReference'));
                });
            }
        }
        if ($(this).attr('id') === "editSchemaSubMenu") {
            $('.userInput').hide();
            $('#editSchema').show();
            if (updateSchemaEditor == null) {
                require.config({
                    paths: {
                        vs: 'min/vs'
                    }
                });
                require(['vs/editor/editor.main'], function() {
                    updateSchemaEditor = monaco.editor.create(document.getElementById('editSchemaData'), {
                        value: "",
                        language: 'proto',
                        fontSize: "13px"

                    });
                });
            }
            if (updateSchemaReferenceEditor == null) {
                require.config({
                    paths: {
                        vs: 'min/vs'
                    }
                });
                require(['vs/editor/editor.main'], function() {
                    updateSchemaReferenceEditor = monaco.editor.create(document.getElementById('editSchemaReference'), {
                        value: "",
                        language: 'javascript',
                        fontSize: "13px"

                    });
                });

            }
        }
        if ($(this).attr('id') === "dependencyTreeSubMenu") {
            $('.userInput').hide();
            $('#viewDependencyTree').show();
        }
        if ($(this).attr('id') === "documentationSubMenu") {
            $('.userInput').hide();
            $("#documentation").show();
        }
        if ($(this).attr('id') === "createEntitySubMenu") {
            $('.userInput').hide();
            $('#createEntity').show();
            if (createEntityEditor == null) {
                require.config({
                    paths: {
                        vs: 'min/vs'
                    }
                });
                var createEntityRequest = {
                    "entityIdentifier": {
                        "namespace": "com.flipkart.entitymanager.test",
                        "name": "OrderRoot"
                    },
                    "entityInfoMeta": {
                        "entityType": "ROOT_ENTITY",
                        "schemaId": "SCH_1LE1617871861518",
                        "info": {
                            "primaryKeyMeta": {
                                "primaryKeyField": "order.order_id"
                            },
                            "versionMeta": {
                                "versionField": "order.version"
                            }
                        }
                    }
                };
                require(['vs/editor/editor.main'], function() {
                    createEntityEditor = monaco.editor.create(document.getElementById('createEntityData'), {
                        value: JSON.stringify(createEntityRequest, null, 2),
                        language: 'json',
                        fontSize: "13px"
                    });
                });
            }
        }
        if ($(this).attr('id') === "viewEntitySubMenu") {
            $('.userInput').hide();
            $('#viewEntity').show();
            if (viewEntityEditor == null) {
                require.config({
                    paths: {
                        vs: 'min/vs'
                    }
                });
                require(['vs/editor/editor.main'], function() {
                    viewEntityEditor = monaco.editor.create(document.getElementById('viewEntityData'), {
                        value: "",
                        language: 'json',
                        readOnly: true,
                        fontSize: "13px"
                    });
                });
            }
        }
        if ($(this).attr('id') === "createIndexSubMenu") {
            $('.userInput').hide();
            $('#createIndex').show();
            if (createIndexEditor == null) {
                require.config({
                    paths: {
                        vs: 'min/vs'
                    }
                });
                var createIndexRequest = {
                                           "name": "ACCOUNT_ID_INDEX",
                                           "validationSchemaId": "SCH_1LE1617871861518",
                                           "validationSchemaVersion": "1.0",
                                           "indexKeyInfo": {
                                             "fieldPath": "order.account_id",
                                             "persistenceMode": "IMMEDIATE_SYNC"
                                           },
                                           "indexValueInfos": [
                                             {
                                               "fieldPath": "order.created_at",
                                               "persistenceMode": "PERSIST_SYNC"
                                             },
                                             {
                                               "fieldPath": "order.payment_status",
                                               "persistenceMode": "PERSIST_ASYNC"
                                             },
                                             {
                                               "fieldPath": "order.status",
                                               "persistenceMode": "PERSIST_SYNC"
                                             }
                                           ],
                                           "defaultSortOrder" : {
                                            "sortSpec":{
                                                "fieldKeyPath":"order.created_at",
                                                "sortOrder": "DESC"
                                            }
                                           }
                                         };
                require(['vs/editor/editor.main'], function() {
                    createIndexEditor = monaco.editor.create(document.getElementById('createIndexData'), {
                        value: JSON.stringify(createIndexRequest, null, 2),
                        language: 'json',
                        fontSize: "13px"
                    });
                });
            }
        }
        if ($(this).attr('id') === "viewIndexSubMenu") {
                    $('.userInput').hide();
                    $('#viewIndex').show();
                    if (viewIndexEditor == null) {
                        require.config({
                            paths: {
                                vs: 'min/vs'
                            }
                        });
                        require(['vs/editor/editor.main'], function() {
                            viewIndexEditor = monaco.editor.create(document.getElementById('viewIndexData'), {
                                value: "",
                                language: 'json',
                                readOnly: true,
                                fontSize: "13px"
                            });
                        });
                    }
                }
        if ($(this).attr('id') === "editIndexSubMenu") {
                            $('.userInput').hide();
                            $('#editIndex').show();
                            if (editIndexEditor == null) {
                                require.config({
                                    paths: {
                                        vs: 'min/vs'
                                    }
                                });
                                require(['vs/editor/editor.main'], function() {
                                    editIndexEditor = monaco.editor.create(document.getElementById('editIndexData'), {
                                        value: "",
                                        language: 'json',
                                        fontSize: "13px"
                                    });
                                });
                            }
                        }
    });

    $('#createSchemaButton').click(function() {
        var createSchemaRequest = {
            "schemaIdentifier": {
                "namespace": $("#createSchemaNamespace").val(),
                "name": $("#createSchemaName").val(),
            },
            "schemaVersion": {
                "schemaData": removeLinebreaks(createSchemaEditor.getValue()),
                "schemaReferences": createSchemaReferenceEditor.getValue() === "" ? [] : JSON.parse(removeSpaces(removeLinebreaks(createSchemaReferenceEditor.getValue()))),
            }
        };
        $.ajax({
            type: "POST",
            url: "http://" + host + "/entity-manager/schema/v1/register",
            data: JSON.stringify(createSchemaRequest),
            contentType: "application/json",
            success: function(result, textStatus) {
                console.log("Successfully registered schema:" + textStatus);
                $('#createSchemaSuccessMessage').text("Successfully registered schema");
                $('#createSchemaSuccessResponse').show();
                window.scrollTo(0, 0);
                //$('#schemaOperationsResponseText').text("Successfully registered schema");
                //$('#schemaOperationsResponse').dialog("open");
            },
            error: function(error, textStatus) {
                console.log("Error in registering schema:" + textStatus);
                $('#createSchemaErrorMessage').text("Error in registering schema");
                $('#createSchemaErrorResponse').show();
                //$('#schemaOperationsResponseText').text("Error in registering schema");
                //$('#schemaOperationsResponse').dialog("open");
                window.scrollTo(0, 0);
            }
        });
    });

    $('#viewSchemaButton').click(function() {
        var viewSchemaRequest = {
            "schemaIdentifier": {
                "namespace": $("#viewNamespace").val(),
                "name": $("#viewName").val(),
            }
        }
        if ($('#viewVersion').val() != "") {
            viewSchemaRequest["version"] = $('#viewVersion').val();
        }
        $.ajax({
            type: "POST",
            url: "http://" + host + "/entity-manager/schema/v1/schemaInfo",
            data: JSON.stringify(viewSchemaRequest),
            contentType: "application/json",
            success: function(result, textStatus) {
                var schemaVersion = result["schemaInfo"]["schemaVersions"][0];
                var schemaData = schemaVersion["schemaData"];
                var schemaReferences = schemaVersion["schemaReferences"];
                var version = schemaVersion["versionId"];
                viewEditor.getModel().setValue(formatProtoSchema(schemaData));
                if (schemaReferences != null && schemaReferences.length != 0) {
                    viewSchemaReferenceEditor.setValue(JSON.stringify(filterSchemaReferences(schemaReferences), null, 2));
                }
                console.log(result["schemaInfo"]["schemaId"]);
                $('#viewSchemaLabel').text("Schema: " + result["schemaInfo"]["schemaId"] + " Version: " + version);
                window.scrollTo(0, 0);
            },
            error: function(error, textStatus) {
                console.log(result);
                window.scrollTo(0, 0);
            }
        });
    });

    $('#fetchSchemaButton').click(function() {
        var viewSchemaRequest = {
            "schemaIdentifier": {
                "namespace": $("#editSchemaNamespace").val(),
                "name": $("#editSchemaName").val(),
            }
        }

        $.ajax({
            type: "POST",
            url: "http://" + host + "/entity-manager/schema/v1/schemaInfo",
            data: JSON.stringify(viewSchemaRequest),
            contentType: "application/json",
            success: function(result, textStatus) {
                var schemaVersion = result["schemaInfo"]["schemaVersions"][0];
                var schemaData = schemaVersion["schemaData"];
                var schemaReferences = schemaVersion["schemaReferences"];
                var version = schemaVersion["versionId"];
                readVersion = result["schemaInfo"]["version"];
                updateSchemaId = result["schemaInfo"]["schemaId"];
                var value = formatProtoSchema(schemaData);

                console.log(value);
                updateSchemaEditor.getModel().setValue(value);
                if (schemaReferences != null && schemaReferences.length != 0) {
                    updateSchemaReferenceEditor.setValue(JSON.stringify(filterSchemaReferences(schemaReferences), null, 2));
                }
                window.scrollTo(0, 0);
            },
            error: function(error, textStatus) {
                console.log(result);
                window.scrollTo(0, 0);
            }
        });
    });

    $('#editSchemaButton').click(function() {
        var updateSchemaRequest = {
            "schemaId": updateSchemaId,
            "readVersion": readVersion,
            "schemaVersion": {
                "schemaData": removeLinebreaks(updateSchemaEditor.getValue()),
                "schemaReferences": updateSchemaReferenceEditor.getValue() === "" ? [] : JSON.parse(removeSpaces(removeLinebreaks(updateSchemaReferenceEditor.getValue()))),
            }
        };
        $.ajax({
            type: "POST",
            url: "http://" + host + "/entity-manager/schema/v1/update",
            data: JSON.stringify(updateSchemaRequest),
            contentType: "application/json",
            success: function(result, textStatus) {
                console.log("Successfully Updated schema:" + textStatus);
                $('#editSchemaSuccessMessage').text("Successfully Updated schema");
                $('#editSchemaSuccessResponse').show();
                window.scrollTo(0, 0);
                //$('#schemaOperationsResponseText').text("Successfully Updated schema");
                //$('#schemaOperationsResponse').dialog("open");
            },
            error: function(error, textStatus) {
                console.log("Error in Updating schema:" + textStatus);
                $('#editSchemaErrorMessage').text("Error in registering schema");
                $('#editSchemaErrorResponse').show();
                window.scrollTo(0, 0);
                //$('#schemaOperationsResponseText').text("Error in Updating schema");
                //$('#schemaOperationsResponse').dialog("open");
            }
        });
    });

    $('#compareSchemaButton').click(function() {
        var viewSchemaRequest = {
            "schemaIdentifier": {
                "namespace": $("#compareSchemaNamespace").val(),
                "name": $("#compareSchemaName").val(),
            },
            "version": $('#version1').val()
        }

        $.ajax({
            type: "POST",
            url: "http://" + host + "/entity-manager/schema/v1/schemaInfo",
            data: JSON.stringify(viewSchemaRequest),
            contentType: "application/json",
            success: function(result, textStatus) {
                var schemaVersion = result["schemaInfo"]["schemaVersions"][0];
                var schemaData = schemaVersion["schemaData"];
                var originalSchemaReferences = filterSchemaReferences(schemaVersion["schemaReferences"]);
                var originalSchemaData = formatProtoSchema(schemaData);

                //Make another call with version2
                var viewSchemaRequest2 = {
                    "schemaIdentifier": {
                        "namespace": $("#compareSchemaNamespace").val(),
                        "name": $("#compareSchemaName").val(),
                    },
                    "version": $('#version2').val()
                }
                $.ajax({
                    type: "POST",
                    url: "http://" + host + "/entity-manager/schema/v1/schemaInfo",
                    data: JSON.stringify(viewSchemaRequest2),
                    contentType: "application/json",
                    success: function(result, textStatus) {
                        var modifiedSchemaVersion = result["schemaInfo"]["schemaVersions"][0];
                        var modifiedSchemaReferences = filterSchemaReferences(modifiedSchemaVersion["schemaReferences"]);
                        var modifiedSchemaData = formatProtoSchema(modifiedSchemaVersion["schemaData"]);
                        compareSchemaEditor.setModel({
                            original: monaco.editor.createModel(originalSchemaData, 'proto'),
                            modified: monaco.editor.createModel(modifiedSchemaData, 'proto')
                        });
                        compareSchemaReferenceEditor.setModel({
                            original: monaco.editor.createModel(JSON.stringify(originalSchemaReferences, null, 2), 'javascript'),
                            modified: monaco.editor.createModel(JSON.stringify(modifiedSchemaReferences, null, 2), 'javascript')
                        });
                        window.scrollTo(0, 0);
                    },
                    error: function(error, textStatus) {
                        console.log(result);
                        window.scrollTo(0, 0);
                    }
                });
            },
            error: function(error, textStatus) {
                console.log(result);
            }
        });


    });

    $('#viewDependencyTreeButton').click(function() {
        console.log("DependencyTreeButton Clicked");
        var namespace = $("#viewDependencyTreeNamespace").val();
        var name = $("#viewDependencyTreeName").val();
        var version = $("#viewDependencyTreeVersion").val();
        $("#treeViewSpinner").show();
        setTimeout(function() {
            var tree = [];
            buildDependencyTree(namespace, name, version, tree, namespace, name);
        }, 2000);
    });

    $('#createEntityButton').click(function() {
        $.ajax({
            type: "PUT",
            url: "http://" + host + "/entity-manager/v1/entity/manager/entityInfo",
            data: removeSpaces(removeLinebreaks(createEntityEditor.getValue())),
            contentType: "application/json",
            success: function(result, textStatus) {
                console.log("Successfully registered Entity:" + textStatus);
                $('#createEntitySuccessMessage').text("Successfully registered Entity");
                $('#createEntitySuccessResponse').show();
                window.scrollTo(0, 0);
            },
            error: function(error, textStatus) {
                console.log("Error in registering Entity:" + textStatus);
                $('#createEntityErrorMessage').text("Error in registering Entity");
                $('#createEntityErrorResponse').show();
                window.scrollTo(0, 0);
            }
        });
    });

    $('#viewEntityButton').click(function() {
        var viewEntityUrl = "http://" + host + "/entity-manager/v1/entity/manager/entityInfoByNamespaceName/" +
            $("#viewEntityNamespace").val() + "/" + $("#viewEntityName").val();
        $.ajax({
            type: "GET",
            url: viewEntityUrl,
            contentType: "application/json",
            success: function(result, textStatus) {
                var entityInfo = result["entityInfo"];
                viewEntityEditor.getModel().setValue(JSON.stringify(entityInfo, null, 2));
                window.scrollTo(0, 0);
            },
            error: function(error, textStatus) {
                console.log(result);
                window.scrollTo(0, 0);
            }
        });
    });

    $('#createIndexButton').click(function() {
            $.ajax({
                type: "POST",
                url: "http://" + host + "/entity-manager/index/v1/register",
                data: removeSpaces(removeLinebreaks(createIndexEditor.getValue())),
                contentType: "application/json",
                success: function(result, textStatus) {
                    console.log("Successfully registered Index:" + textStatus);
                    $('#createIndexSuccessMessage').text("Successfully registered Index");
                    $('#createIndexSuccessResponse').show();
                    window.scrollTo(0, 0);
                },
                error: function(error, textStatus) {
                    console.log("Error in registering Index:" + textStatus);
                    $('#createIndexErrorMessage').text("Error in registering Index");
                    $('#createIndexErrorResponse').show();
                    window.scrollTo(0, 0);
                }
            });
    });

    $('#viewIndexButton').click(function() {
            var viewIndexUrl = "http://" + host + "/entity-manager/index/v1/getIndexInfo/" +
                $("#viewIndexEntityNamespace").val() + "/" + $("#viewIndexEntityName").val();
            $.ajax({
                type: "GET",
                url: viewIndexUrl,
                contentType: "application/json",
                success: function(result, textStatus) {
                    var indexInfo = [];
                    for (var i = 0; i < result.length; i++) {
                        indexInfo.push(JSON.parse(result[i]));
                    }
                    viewIndexEditor.getModel().setValue(JSON.stringify(indexInfo, null, 2));
                    window.scrollTo(0, 0);
                },
                error: function(error, textStatus) {
                    console.log(result);
                    window.scrollTo(0, 0);
                }
            });
    });

    $('#fetchIndexButton').click(function() {
                var fetchIndexUrl = "http://" + host + "/entity-manager/index/v1/" +
                    $("#editIndexId").val();
                $.ajax({
                    type: "GET",
                    url: fetchIndexUrl,
                    contentType: "application/json",
                    success: function(result, textStatus) {
                        var entityIndex = result["entityIndex"];
                        var editIndexRequest = {
                                                                   "name": entityIndex["indexIdentifier"]["name"],
                                                                   "validationSchemaId": entityIndex["indexMeta"]["validationSchemaId"],
                                                                   "validationSchemaVersion": entityIndex["indexMeta"]["validationSchemaVersion"],
                                                                   "indexKeyInfo": entityIndex["indexMeta"]["indexKeyInfo"],
                                                                   "indexValueInfos": entityIndex["indexMeta"]["indexValueInfos"]
                                                                 };
                        editIndexEditor.getModel().setValue(JSON.stringify(editIndexRequest, null, 2));
                        window.scrollTo(0, 0);
                    },
                    error: function(error, textStatus) {
                        console.log(result);
                        window.scrollTo(0, 0);
                    }
                });
        });

    $('#editIndexButton').click(function() {
                $.ajax({
                    type: "POST",
                    url: "http://" + host + "/entity-manager/index/v1/updateIndex",
                    data: removeSpaces(removeLinebreaks(editIndexEditor.getValue())),
                    contentType: "application/json",
                    success: function(result, textStatus) {
                        console.log("Successfully Updated Index:" + textStatus);
                        $('#editIndexSuccessMessage').text("Successfully Updated Index");
                        $('#editIndexSuccessResponse').show();
                        window.scrollTo(0, 0);
                    },
                    error: function(error, textStatus) {
                        console.log("Error in Updating Index:" + textStatus);
                        $('#editIndexErrorMessage').text("Error in Updating Index");
                        $('#editIndexErrorResponse').show();
                        window.scrollTo(0, 0);
                    }
                });
        });

    $('#createSchemaSuccessCloseButton').click(function() {
        $('#createSchemaSuccessResponse').hide();
    });

    $('#createSchemaErrorCloseButton').click(function() {
        $('#createSchemaErrorResponse').hide();
    });

    $('#editSchemaSuccessCloseButton').click(function() {
        $('#editSchemaSuccessResponse').hide();
    });

    $('#editSchemaErrorCloseButton').click(function() {
        $('#editSchemaErrorResponse').hide();
    });

    $('#createEntitySuccessCloseButton').click(function() {
        $('#createEntitySuccessResponse').hide();
    });

    $('#createEntityErrorCloseButton').click(function() {
        $('#createEntityErrorResponse').hide();
    });

    $('#createIndexSuccessCloseButton').click(function() {
       $('#createIndexSuccessResponse').hide();
    });

    $('#createIndexErrorCloseButton').click(function() {
       $('#createIndexErrorResponse').hide();
    });
});

function formatProtoSchema(schemaData) {
    var token = schemaData.split(";");
    var formattedSchema = "";
    for (var i = 0; i < token.length; i++) {
        if (token[i] === "") {
            continue;
        }
        if (token[i].includes("}")) {
            formattedSchema = formatCloseBrackets(formattedSchema, token[i]);
        } else if (token[i].includes("{")) {
            formattedSchema = formatOpenBrackets(formattedSchema, token[i]);
        } else {
            formattedSchema += token[i] + ";" + "\n";
        }
    }
    var lines = formattedSchema.split("\n");
    var filteredSchema = "";
    for (var i = 0; i < lines.length; i++) {
        if (lines[i].includes("com.flipkart.entitymanager.schemaregistry.protobuf.schema_id") ||
            lines[i].includes("com.flipkart.entitymanager.schemaregistry.protobuf.schema_version") ||
            lines[i].includes("SchemaOptions")) {
            continue;
        } else {
            filteredSchema += lines[i] + "\n";
        }
    }
    return filteredSchema;
}

function formatOpenBrackets(formattedSchema, input) {
    formattedSchema += "\n";
    var beginning = input.substring(0, input.indexOf("{") + 1);
    var rest = input.substring(input.indexOf("{") + 1);
    formattedSchema += beginning + "\n";
    if (rest.includes("{")) {
        formattedSchema = formatOpenBrackets(formattedSchema, rest);
    } else {
        formattedSchema += rest + ";" + "\n";
    }
    return formattedSchema;
}

function formatCloseBrackets(formattedSchema, input) {
    var beginning = input.substring(0, input.indexOf("}") + 1);
    var rest = input.substring(input.indexOf("}") + 1);
    formattedSchema += beginning + "\n" + "\n";
    if (rest.includes("}")) {
        var restTokens = rest.split("}");
        formattedSchema += restTokens[0] + "}" + "\n"; //add an extra newline for the end of the message
        //The restTokens can also contain beginning of another message
        if (restTokens[1].includes("{")) {
            formattedSchema = formatOpenBrackets(formattedSchema, restTokens[1]);
        } else {
            formattedSchema += restTokens[1] + ";" + "\n";
        }
    } else {
        if (rest.includes("{")) {
            formattedSchema = formatOpenBrackets(formattedSchema, rest);
        } else {
            formattedSchema += rest + ";" + "\n";
        }
    }
    return formattedSchema;
}

function removeLinebreaks(str) {
    return str.replace(/[\r\n]+/gm, "");
}

function removeSpaces(str) {
    return str.replace(/ +/gm, "");
}

function filterSchemaReferences(schemaReferences) {
    var filteredReferences = [];
    for (var i = 0; i < schemaReferences.length; i++) {
        if (schemaReferences[i]["schemaIdentifier"]["name"] === "SchemaOptions") {
            continue;
        } else {
            filteredReferences.push(schemaReferences[i]);
        }
    }
    return filteredReferences;
}

function buildDependencyTree(namespace, name, version, nodes, originalNamespace, originalName) {
    var viewSchemaRequest = {
        "schemaIdentifier": {
            "namespace": namespace,
            "name": name
        },
        "version": version
    };
    $.ajax({
        type: "POST",
        async: false,
        url: "http://" + host + "/entity-manager/schema/v1/schemaInfo",
        data: JSON.stringify(viewSchemaRequest),
        contentType: "application/json",
        success: function(result, textStatus) {
            var schemaVersion = result["schemaInfo"]["schemaVersions"][0];
            var schemaData = schemaVersion["schemaData"];
            var schemaReferences = schemaVersion["schemaReferences"];
            var version = schemaVersion["versionId"];
            var filteredSchemaReferences = filterSchemaReferences(schemaReferences);
            var node = {
                "text": namespace + " : " + name + " : " + version
            };
            nodes.push(node);
            if (filteredSchemaReferences.length > 0) {
                node["nodes"] = [];
            }
            for (var i = 0; i < filteredSchemaReferences.length; i++) {
                buildDependencyTree(filteredSchemaReferences[i]["schemaIdentifier"]["namespace"], filteredSchemaReferences[i]["schemaIdentifier"]["name"], filteredSchemaReferences[i]["versionId"], node["nodes"]);
            }
            if (originalNamespace === namespace && originalName === name) {
                $('#viewDependencyTreeValue').treeview({
                    levels: 99,
                    data: nodes,
                    expandIcon: "fa fa-plus",
                    collapseIcon: "fa fa-minus"
                });
                $("#treeViewSpinner").hide();
            }
            window.scrollTo(0, 0);
        },
        error: function(error, textStatus) {
            console.log(result);
            window.scrollTo(0, 0);
        }
    });
}