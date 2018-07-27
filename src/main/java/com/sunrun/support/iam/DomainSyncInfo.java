package com.sunrun.support.iam;

import com.sunrun.entity.Domain;
import com.sunrun.entity.Org;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DomainSyncInfo {
    private static final String SUCCESS_DOMAINS = "successDomains";
    private static final String FIALED_DOMAINS = "failedDomains";
    private static final String DELETE_DOMAINS = "deleteDomains";
    private static volatile ConcurrentHashMap<String,Object> domainsInfo = new ConcurrentHashMap();
    private static volatile List<Domain> needDeleteDomains = Collections.synchronizedList(new LinkedList<Domain>());
    public static ConcurrentHashMap getDomainsInfo() {
        return domainsInfo;
    }
    public static List<Domain> getNeedDeleteDomains(){
        return needDeleteDomains;
    }

    public static synchronized void  putSuccessDomain(Domain domain){
        List<Domain> domains;
        if (domainsInfo.get(SUCCESS_DOMAINS) == null ) {
            domains = Collections.synchronizedList(new ArrayList<Domain>());
        } else {
            domains = (List<Domain>) domainsInfo.get(SUCCESS_DOMAINS);
        }
        domains.add(domain);
        domainsInfo.put(SUCCESS_DOMAINS,domains);
    }

    public static synchronized void  putDeleteDomain(Domain domain){
        List<Domain> domains;
        if (domainsInfo.get(DELETE_DOMAINS) == null ) {
            domains = Collections.synchronizedList(new ArrayList<Domain>());
        } else {
            domains = (List<Domain>) domainsInfo.get(DELETE_DOMAINS);
        }
        domains.add(domain);
        domainsInfo.put(DELETE_DOMAINS,domains);
    }

    public static synchronized void  putFailedDomain(Domain domain){
        List<Domain> domains;
        if (domainsInfo.get(FIALED_DOMAINS) == null ) {
            domains = Collections.synchronizedList(new ArrayList<Domain>());
        } else {
            domains = (List<Domain>) domainsInfo.get(FIALED_DOMAINS);
        }
        domains.add(domain);
        domainsInfo.put(FIALED_DOMAINS,domains);
    }

    public static List<Domain> getSuccessDomains() {
        return domainsInfo.get(SUCCESS_DOMAINS) == null ? null : (List<Domain>)domainsInfo.get(SUCCESS_DOMAINS);
    }
    public static List<Domain> getFailedDomains(){
        return domainsInfo.get(FIALED_DOMAINS) == null ? null : (List<Domain>)domainsInfo.get(FIALED_DOMAINS);
    }

    public static void clearDomains(){
        domainsInfo.clear();
        /*domainsInfo.remove(SUCCESS_DOMAINS);
        domainsInfo.remove(FIALED_DOMAINS);
        domainsInfo.remove(DELETE_DOMAINS);*/
    }
}
