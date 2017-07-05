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
        @NamedQuery(name = "ExtensionVersion.fetchByAliasAndResourceName",
                query = """FROM ExtensionVersion a
                              WHERE a.alias = :alias
                                AND a.resourceName = :resourceName"""),
        @NamedQuery(name = "ExtensionVersion.fetchDefaultByResourceName",
                query = """FROM ExtensionVersion a
                              WHERE a.resourceName = :resourceName""")
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
     * KNOWN: This field has the known api version.
     */
    @Column(name = "GURAPVR_KNOWN")
    String known

    /**
     * ALIAS: This field indicates alias version.
     */
    @Column(name = "GURAPVR_ALIAS")
    String alias

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
                alias=$alias,
                known=$known,
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
        if (alias != that.alias) return false
        if (known != that.known) return false

        if (lastModified != that.lastModified) return false
        if (lastModifiedBy != that.lastModifiedBy) return false
        if (dataOrigin != that.dataOrigin) return false

        return true
    }


    int hashCode() {
        int result
        result = (id != null ? id.hashCode() : 0)
        result = 31 * result + (version != null ? version.hashCode() : 0)
        result = 31 * result + (alias != null ? alias.hashCode() : 0)
        result = 31 * result + (known != null ? known.hashCode() : 0)
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


    static ExtensionVersion fetchByAliasAndResourceName(String alias, String resourceName) {
        List<ExtensionVersion> extensionVersionList = null
        if( !alias  ||  !resourceName ) {
            return extensionVersionList
        }

        extensionVersionList = ExtensionVersion.withSession { session ->
            extensionVersionList = session.getNamedQuery('ExtensionVersion.fetchByAliasAndResourceName').setCacheMode(CacheMode.GET)
                    .setString('alias', alias).setString('resourceName', resourceName).setCacheable(true).setCacheRegion(ExtensionVersion.EXT_CACHE_NAME).list()
        }
        return extensionVersionList?.size() > 0 ? extensionVersionList?.get(0) : null
    }


    static ExtensionVersion fetchDefaultByResourceName(String resourceName) {
        List<ExtensionVersion> extensionVersionList = null
        if( !resourceName ) {
            return extensionVersionList
        }

        extensionVersionList = ExtensionVersion.withSession { session ->
            extensionVersionList = session.getNamedQuery('ExtensionVersion.fetchDefaultByResourceName').setCacheMode(CacheMode.GET)
                    .setString('resourceName', resourceName).setCacheable(true).setCacheRegion(ExtensionVersion.EXT_CACHE_NAME).list()
        }
        return extensionVersionList?.size() > 0 ? extensionVersionList?.get(0) : null
    }


}
