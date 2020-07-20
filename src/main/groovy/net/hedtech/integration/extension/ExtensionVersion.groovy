/******************************************************************************
 Copyright 2017-2020 Ellucian Company L.P. and its affiliates.
 ******************************************************************************/
package net.hedtech.integration.extension

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.hibernate.CacheMode
import org.hibernate.annotations.CacheConcurrencyStrategy

import javax.persistence.Cacheable
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.NamedQueries
import javax.persistence.NamedQuery
import javax.persistence.SequenceGenerator
import javax.persistence.Table
import javax.persistence.Temporal
import javax.persistence.TemporalType
import javax.persistence.Version

/**
 * API Extension Version  resolution table
 */
@Cacheable(true)
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = ExtensionVersion.EXT_CACHE_NAME)
@Entity
@Table(name = "GURAPVR")
@NamedQueries(value = [
        @NamedQuery(name = "ExtensionVersion.fetchAllByResourceName",
                query = """FROM ExtensionVersion a
                              WHERE a.resourceName = :resourceName"""),
        @NamedQuery(name = "ExtensionVersion.fetchByResourceNameAndKnownMediaType",
                query = """FROM ExtensionVersion a
                              WHERE a.knownMediaType = :knownMediaType
                                AND a.resourceName = :resourceName"""),
        @NamedQuery(name = "ExtensionVersion.countAll",
                query = """select count(*) FROM ExtensionVersion a"""),
        @NamedQuery(name = "ExtensionVersion.fetchAll",
                query = """FROM ExtensionVersion a""")
])
@EqualsAndHashCode(includeFields = true)
@ToString(includeNames = true, includeFields = true)
class ExtensionVersion implements Serializable {

    public static final String EXT_CACHE_NAME = "extensibilityCache";

    /**
     * Surrogate ID for GURAPVR
     */
    @Id
    @Column(name = "GURAPVR_SURROGATE_ID")
    @SequenceGenerator(name = "GURAPVR_SEQ_GEN", allocationSize = 1, sequenceName = "GURAPVR_SURROGATE_ID_SEQUENCE")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "GURAPVR_SEQ_GEN")
    Long id

    /**
     * Optimistic lock token for GURAPVR
     */
    @Version
    @Column(name = "GURAPVR_VERSION")
    Long version

    /**
     * RESOURCE NAME: This resource this map applies to
     */
    @Column(name = "GURAPVR_RESOURCE_NAME")
    String resourceName

    /**
     * EXTENSION CODE: a code group to identify this version with
     */
    @Column(name = "GURAPVR_EXTENSION_CODE")
    String extensionCode

    /**
     * KNOWN MEDIATYPE: This field has the known api version.
     */
    @Column(name = "GURAPVR_KNOWN_MEDIATYPE")
    String knownMediaType

    /**
     * COMMENT: A general comment about the version
     */
    @Column(name = "GURAPVR_COMMENT")
    String comment

    /**
     * ACTIVITY DATE: The date that the information for the row was inserted or updated in the GOBINTL table.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "GURAPVR_ACTIVITY_DATE")
    Date lastModified

    /**
     * USER IDENTIFICATION: The unique identification of the user who changed the record.
     */
    @Column(name = "GURAPVR_USER_ID")
    String lastModifiedBy

    /**
     * Data origin column for GOBINTL
     */
    @Column(name = "GURAPVR_DATA_ORIGIN")
    String dataOrigin


    static constraints = {

        comment(nullable: true)
        lastModified(nullable: true)
        lastModifiedBy(nullable: true, maxSize: 30)
        dataOrigin(nullable: true, maxSize: 30)

    }

    //Read Only fields that should be protected against update
    public static readonlyProperties = []

    static ExtensionVersion fetchByResourceNameAndKnownMediaType(String resourceName, String knownMediaType) {
        List<ExtensionVersion> extensionVersionList = null
        if( !knownMediaType  ||  !resourceName ) {
            return extensionVersionList
        }

        extensionVersionList = ExtensionVersion.withSession { session ->
            extensionVersionList = session.getNamedQuery('ExtensionVersion.fetchByResourceNameAndKnownMediaType').setCacheMode(CacheMode.GET)
                    .setString('resourceName', resourceName).setString('knownMediaType', knownMediaType).setCacheable(true).setCacheRegion(ExtensionVersion.EXT_CACHE_NAME).list()
        }
        return extensionVersionList?.size() > 0 ? extensionVersionList?.get(0) : null
    }


    static long countAll(){
        def result
        ExtensionVersion.withSession { session ->
            result = session.getNamedQuery('ExtensionVersion.countAll').setCacheMode(CacheMode.IGNORE)
                    .setCacheable(false).setCacheRegion(ExtensionVersion.EXT_CACHE_NAME).uniqueResult()
        }
        return result
    }

    static def fetchAll(){
        List<ExtensionVersion> extensionVersionList = null

        extensionVersionList = ExtensionVersion.withSession { session ->
            extensionVersionList = session.getNamedQuery('ExtensionVersion.fetchAll').setCacheMode(CacheMode.IGNORE)
                    .setCacheable(false).setCacheRegion(ExtensionVersion.EXT_CACHE_NAME).list()
        }
        return extensionVersionList
    }

    static List<ExtensionVersion> fetchAllByResourceName(String resourceName) {
        List<ExtensionVersion> extensionVersionList = []
        if (resourceName) {
            extensionVersionList = ExtensionVersion.withSession { session ->
                extensionVersionList = session.getNamedQuery('ExtensionVersion.fetchAllByResourceName').setCacheMode(CacheMode.GET)
                        .setString('resourceName', resourceName).setCacheable(true).setCacheRegion(ExtensionVersion.EXT_CACHE_NAME).list()
            }
        }

        return extensionVersionList
    }


}
