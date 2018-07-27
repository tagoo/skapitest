package com.sunrun.security.sync;

import com.sunrun.exception.*;

public interface Synchronize {
    boolean synchronizeData() throws IamConnectionException, NotFindMucServiceException, SyncOrgException, CannotFindDomain, GetUserException;
}
