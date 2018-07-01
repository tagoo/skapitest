package com.sunrun.security.sync;

import com.sunrun.exception.IamConnectionException;

public interface Synchronize {
    boolean synchronizeData() throws IamConnectionException;
}
