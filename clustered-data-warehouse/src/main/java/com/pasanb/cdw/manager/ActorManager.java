package com.pasanb.cdw.manager;

import com.pasanb.cdw.manager.impl.ActorManagerImpl;

/**
 * Created by Pasan on 6/25/2016.
 */
public interface ActorManager {

    static ActorManager getInstance(){
        return ActorManagerImpl.getInstance();
    }

    void start() throws Exception;
}
