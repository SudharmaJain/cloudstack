// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.
package org.apache.cloudstack.api.command.user.solidfire;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import org.apache.cloudstack.api.ApiConstants;
import org.apache.cloudstack.api.APICommand;
import org.apache.cloudstack.api.ApiErrorCode;
import org.apache.cloudstack.api.BaseCmd;
import org.apache.cloudstack.api.Parameter;
import org.apache.cloudstack.api.ServerApiException;
import org.apache.cloudstack.api.ResponseObject.ResponseView;
import org.apache.cloudstack.api.helper.ApiHelper;
import org.apache.cloudstack.api.response.AccountResponse;
import org.apache.cloudstack.api.response.ApiSolidFireVirtualNetworkResponse;
import org.apache.cloudstack.api.response.ApiSolidFireVolumeResponse;
import org.apache.cloudstack.solidfire.ApiSolidFireService2;
import org.apache.cloudstack.solidfire.dataaccess.SfVolume;

@APICommand(name = "createSolidFireVolume", responseObject = ApiSolidFireVolumeResponse.class, description = "Create SolidFire Volume",
    requestHasSensitiveInfo = false, responseHasSensitiveInfo = false)
public class CreateSolidFireVolumeCmd extends BaseCmd {
    private static final Logger s_logger = Logger.getLogger(CreateSolidFireVolumeCmd.class.getName());
    private static final String s_name = "createsolidfirevolumeresponse";

    @Parameter(name = ApiConstants.NAME, type = CommandType.STRING, description = ApiHelper.VOLUME_NAME_DESC, required = true)
    private String _name;

    @Parameter(name = ApiConstants.SIZE, type = CommandType.LONG, description = ApiHelper.SIZE_DESC, required = true)
    private long _size;

    @Parameter(name = ApiConstants.MIN_IOPS, type = CommandType.LONG, description = ApiHelper.MIN_IOPS_DESC, required = true)
    private long _minIops;

    @Parameter(name = ApiConstants.MAX_IOPS, type = CommandType.LONG, description = ApiHelper.MAX_IOPS_DESC, required = true)
    private long _maxIops;

    @Parameter(name = ApiHelper.BURST_IOPS, type = CommandType.LONG, description = ApiHelper.BURST_IOPS_DESC, required = true)
    private long _burstIops;

    @Parameter(name = ApiConstants.ACCOUNT_ID, type = CommandType.UUID, entityType = AccountResponse.class, description = ApiHelper.ACCOUNT_ID_DESC, required = true)
    private long _accountId;

    @Parameter(name = ApiHelper.SF_VIRTUAL_NETWORK_ID, type = CommandType.UUID, entityType = ApiSolidFireVirtualNetworkResponse.class,
            description = ApiHelper.VIRTUAL_NETWORK_ID_DESC, required = true)
    private long _sfVirtualNetworkId;

    @Inject private ApiHelper _apiHelper;
    @Inject private ApiSolidFireService2 _apiSolidFireService2;

    /////////////////////////////////////////////////////
    /////////////// API Implementation///////////////////
    /////////////////////////////////////////////////////

    @Override
    public String getCommandName() {
        return s_name;
    }

    @Override
    public long getEntityOwnerId() {
        return _accountId;
    }

    @Override
    public void execute() {
        try {
            s_logger.info(CreateSolidFireVolumeCmd.class.getName() + ".execute invoked");

            SfVolume sfVolume = _apiSolidFireService2.createSolidFireVolume(_name, _size, _minIops, _maxIops, _burstIops, _accountId, _sfVirtualNetworkId);

            ResponseView responseView = _apiHelper.isRootAdmin() ? ResponseView.Full : ResponseView.Restricted;

            ApiSolidFireVolumeResponse response = _apiHelper.getApiSolidFireVolumeResponse(sfVolume, responseView);

            response.setResponseName(getCommandName());
            response.setObjectName("apicreatesolidfirevolume");

            setResponseObject(response);
        }
        catch (Throwable t) {
            s_logger.error(t.getMessage());

            throw new ServerApiException(ApiErrorCode.INTERNAL_ERROR, t.getMessage());
        }
    }
}