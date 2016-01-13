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

import gov.hhs.fha.nhinc.nhinclib.NhincConstants;
import gov.hhs.fha.nhinc.nhinclib.NhincConstants.UDDI_SPEC_VERSION;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uddi.api_v3.BindingTemplate;
import org.uddi.api_v3.BusinessEntity;
import org.uddi.api_v3.BusinessService;
import org.uddi.api_v3.KeyedReference;

public class ConnectionManagerCacheHelper {

    private static final Logger LOG = LoggerFactory.getLogger(ConnectionManagerCacheHelper.class);

    public static final String UDDI_SPEC_VERSION_KEY = "uddi:nhin:versionofservice";
    public static final String UDDI_HOME_COMMUNITY_ID_KEY = "uddi:nhin:nhie:homecommunityid";
    public static final String UDDI_STATE_KEY = "uddi:uddi.org:ubr:categorization:iso3166";
    public static final String UDD_SERVICE_NAMES_KEY = "uddi:nhin:standard-servicenames";

    /**
     * This method merge the businessServices from the uddiEntity to the internalEntity.
     *
     * @param internalEntity Internal Business Entity
     * @param uddiEntity UDDI Business Entity
     * @return
     */
    public BusinessEntity mergeBusinessEntityServices(BusinessEntity internalEntity, BusinessEntity uddiEntity) {
        Map<String, BusinessService> internalServiceNames = new HashMap<>();
        List<BusinessService> servicesToAdd = new ArrayList<>();

        // TODO: Concurrent mod exception here?
        for (BusinessService internalService : internalEntity.getBusinessServices().getBusinessService()) {
            internalServiceNames.put(internalService.getServiceKey(), internalService);
        }

        // TODO: Concurrent mod exception here?
        for (BusinessService uddiService : uddiEntity.getBusinessServices().getBusinessService()) {
            if (!internalServiceNames.containsKey(uddiService.getServiceKey())) {
                servicesToAdd.add(uddiService);
            }
        }

        internalEntity.getBusinessServices().getBusinessService().addAll(servicesToAdd);

        return internalEntity;
    }

    /**
     * TODO: JavaDoc
     *
     * @param businessEntity
     * @return
     */
    public String getCommunityId(BusinessEntity businessEntity) {
        KeyedReference ref = getCommunityIdKeyReference(businessEntity);
        if (ref != null) {
            return ref.getKeyValue().trim();
        }
        return null;
    }

    /**
     * TODO: JavaDoc
     *
     * @param businessEntity
     * @return
     */
    public KeyedReference getCommunityIdKeyReference(BusinessEntity businessEntity) {
        if (businessEntity != null && businessEntity.getIdentifierBag() != null) {
            for (KeyedReference reference : businessEntity.getIdentifierBag().getKeyedReference()) {
                if (reference.getTModelKey().equals(UDDI_HOME_COMMUNITY_ID_KEY)) {
                    return reference;
                }
            }
        }
        return null;
    }

    /**
     *
     * @param businessEntity
     * @return the list of states; if none, returns null
     */
    public List<String> getStates(BusinessEntity businessEntity) {
        List<String> states = new ArrayList<>();

        for (KeyedReference reference : businessEntity.getCategoryBag().getKeyedReference()) {
            if (UDDI_STATE_KEY.equals(reference.getTModelKey())) {
                states.add(reference.getKeyValue());
            }
        }

        if (states.isEmpty()) {
            states = null;
        }

        return states;
    }

    /**
     * This method looks for the entity with the given home community ID and returns it.
     *
     * @param entities The entities to be searched.
     * @param homeCommunityId The home community ID to search for.
     * @return The business entity for that home community.
     */
    public BusinessEntity extractBusinessEntity(List<BusinessEntity> entities, String homeCommunityId) {
        if (entities != null && !entities.isEmpty() && StringUtils.isNotEmpty(homeCommunityId)) {
            for (BusinessEntity entity : entities) {
                if (homeCommunityId.equals(getCommunityId(entity))) {
                    return entity;
                }
            }
        }

        return null;
    }

    /*
     * This method searches for the business entity in the list that has the same home community Id. If it finds it, it
     * replaces it with this one. If it does not find it, then it adds this one to the list.
     *
     * @param entities The entities to search.
     * @param entity The entity to replace...
     */
    public void replaceBusinessEntity(List<BusinessEntity> entities, BusinessEntity entity) {
        if (entity != null && entities != null) {
            // getCommunityId has a large overhead, so only call if entity & entities are not null
            String homeCommunityId = getCommunityId(entity);

            if (homeCommunityId != null) {
                for (int i = 0; i < entities.size(); i++) {
                    if (homeCommunityId.equals(getCommunityId(entities.get(i)))) {
                        entities.set(i, entity);
                        return; // We are done
                    }
                }
            }

            // entity was not found, add it instead
            entities.add(entity);
        }
    }

    /**
     * TODO: Javadoc
     *
     * @param businessEntity
     * @param serviceName
     * @return
     */
    public List<UDDI_SPEC_VERSION> getSpecVersionsFromBusinessEntity(BusinessEntity businessEntity,
        NhincConstants.NHIN_SERVICE_NAMES serviceName) {

        List<UDDI_SPEC_VERSION> specVersionList = new ArrayList<>();

        if (businessEntity != null) {
            for (BusinessService service : businessEntity.getBusinessServices().getBusinessService()) {
                if (serviceNameEquals(service, serviceName.getUDDIServiceName())) {
                    specVersionList = getSpecVersions(service);
                }
            }
        }

        return specVersionList;
    }

    private boolean serviceNameEquals(BusinessService service, String serviceName) {
        for (String sName : getServiceNames(service)) {
            if (sName.equalsIgnoreCase(serviceName)) {
                return true;
            }
        }

        return false;
    }

    private List<String> getServiceNames(BusinessService service) {
        List<String> serviceNameList = new ArrayList<>();

        if (service.getCategoryBag() != null && service.getCategoryBag().getKeyedReference() != null) {
            for (KeyedReference reference : service.getCategoryBag().getKeyedReference()) {
                if (reference.getTModelKey().equals(UDD_SERVICE_NAMES_KEY)) {
                    serviceNameList.add(reference.getKeyValue());
                }
            }
        }

        return serviceNameList;
    }

    /**
     * TODO: Javadoc
     *
     * @param businessService
     * @return
     */
    public List<UDDI_SPEC_VERSION> getSpecVersions(BusinessService businessService) {
        List<UDDI_SPEC_VERSION> specVersionList = new ArrayList<>();

        if (businessService == null || businessService.getBindingTemplates() == null
            || businessService.getBindingTemplates().getBindingTemplate() == null) {

            return specVersionList;
        }

        for (BindingTemplate bindingTemplate : businessService.getBindingTemplates().getBindingTemplate()) {
            if (bindingTemplate.getCategoryBag() != null
                && bindingTemplate.getCategoryBag().getKeyedReference() != null) {

                for (KeyedReference reference : bindingTemplate.getCategoryBag().getKeyedReference()) {
                    String keyName = reference.getTModelKey();
                    String specVersionValue = reference.getKeyValue();

                    if (keyName.equals(UDDI_SPEC_VERSION_KEY)) {
                        specVersionList.add(UDDI_SPEC_VERSION.fromString(specVersionValue));
                    }
                }
            }
        }

        return specVersionList;
    }

    /**
     * TODO: Javadoc
     *
     * @param specVersions
     * @return
     */
    public UDDI_SPEC_VERSION getHighestUDDISpecVersion(List<UDDI_SPEC_VERSION> specVersions) {
        UDDI_SPEC_VERSION highestSpecVersion = null;

        try {
            for (UDDI_SPEC_VERSION specVersion : specVersions) {
                if (highestSpecVersion == null || specVersion.ordinal() > highestSpecVersion.ordinal()) {
                    highestSpecVersion = specVersion;
                }
            }
        } catch (Exception ex) {
            LOG.error("Error deducing highest spec version: {}", ex.getLocalizedMessage(), ex);
        }

        return highestSpecVersion;
    }

    /**
     * TODO: Javadoc
     *
     * @param businessEntity
     * @param serviceName
     * @param key
     * @param value
     * @return
     */
    public BindingTemplate findBindingTemplateByCategoryBagNameValue(BusinessEntity businessEntity, String serviceName,
        String key, String value) {

        BindingTemplate bindingTemplate;

        if (businessEntity != null && businessEntity.getBusinessServices() != null
            && businessEntity.getBusinessKey() != null) {

            for (BusinessService service : businessEntity.getBusinessServices().getBusinessService()) {
                if (serviceNameEquals(service, serviceName)) {
                    bindingTemplate = findBindingTemplateByKey(service, key, value);
                    if (bindingTemplate != null) {
                        return bindingTemplate;
                    }
                }
            }
        }

        return null;
    }

    /**
     * TODO: Javadoc
     *
     * @param service
     * @param keyRefName
     * @param keyRefValue
     * @return
     */
    public BindingTemplate findBindingTemplateByKey(BusinessService service, String keyRefName, String keyRefValue) {
        BindingTemplate bindingTemplate = null;

        if (service.getBindingTemplates() != null && service.getBindingTemplates().getBindingTemplate() != null) {
            for (BindingTemplate template : service.getBindingTemplates().getBindingTemplate()) {
                if (template.getCategoryBag() != null && template.getCategoryBag().getKeyedReference() != null) {
                    for (KeyedReference ref : template.getCategoryBag().getKeyedReference()) {
                        if (keyRefName.equals(ref.getTModelKey()) && keyRefValue.equals(ref.getKeyValue())) {
                            return template;
                        }
                    }
                }
            }
        }

        return bindingTemplate;
    }

    /**
     * TODO: Javadoc
     *
     * @param entity
     * @param uniformServiceName
     * @return
     * @throws ConnectionManagerException
     */
    public BusinessService getBusinessServiceByServiceName(BusinessEntity entity, String uniformServiceName)
        throws ConnectionManagerException {

        if (entity != null && entity.getBusinessServices() != null && StringUtils.isNotEmpty(uniformServiceName)) {
            for (BusinessService service : entity.getBusinessServices().getBusinessService()) {
                if (serviceNameEquals(service, uniformServiceName)) {
                    return service;
                }
            }
        }

        return null;
    }
}
