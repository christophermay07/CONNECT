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
package gov.hhs.fha.nhinc.transform.policy;

/**
 *
 * @author rayj
 */
public class XacmlAttributeId {

    public static final String AUTHN_STATEMENT_AUTHN_INSTANT = "urn:gov:hhs:fha:nhinc:saml-authn-statement:auth-instant";
    public static final String AUTHN_STATEMENT_SESSION_INDEX = "urn:gov:hhs:fha:nhinc:saml-authn-statement:session-index";
    public static final String AUTHN_STATEMENT_AUTHN_CONTEXT_CLASS_REF = "urn:gov:hhs:fha:nhinc:saml-authn-statement:auth-context-class-ref";
    public static final String AUTHN_STATEMENT_SUBJECT_LOCALITY_ADDRESS = "urn:oasis:names:tc:xacml:1.0:subject:authn-locality:ip-address";
    public static final String AUTHN_STATEMENT_DNS_NAME = "urn:oasis:names:tc:xacml:1.0:subject:authn-locality:dns-name";
    public static final String USER_PERSON_NAME = "urn:oasis:names:tc:xacml:1.0:subject:subject-id";
    public static final String USER_ORGANIZATION_NAME = "urn:gov:hhs:fha:nhinc:user-organization-name";
    public static final String USER_ORGANIZATION_ID = "urn:oasis:names:tc:xspa:1.0:subject:organization-id";
    public static final String HOME_COMMUNITY_NAME = "http://www.hhs.gov/healthit/nhin#HomeCommunityId";
    public static final String UNIQUE_PATIENT_ID = "http://www.hhs.gov/healthit/nhin#subject-id";
    public static final String USER_ROLE_CODE = "urn:oasis:names:tc:xacml:2.0:subject:role";
    public static final String USER_ROLE_CODE_SYSTEM = "urn:gov:hhs:fha:nhinc:user-role-code-system";
    public static final String USER_ROLE_CODE_SYSTEM_NAME = "urn:gov:hhs:fha:nhinc:user-role-code-system-name";
    public static final String USER_ROLE_CODE_DISPLAY_NAME = "urn:gov:hhs:fha:nhinc:user-role-description";
    public static final String PURPOSE_OF_USE_CODE = "urn:oasis:names:tc:xspa:1.0:subject:purposeofuse";
    public static final String PURPOSE_OF_USE_CODE_SYSTEM = "urn:gov:hhs:fha:nhinc:purpose-of-use-code-system";
    public static final String PURPOSE_OF_USE_CODE_SYSTEM_NAME = "urn:gov:hhs:fha:nhinc:purpose-of-use-code-system-name";
    public static final String PURPOSE_OF_USE_CODE_DISPLAY_NAME = "urn:gov:hhs:fha:nhinc:purpose-of-use-display-name";
    public static final String AUTHZ_DECISION_STATEMENT_DECISION = "urn:gov:hhs:fha:nhinc:saml-authz-decision-statement-decision";
    public static final String AUTHZ_DECISION_STATEMENT_RESOURCE = "urn:gov:hhs:fha:nhinc:saml-authz-decision-statement-resource";
    public static final String AUTHZ_DECISION_STATEMENT_ACTION = "urn:gov:hhs:fha:nhinc:saml-authz-decision-statement-action";
    public static final String AUTHZ_DECISION_STATEMENT_EVIDENCE_ASSERTION_ID = "urn:gov:hhs:fha:nhinc:saml-authz-decision-statement-evidence-assertion-id";
    public static final String AUTHZ_DECISION_STATEMENT_EVIDENCE_ASSERTION_ISSUE_INSTANT = "urn:gov:hhs:fha:nhinc:saml-authz-decision-statement-evidence-assertion-issue-instant";
    public static final String AUTHZ_DECISION_STATEMENT_EVIDENCE_ASSERTION_VERSION = "urn:gov:hhs:fha:nhinc:saml-authz-decision-statement-evidence-assertion-version";
    public static final String AUTHZ_DECISION_STATEMENT_EVIDENCE_ASSERTION_ISSUER = "urn:gov:hhs:fha:nhinc:saml-authz-decision-statement-evidence-assertion-issuer";
    public static final String AUTHZ_DECISION_STATEMENT_EVIDENCE_ASSERTION_CONDITIONS_NOT_BEFORE = "urn:gov:hhs:fha:nhinc:saml-authz-decision-statement-evidence-assertion-not-before";
    public static final String AUTHZ_DECISION_STATEMENT_EVIDENCE_ASSERTION_CONDITIONS_NOT_ON_OR_AFTER = "urn:gov:hhs:fha:nhinc:saml-authz-decision-statement-evidence-assertion-not-on-or-after";
    public static final String AUTHZ_DECISION_STATEMENT_EVIDENCE_ASSERTION_ACCESS_CONSENT_POLICY = "urn:gov:hhs:fha:nhinc:saml-authz-decision-statement-evidence-assertion-access-consent";
    public static final String AUTHZ_DECISION_STATEMENT_EVIDENCE_ASSERTION_INSTANCE_ACCESS_CONSENT_POLICY = "urn:gov:hhs:fha:nhinc:saml-authz-decision-statement-evidence-assertion-instance-access-consent";
    public static final String SIGNATURE_KEY_MODULUS = "urn:gov:hhs:fha:nhinc:saml-signature-rsa-key-value-modulus";
    public static final String SIGNATURE_KEY_EXPONENT = "urn:gov:hhs:fha:nhinc:saml-signature-rsa-key-value-exponent";
    public static final String SIGNATURE_VALUE = "urn:gov:hhs:fha:nhinc:saml-signature-value";

    private XacmlAttributeId() {
    }
}
