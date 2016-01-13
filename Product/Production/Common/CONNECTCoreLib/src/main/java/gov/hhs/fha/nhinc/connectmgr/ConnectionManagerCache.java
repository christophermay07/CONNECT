/*
 * Copyright (c) 2009-2015, United States Government, as represented by the Secretary of Health and Human Services.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above
 *       copyright notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the documentation
 *       and/or other materials provided with the distribution.
 *     * Neither the name of the United States Government nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE UNITED STATES GOVERNMENT BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package gov.hhs.fha.nhinc.connectmgr;

import gov.hhs.fha.nhinc.common.nhinccommon.NhinTargetCommunitiesType;
import gov.hhs.fha.nhinc.common.nhinccommon.NhinTargetCommunityType;
import gov.hhs.fha.nhinc.common.nhinccommon.NhinTargetSystemType;
import gov.hhs.fha.nhinc.connectmgr.persistance.dao.InternalConnectionInfoDAOFileImpl;
import gov.hhs.fha.nhinc.connectmgr.persistance.dao.UddiConnectionInfoDAOFileImpl;
import gov.hhs.fha.nhinc.nhinclib.NhincConstants;
import gov.hhs.fha.nhinc.nhinclib.NhincConstants.ADAPTER_API_LEVEL;
import gov.hhs.fha.nhinc.nhinclib.NhincConstants.UDDI_SPEC_VERSION;
import gov.hhs.fha.nhinc.nhinclib.NullChecker;
import gov.hhs.fha.nhinc.properties.PropertyAccessException;
import gov.hhs.fha.nhinc.properties.PropertyAccessor;
import gov.hhs.fha.nhinc.util.HomeCommunityMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uddi.api_v3.BindingTemplate;
import org.uddi.api_v3.BusinessDetail;
import org.uddi.api_v3.BusinessEntity;
import org.uddi.api_v3.BusinessService;
import org.uddi.api_v3.KeyedReference;
import org.uddi.api_v3.Name;

/**
 * This class is used to manage the Connection Manager's cache. It handles both internal connection settings and UDDI
 * connection settings. If there is a collision for a connection between the UDDI and the Internal settings, the
 * internal one will be used.
 *
 * @author Les Westberg, msw
 */
public class ConnectionManagerCache implements ConnectionManager {

    private static final Logger LOG = LoggerFactory.getLogger(ConnectionManagerCache.class);
    private final PropertyAccessor accessor;

    public static final String UDDI_SPEC_VERSION_KEY = "uddi:nhin:versionofservice";
    private static final String INTERNAL_CONNECTION_API_LEVEL_KEY = "CONNECT:adapter:apilevel";

    // Connection information maps, keyed by home community id
    private final Map<String, BusinessEntity> uddiConnectionInfo = new HashMap<>();
    private final Map<String, BusinessEntity> internalConnectionInfo = new HashMap<>();

    // TRUE if the properties have been loaded
    private boolean uddiLoaded = false;
    private boolean internalLoaded = false;

    // Last modified time, measured in milliseconds since epoch
    private long uddiFileLastModified = 0;
    private long internalFileLastModified = 0;

    public ConnectionManagerCache() {
        this.accessor = PropertyAccessor.getInstance();
    }

    public ConnectionManagerCache(PropertyAccessor accessor) {
        this.accessor = accessor;
    }

    private static class SingletonHolder {

        public static final ConnectionManagerCache INSTANCE = new ConnectionManagerCache();
    }

    public static ConnectionManagerCache getInstance() {
        return SingletonHolder.INSTANCE;
    }

    protected UddiConnectionInfoDAOFileImpl getUddiConnectionManagerDAO() {
        return UddiConnectionInfoDAOFileImpl.getInstance();
    }

    protected InternalConnectionInfoDAOFileImpl getInternalConnectionManagerDAO() {
        return InternalConnectionInfoDAOFileImpl.getInstance();
    }

    /**
     * This method is used to load the UDDI Connection Information form the uddiConnectionInfo.xml file.
     */
    private void loadUDDIConnectionInfo() throws ConnectionManagerException {
        BusinessDetail businessDetail = null;

        try {
            businessDetail = getUddiConnectionManagerDAO().loadBusinessDetail();
        } catch (Exception ex) {
            LOG.error("Could not load UDDI business details: {}", ex.getLocalizedMessage(), ex);
        }

        if (businessDetail != null) {
            synchronized (uddiConnectionInfo) {
                uddiConnectionInfo.clear();

                if (businessDetail.getBusinessEntity() != null && !businessDetail.getBusinessEntity().isEmpty()) {
                    for (BusinessEntity entity : businessDetail.getBusinessEntity()) {
                        ConnectionManagerCacheHelper helper = new ConnectionManagerCacheHelper();
                        String homeCommunityId = helper.getCommunityId(entity);

                        if (StringUtils.isNotEmpty(homeCommunityId)) {
                            uddiConnectionInfo.put(homeCommunityId, entity);
                        }
                    }
                }

                uddiLoaded = true;
                uddiFileLastModified = getUddiConnectionManagerDAO().getLastModified();
            }
        } else {
            LOG.warn("No UDDI information was found");
        }
    }

    /**
     * This method simply checks to see if the cache is loaded. If it is not, then it is loaded as a byproduct of
     * calling this method.
     *
     * @throws gov.hhs.fha.nhinc.connectmgr.ConnectionManagerException
     */
    private void checkLoaded() throws ConnectionManagerException {
        if (!internalLoaded) {
            forceRefreshInternalConnectCache();
        }

        if (!uddiLoaded) {
            forceRefreshUDDICache();
        }

        refreshIfExpired();
    }

    /**
     * Function provided for unit testing
     *
     * @param businessEntity
     * @param newId
     */
    public void setCommunityId(BusinessEntity businessEntity, String newId) {
        ConnectionManagerCacheHelper helper = new ConnectionManagerCacheHelper();

        // Retrieve and update the existing KeyedReference, if it exists
        KeyedReference ref = helper.getCommunityIdKeyReference(businessEntity);
        if (ref != null) {
            ref.setKeyValue(newId);
        }

        // Create and add a new KeyedReference
        KeyedReference identifierBagRef = new KeyedReference();
        identifierBagRef.setKeyValue(newId);
        businessEntity.getIdentifierBag().getKeyedReference().add(identifierBagRef);
    }

    /**
     * This method is used to load the UDDI Connection Information form the uddiConnectionInfo.xml file.
     */
    private void loadInternalConnectionInfo() throws ConnectionManagerException {
        BusinessDetail businessDetail = null;

        try {
            businessDetail = getInternalConnectionManagerDAO().loadBusinessDetail();
        } catch (Exception ex) {
            LOG.error("Could not load Internal Connection business details: {}", ex.getLocalizedMessage(), ex);
        }

        if (businessDetail != null) {
            synchronized (internalConnectionInfo) {
                internalConnectionInfo.clear();

                if (businessDetail.getBusinessEntity() != null && !businessDetail.getBusinessEntity().isEmpty()) {
                    for (BusinessEntity businessEntity : businessDetail.getBusinessEntity()) {
                        ConnectionManagerCacheHelper helper = new ConnectionManagerCacheHelper();
                        String homeCommunityId = helper.getCommunityId(businessEntity);
                        if (StringUtils.isNotEmpty(homeCommunityId)) {
                            internalConnectionInfo.put(homeCommunityId, businessEntity);
                        }
                    }
                }

                internalLoaded = true;
                internalFileLastModified = getInternalConnectionManagerDAO().getLastModified();

            }
        } else {
            LOG.warn("No UDDI information was found");
        }
    }

    /**
     * This method will cause the ConnectionManagerCache to refresh the UDDI connection data by replacing the cached
     * UDDI information with the information in the uddiConnectionInfo.xml file.
     *
     * @throws ConnectionManagerException
     */
    public void forceRefreshUDDICache() throws ConnectionManagerException {
        loadUDDIConnectionInfo();
    }

    /**
     * This method will cause the ConnectionManagerCache to refresh the internal connection data by replacing the cached
     * internal connection information with the information in the internalConnectionInfo.xml file.
     *
     * @throws ConnectionManagerException
     */
    public void forceRefreshInternalConnectCache() throws ConnectionManagerException {
        loadInternalConnectionInfo();
    }

    /**
     * This method checks to see if either cache has expired and forces a refresh if it has.
     *
     */
    private void refreshIfExpired() throws ConnectionManagerException {
        long uddiLastModified = 0;
        long internalLastModified = 0;

        // Find out our refresh timing from the properties file.
        try {
            uddiLastModified = getUddiConnectionManagerDAO().getLastModified();
            internalLastModified = getInternalConnectionManagerDAO().getLastModified();
        } catch (Exception e) {
            // Assume a refresh is required, but log the exception.
            LOG.warn("Failed to retrieve last modified dates on the connection manager XML files: {}",
                e.getLocalizedMessage(), e);
        }

        // If we need to refresh the UDDI cache information.
        if (uddiLastModified != uddiFileLastModified) {
            forceRefreshUDDICache();
            LOG.debug("UDDI cache was refreshed based on last modified time stamp change.");
        }

        if (internalLastModified != internalFileLastModified) {
            forceRefreshInternalConnectCache();
            LOG.debug("Internal connection cache was refreshed based on last modified time stamp change.");
        }
    }

    @Override
    public List<BusinessEntity> getAllBusinessEntities() throws ConnectionManagerException {
        List<BusinessEntity> allEntities = new ArrayList<>();
        ConnectionManagerCacheHelper helper = new ConnectionManagerCacheHelper();
        checkLoaded();

        // First get the information from the internal connections.
        for (BusinessEntity internalEntity : internalConnectionInfo.values()) {
            if (NullChecker.isNotNullish(helper.getCommunityId(internalEntity))) {
                allEntities.add(internalEntity);
            }
        }

        // Next get the information from the UDDI connections -
        // If it is in the list, then merge the services. If not, then add it as is.
        for (BusinessEntity uddiEntity : uddiConnectionInfo.values()) {
            String homeCommunityId = helper.getCommunityId(uddiEntity);
            if (NullChecker.isNotNullish(homeCommunityId)) {
                BusinessEntity existingEntity = helper.extractBusinessEntity(allEntities, homeCommunityId);
                if (existingEntity != null) {
                    helper.mergeBusinessEntityServices(existingEntity, uddiEntity);
                    helper.replaceBusinessEntity(allEntities, existingEntity);
                } else {
                    allEntities.add(uddiEntity);
                }
            }
        }
        return allEntities;

    }

    @Override
    public BusinessEntity getBusinessEntity(String homeCommunityId) throws ConnectionManagerException {
        ConnectionManagerCacheHelper helper = new ConnectionManagerCacheHelper();
        checkLoaded();

        BusinessEntity internalEntity = null;
        BusinessEntity uddiEntity = null;

        String homeCommunityIdWithoutPrefix = HomeCommunityMap.formatHomeCommunityId(homeCommunityId);
        String homeCommunityIdWithPrefix = HomeCommunityMap.getHomeCommunityIdWithPrefix(homeCommunityId);

        // TODO: The below logic needs to be revisited
        // First look in internal connection info
        if (internalConnectionInfo.containsKey(homeCommunityIdWithoutPrefix)) {
            internalEntity = internalConnectionInfo.get(homeCommunityIdWithoutPrefix);
        } else if (internalConnectionInfo.containsKey(homeCommunityIdWithPrefix)) {
            internalEntity = internalConnectionInfo.get(homeCommunityIdWithPrefix);
        }

        // Not found in internal connection info, check uddi connection info
        if (uddiConnectionInfo.containsKey(homeCommunityIdWithoutPrefix)) {
            uddiEntity = uddiConnectionInfo.get(homeCommunityIdWithoutPrefix);
        } else if (uddiConnectionInfo.containsKey(homeCommunityIdWithPrefix)) {
            uddiEntity = uddiConnectionInfo.get(homeCommunityIdWithPrefix);
        }

        if (internalEntity != null && uddiEntity != null) {
            helper.mergeBusinessEntityServices(internalEntity, uddiEntity);
        } else if (uddiEntity != null) {
            return uddiEntity;
        }

        return internalEntity;
    }

    @Override
    public String getBusinessEntityName(String homeCommunityId) throws ConnectionManagerException {
        BusinessEntity businessEntity = getBusinessEntity(homeCommunityId);

        if (businessEntity != null && businessEntity.getName() != null) {
            for (Name name : businessEntity.getName()) {
                if (name != null && name.getValue() != null && !name.getValue().isEmpty()) {
                    return name.getValue();
                }
            }
        }

        return null;
    }

    @Override
    public Set<BusinessEntity> getBusinessEntitySet(List<String> homeCommunityIds) throws ConnectionManagerException {
        Set<BusinessEntity> entities = new HashSet<>();

        checkLoaded();

        if (homeCommunityIds != null) {
            // We always first look in our internal list - then in the UDDI one...
            for (String homeCommunityId : homeCommunityIds) {
                BusinessEntity entity = getBusinessEntity(homeCommunityId);
                if (entity != null) {
                    entities.add(entity);
                }
            }
        }

        return entities.isEmpty() ? null : entities;
    }

    @Override
    public BusinessEntity getBusinessEntityByServiceName(String homeCommunityId, String uniformServiceName)
        throws ConnectionManagerException {

        ConnectionManagerCacheHelper helper = new ConnectionManagerCacheHelper();

        // Reload remote and local if needed
        checkLoaded();

        String homeCommunityIdWithoutPrefix = HomeCommunityMap.formatHomeCommunityId(homeCommunityId);
        String homeCommunityIdWithPrefix = HomeCommunityMap.getHomeCommunityIdWithPrefix(homeCommunityId);

        // Validation
        if (NullChecker.isNullish(homeCommunityId) || NullChecker.isNullish(uniformServiceName)) {
            return null;
        }

        BusinessEntity internalBusinessEntity = null;
        BusinessEntity uddiEntity = null;
        BusinessService bService;

        // TODO: The below logic needs to be revisited
        // check the internal connections for the service
        if (internalConnectionInfo.containsKey(homeCommunityIdWithPrefix)) {
            internalBusinessEntity = internalConnectionInfo.get(homeCommunityIdWithPrefix);

            bService = helper.getBusinessServiceByServiceName(internalBusinessEntity, uniformServiceName);
            if (bService == null) {
                internalBusinessEntity = null;
            }
        } else if (internalConnectionInfo.containsKey(homeCommunityIdWithoutPrefix)) {
            internalBusinessEntity = internalConnectionInfo.get(homeCommunityIdWithoutPrefix);

            bService = helper.getBusinessServiceByServiceName(internalBusinessEntity, uniformServiceName);
            if (bService == null) {
                internalBusinessEntity = null;
            }
        }

        // check the uddi connections for the service
        if (uddiConnectionInfo.containsKey(homeCommunityIdWithPrefix)) {
            uddiEntity = uddiConnectionInfo.get(homeCommunityIdWithPrefix);

            bService = helper.getBusinessServiceByServiceName(uddiEntity, uniformServiceName);
            if (bService == null) {
                uddiEntity = null;
            }
        } else if (uddiConnectionInfo.containsKey(homeCommunityIdWithoutPrefix)) {
            uddiEntity = uddiConnectionInfo.get(homeCommunityIdWithoutPrefix);

            bService = helper.getBusinessServiceByServiceName(uddiEntity, uniformServiceName);
            if (bService == null) {
                uddiEntity = null;
            }
        }

        // Merge local and remote; if nothing to merge, return null
        BusinessEntity combinedEntity = null;
        if (internalBusinessEntity != null && uddiEntity != null) {
            helper.mergeBusinessEntityServices(internalBusinessEntity, uddiEntity);
            combinedEntity = internalBusinessEntity;
        } else if (internalBusinessEntity != null) {
            combinedEntity = internalBusinessEntity;
        } else if (uddiEntity != null) {
            combinedEntity = uddiEntity;
        }

        return combinedEntity;
    }

    @Override
    public BusinessEntity getBusinessEntityByHCID(String homeCommunityId) throws ConnectionManagerException {
        ConnectionManagerCacheHelper helper = new ConnectionManagerCacheHelper();
        // Reload remote and local if needed
        checkLoaded();

        if (StringUtils.isEmpty(homeCommunityId)) {
            return null;
        }

        String homeCommunityIdWithoutPrefix = HomeCommunityMap.formatHomeCommunityId(homeCommunityId);
        String homeCommunityIdWithPrefix = HomeCommunityMap.getHomeCommunityIdWithPrefix(homeCommunityId);

        BusinessEntity internalBusinessEntity = null;
        BusinessEntity uddiEntity = null;

        // TODO: The below logic needs to be revisited
        // load internal connections, check first with the urn:oid prefix, if not found check without the prefix
        if (internalConnectionInfo.containsKey(homeCommunityIdWithoutPrefix)) {
            internalBusinessEntity = internalConnectionInfo.get(homeCommunityIdWithoutPrefix);
        } else if (internalConnectionInfo.containsKey(homeCommunityIdWithPrefix)) {
            internalBusinessEntity = internalConnectionInfo.get(homeCommunityIdWithPrefix);
        }

        // get UDDI from cache, check first with the urn:oid prefix, if not found check without the prefix
        if (uddiConnectionInfo.containsKey(homeCommunityIdWithPrefix)) {
            uddiEntity = uddiConnectionInfo.get(homeCommunityIdWithPrefix);
        } else if (uddiConnectionInfo.containsKey(homeCommunityIdWithoutPrefix)) {
            uddiEntity = uddiConnectionInfo.get(homeCommunityIdWithoutPrefix);
        }

        // Merge local and remote
        BusinessEntity combinedEntity;
        if (internalBusinessEntity != null && uddiEntity != null) {
            combinedEntity = helper.mergeBusinessEntityServices(internalBusinessEntity, uddiEntity);
        } else if (internalBusinessEntity != null) {
            combinedEntity = internalBusinessEntity;
        } else if (uddiEntity != null) {
            combinedEntity = uddiEntity;
        } else {
            return null; // We found nothing...
        }

        return combinedEntity;
    }

    /**
     * This method retrieves a set of URLs for that that service for all communities in the specified region or state.
     *
     * @param urlSet A set of unique URLs to add state URL information to
     * @param region Region or State name to filter on.
     * @param serviceName The name of the service to locate who URL is being requested.
     * @throws ConnectionManagerException
     */
    private void filterByRegion(Set<UrlInfo> urlSet, String region, String serviceName)
        throws ConnectionManagerException {

        ConnectionManagerCacheHelper helper = new ConnectionManagerCacheHelper();
        Set<BusinessEntity> entities = getAllBusinessEntitySetByServiceName(serviceName);

        if (entities != null) {
            for (BusinessEntity entity : entities) {
                if (helper.getStates(entity) != null && NullChecker.isNotNullish(helper.getStates(entity))) {
                    for (String state : helper.getStates(entity)) {
                        if (state.equalsIgnoreCase(region)) {
                            String hcid = helper.getCommunityId(entity);
                            String url = getDefaultEndpointURLByServiceName(hcid, serviceName);

                            if (NullChecker.isNotNullish(url) && NullChecker.isNotNullish(hcid)) {
                                UrlInfo entry = new UrlInfo();
                                entry.setHcid(hcid);
                                entry.setUrl(url);
                                urlSet.add(entry);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * This method will print out the contents of a URL list.
     *
     * @param urlList List of URLs.
     */
    private void printUrlList(List<UrlInfo> urlList) {
        if (urlList != null) {
            LOG.debug("Connection Management URL Info List:");
            for (UrlInfo url : urlList) {
                LOG.debug("   HCID: {} URL: {}", url.getHcid(), url.getUrl());
            }
        } else {
            LOG.debug("URL List was Empty");
        }
    }

    @Override
    public Set<BusinessEntity> getBusinessEntitySetByServiceName(List<String> homeCommunityIds,
        String uniformServiceName) throws ConnectionManagerException {

        Set<BusinessEntity> entities = new HashSet<>();

        checkLoaded();

        if (homeCommunityIds != null && StringUtils.isNotEmpty(uniformServiceName)) {
            for (String homeCommunityId : homeCommunityIds) {
                BusinessEntity entity = getBusinessEntityByServiceName(homeCommunityId, uniformServiceName);
                if (entity != null) {
                    entities.add(entity);
                }
            }
        }

        return (entities.isEmpty()) ? null : entities;
    }

    @Override
    public Set<BusinessEntity> getAllBusinessEntitySetByServiceName(String uniformServiceName)
        throws ConnectionManagerException {

        checkLoaded();

        // This is a slick way to add them all and remove any duplicates...
        HashSet<String> hcids = new HashSet<>();
        hcids.addAll(internalConnectionInfo.keySet());
        hcids.addAll(uddiConnectionInfo.keySet());

        Set<BusinessEntity> entities = getBusinessEntitySetByServiceName(new ArrayList<>(hcids), uniformServiceName);

        return (entities.isEmpty()) ? null : entities;
    }

    @Override
    public List<UDDI_SPEC_VERSION> getSpecVersions(String homeCommunityId,
        NhincConstants.NHIN_SERVICE_NAMES serviceName) {

        ConnectionManagerCacheHelper helper = new ConnectionManagerCacheHelper();
        List<UDDI_SPEC_VERSION> specVersions = new ArrayList<>();

        try {
            BusinessEntity businessEntity = getBusinessEntity(homeCommunityId);
            specVersions = helper.getSpecVersionsFromBusinessEntity(businessEntity, serviceName);
        } catch (Exception ex) {
            LOG.error("Could not get spec versions from business entity: {}", ex.getLocalizedMessage(), ex);
        }

        return specVersions;
    }

    @Override
    public String getAdapterEndpointURL(String homeCommunityId, String serviceName, ADAPTER_API_LEVEL level)
        throws ConnectionManagerException {

        ConnectionManagerCacheHelper helper = new ConnectionManagerCacheHelper();
        String endpointUrl = null;

        BusinessEntity entity = getBusinessEntityByServiceName(homeCommunityId, serviceName);
        BindingTemplate template = helper.findBindingTemplateByCategoryBagNameValue(entity, serviceName,
            INTERNAL_CONNECTION_API_LEVEL_KEY, level.toString());

        if (template != null) {
            endpointUrl = template.getAccessPoint().getValue();
        }

        return endpointUrl;
    }

    @Override
    public String getAdapterEndpointURL(String serviceName, ADAPTER_API_LEVEL level)
        throws ConnectionManagerException {

        return getAdapterEndpointURL(getHomeCommunityFromPropFile(), serviceName, level);
    }

    /**
     *
     * @return home community id
     */
    protected String getHomeCommunityFromPropFile() {
        String sHomeCommunityId = null;

        try {
            sHomeCommunityId = accessor.getProperty(NhincConstants.GATEWAY_PROPERTY_FILE,
                NhincConstants.HOME_COMMUNITY_ID_PROPERTY);
        } catch (PropertyAccessException ex) {
            LOG.error("Error: Failed to retrieve {} from property file: {}",
                NhincConstants.HOME_COMMUNITY_ID_PROPERTY, NhincConstants.GATEWAY_PROPERTY_FILE, ex);
        }

        return sHomeCommunityId;
    }

    @Override
    public String getDefaultEndpointURLByServiceName(String homeCommunityId, String uniformServiceName)
        throws ConnectionManagerException {

        ConnectionManagerCacheHelper helper = new ConnectionManagerCacheHelper();
        LOG.trace("begin getEndpointURLByServiceName");

        String endpointURL = "";

        BusinessEntity entity = getBusinessEntityByHCID(homeCommunityId);
        if (entity == null) {
            return endpointURL;
        }

        BusinessService service = helper.getBusinessServiceByServiceName(entity, uniformServiceName);
        if (service == null) {
            return endpointURL;
        }

        List<UDDI_SPEC_VERSION> specVersions = helper.getSpecVersions(service);
        if (specVersions.isEmpty()) {
            return endpointURL;
        }

        UDDI_SPEC_VERSION highestSpec = helper.getHighestUDDISpecVersion(specVersions);
        if (highestSpec == null) {
            return endpointURL;
        }

        LOG.debug("Attempting to find binding template with spec version ({}).", highestSpec.toString());

        BindingTemplate bindingTemplate = helper.findBindingTemplateByKey(service, UDDI_SPEC_VERSION_KEY,
            highestSpec.toString());

        // we have no info on which binding template/endpoint "version" to use so just take the first.
        if (bindingTemplate == null || bindingTemplate.getAccessPoint() == null) {
            LOG.error("No binding templates found for home community");
            return endpointURL;
        }

        endpointURL = bindingTemplate.getAccessPoint().getValue();

        LOG.debug("Home community ({}) and service name ({}) returned endpoint address: {}",
            homeCommunityId, uniformServiceName, endpointURL);

        return endpointURL;
    }

    @Override
    public String getEndpointURLByServiceNameSpecVersion(String homeCommunityId, String uniformServiceName,
        UDDI_SPEC_VERSION version) throws ConnectionManagerException {

        LOG.trace("begin getEndpointURLByServiceName: {} / {}", homeCommunityId, uniformServiceName);

        String endpointURL = "";

        if (version == null) {
            return endpointURL;
        }

        BusinessEntity entity = getBusinessEntityByHCID(homeCommunityId);
        if (entity == null) {
            return endpointURL;
        }

        ConnectionManagerCacheHelper helper = new ConnectionManagerCacheHelper();

        BusinessService service = helper.getBusinessServiceByServiceName(entity, uniformServiceName);
        if (service == null) {
            return endpointURL;
        }

        LOG.debug("Attempting to find binding template with spec version ({}).", version);

        BindingTemplate bindingTemplate = helper.findBindingTemplateByKey(service, UDDI_SPEC_VERSION_KEY,
            version.toString());

        // we have no info on which binding template/endpoint "version" to use so just take the first.
        if (bindingTemplate == null || bindingTemplate.getAccessPoint() == null) {
            LOG.error("No binding templates found for home community: {} and service name: {}", homeCommunityId,
                uniformServiceName);
            throw new ConnectionManagerException("No matching target endpoint for guidance: " + version);
        }

        endpointURL = bindingTemplate.getAccessPoint().getValue();

        LOG.debug("Home community ({})) and service name ({}) returned endpoint address: {}",
            homeCommunityId, uniformServiceName, endpointURL);

        return endpointURL;
    }

    @Override
    public String getInternalEndpointURLByServiceName(String uniformServiceName) throws ConnectionManagerException {
        String endpointURL = null;
        String homeCommunityId = getHomeCommunityFromPropFile();

        if (NullChecker.isNotNullish(homeCommunityId)) {
            endpointURL = getDefaultEndpointURLByServiceName(homeCommunityId, uniformServiceName);
        }

        return endpointURL;
    }

    @Override
    public String getEndpointURLFromNhinTarget(NhinTargetSystemType targetSystem, String serviceName)
        throws ConnectionManagerException {

        String endpointUrl = null;

        if (targetSystem != null) {
            if (targetSystem.getEpr() != null) {
                // Extract the URL from the Endpoint Reference
                LOG.debug("Attempting to look up URL by EPR");
                if (targetSystem.getEpr() != null && targetSystem.getEpr().getAddress() != null
                    && NullChecker.isNotNullish(targetSystem.getEpr().getAddress().getValue())) {

                    endpointUrl = targetSystem.getEpr().getAddress().getValue();
                }
            } else if (NullChecker.isNotNullish(targetSystem.getUrl())) {
                // Echo back the URL provided
                LOG.debug("Attempting to look up URL by URL");
                endpointUrl = targetSystem.getUrl();
            } else if (NullChecker.isNotNullish(serviceName) && targetSystem.getHomeCommunity() != null
                && NullChecker.isNotNullish(targetSystem.getHomeCommunity().getHomeCommunityId())) {

                // Get the URL based on Home Community Id and Service Name
                String homeCommunityId = HomeCommunityMap.formatHomeCommunityId(targetSystem.getHomeCommunity()
                    .getHomeCommunityId());

                final String userSpecVersion = targetSystem.getUseSpecVersion();

                if (!StringUtils.isEmpty(userSpecVersion)) {
                    final UDDI_SPEC_VERSION version = UDDI_SPEC_VERSION.fromString(userSpecVersion);

                    LOG.debug("Attempting to look up URL by home community id: {}, service name: {}, and version: {}",
                        homeCommunityId, serviceName, version.toString());
                    endpointUrl = getEndpointURLByServiceNameSpecVersion(homeCommunityId, serviceName, version);
                } else {
                    LOG.debug("Retrieve endpoint from service Name {}", serviceName);
                    endpointUrl = getDefaultEndpointURLByServiceName(homeCommunityId, serviceName);
                }
            }
        }

        LOG.debug("Returning URL: {}", endpointUrl);

        return endpointUrl;
    }

    @Override
    public List<UrlInfo> getEndpointURLFromNhinTargetCommunities(NhinTargetCommunitiesType targets, String serviceName)
        throws ConnectionManagerException {

        ConnectionManagerCacheHelper helper = new ConnectionManagerCacheHelper();
        Set<UrlInfo> endpointUrlSet = new HashSet<>();

        if (targets != null && NullChecker.isNotNullish(targets.getNhinTargetCommunity())) {
            for (NhinTargetCommunityType target : targets.getNhinTargetCommunity()) {
                if (target.getHomeCommunity() != null
                    && NullChecker.isNotNullish(target.getHomeCommunity().getHomeCommunityId())) {

                    LOG.debug("Looking up URL by home community id");
                    String endpt = getDefaultEndpointURLByServiceName(target.getHomeCommunity().getHomeCommunityId(),
                        serviceName);

                    if (NullChecker.isNotNullish(endpt) || (NullChecker.isNullish(endpt)
                        && serviceName.equals(NhincConstants.DOC_QUERY_SERVICE_NAME))) {

                        UrlInfo entry = new UrlInfo();
                        entry.setHcid(target.getHomeCommunity().getHomeCommunityId());
                        entry.setUrl(endpt);
                        endpointUrlSet.add(entry);
                    }
                }

                if (target.getRegion() != null) {
                    LOG.debug("Looking up URL by region");
                    filterByRegion(endpointUrlSet, target.getRegion(), serviceName);
                }

                if (target.getList() != null) {
                    LOG.debug("Looking up URL by list");
                    LOG.warn("The List target feature has not been implemented yet");
                }
            }
        } else {
            // This is the broadcast scenario so retrieve the entire list of URLs for the specified service
            for (BusinessEntity entity : getAllBusinessEntitySetByServiceName(serviceName)) {
                String hcid = helper.getCommunityId(entity);
                String endpt = getDefaultEndpointURLByServiceName(hcid, serviceName);

                if (NullChecker.isNotNullish(endpt)) {
                    UrlInfo entry = new UrlInfo();
                    entry.setHcid(hcid);
                    entry.setUrl(endpt);
                    endpointUrlSet.add(entry);
                }

            }
        }

        List<UrlInfo> endpointUrlList = new ArrayList<>(endpointUrlSet);
        printUrlList(endpointUrlList);

        return endpointUrlList;
    }

    public boolean updateInternalServiceUrl(String serviceName, String url) throws Exception {
        String homeCommunityId = getHomeCommunityFromPropFile();
        String homeCommunityIdWithoutPrefix = HomeCommunityMap.formatHomeCommunityId(homeCommunityId);
        String homeCommunityIdWithPrefix = HomeCommunityMap.getHomeCommunityIdWithPrefix(homeCommunityId);

        ConnectionManagerCacheHelper helper = new ConnectionManagerCacheHelper();

        BusinessEntity internalEntity;
        if (internalConnectionInfo.containsKey(homeCommunityIdWithoutPrefix)) {
            internalEntity = internalConnectionInfo.get(homeCommunityIdWithoutPrefix);
        } else if (internalConnectionInfo.containsKey(homeCommunityIdWithPrefix)) {
            internalEntity = internalConnectionInfo.get(homeCommunityIdWithPrefix);
        } else {
            return false;
        }

        BusinessService service = helper.getBusinessServiceByServiceName(internalEntity, serviceName);

        BindingTemplate serviceUrl = helper.findBindingTemplateByKey(service,
            AdapterEndpointManager.ADAPTER_API_LEVEL_KEY, NhincConstants.ADAPTER_API_LEVEL.LEVEL_a0.name());

        serviceUrl.getAccessPoint().setValue(url);

        BusinessDetail detail = getInternalConnectionManagerDAO().loadBusinessDetail();

        for (BusinessEntity savedEntity : detail.getBusinessEntity()) {
            String identifier = getHcidFromIdentifierBag(savedEntity);
            if (identifier != null && (identifier.equals(homeCommunityIdWithoutPrefix)
                || identifier.equals(homeCommunityIdWithPrefix))) {

                detail.getBusinessEntity().remove(savedEntity);
                detail.getBusinessEntity().add(internalEntity);
                break;
            }
        }

        getInternalConnectionManagerDAO().saveBusinessDetail(detail);

        return true;
    }

    private String getHcidFromIdentifierBag(BusinessEntity entity) {
        for (KeyedReference ref : entity.getIdentifierBag().getKeyedReference()) {
            if (ref.getTModelKey().equals("uddi:nhin:nhie:homecommunityid")) {
                return ref.getKeyValue();
            }
        }

        return null;
    }
}
