/******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 ******************************************************************************/
package net.hedtech.integration.extension

import org.hibernate.CacheMode
import org.hibernate.annotations.CacheConcurrencyStrategy

import javax.persistence.*

/**
 * API Extension Version  resolution table
 */
@Cacheable(true)
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = ExtensionVersion.EXT_CACHE_NAME)
@Entity
@Table(name = "GURAPVR")
@NamedQueries(value = [
        @NamedQuery(name = "ExtensionVersion.fetchByResourceName",
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

    public String toString() {
        """ExtensionVersion[
                id=$id,
                version=$version,
                resourceName=$resourceName,
                knownMediaType=$knownMediaType,
                lastModified=$lastModified,
                lastModifiedBy=$lastModifiedBy,
                dataOrigin=$dataOrigin"""
    }


    boolean equals(o) {
        if (this.is(o)) return true
        if (!(o instanceof ExtensionVersion)) return false
        ExtensionVersion that = (ExtensionVersion) o
        if (id != that.id) return false
        if (version != that.version) return false
        if (resourceName != that.resourceName) return false
        if (knownMediaType != that.knownMediaType) return false
        if (comment != that.comment) return false
        if (lastModified != that.lastModified) return false
        if (lastModifiedBy != that.lastModifiedBy) return false
        if (dataOrigin != that.dataOrigin) return false

        return true
    }


    int hashCode() {
        int result
        result = (id != null ? id.hashCode() : 0)
        result = 31 * result + (version != null ? version.hashCode() : 0)
        result = 31 * result + (knownMediaType != null ? knownMediaType.hashCode() : 0)
        result = 31 * result + (lastModified != null ? lastModified.hashCode() : 0)
        result = 31 * result + (lastModifiedBy != null ? lastModifiedBy.hashCode() : 0)
        result = 31 * result + (dataOrigin != null ? dataOrigin.hashCode() : 0)

        return result
    }


    static constraints = {

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

    static ExtensionVersion fetchByResourceName(String resourceName) {
        List<ExtensionVersion> extensionVersionList = null
        if( !resourceName ) {
            return extensionVersionList
        }

        extensionVersionList = ExtensionVersion.withSession { session ->
            extensionVersionList = session.getNamedQuery('ExtensionVersion.fetchByResourceName').setCacheMode(CacheMode.GET)
                    .setString('resourceName', resourceName).setCacheable(true).setCacheRegion(ExtensionVersion.EXT_CACHE_NAME).list()
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


}
