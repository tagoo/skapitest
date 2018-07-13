package com.sunrun.security.sync;

import com.sunrun.common.notice.ReturnData;
import com.sunrun.exception.IamConnectionException;

public interface Synchronize {
    ReturnData synchronizeData() throws IamConnectionException;
}
