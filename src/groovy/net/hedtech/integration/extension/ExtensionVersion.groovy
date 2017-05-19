/******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 ******************************************************************************/
package net.hedtech.integration.extension

import grails.persistence.Entity
import javax.persistence.Column
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
@Entity
@Table(name = "GURAPVR")
@NamedQueries(value = [
        @NamedQuery(name = "ExtensionVersion.fetchByKnownMaxSequence",
                query = """FROM ExtensionVersion a
       WHERE a.known = :known AND a.resource = :resource
         AND a.sequence = (SELECT MAX(b.sequence)
                                  FROM ResolvedVersion b
                                 WHERE b.known = a.known)
             )
""")
])

class ExtensionVersion implements Serializable {

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
     * Optimistic lock token for GURAPVR
     */

    @Column(name = "GURAPVR_SEQUENCE")
    Long sequence

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
                sequence=$sequence,
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
        if (sequence != that.sequence) return false

        return true
    }


    int hashCode() {
        int result
        result = (id != null ? id.hashCode() : 0)
        result = 31 * result + (version != null ? version.hashCode() : 0)
        result = 31 * result + (alias != null ? alias.hashCode() : 0)
        result = 31 * result + (known != null ? known.hashCode() : 0)
        result = 31 * result + (sequence != null ? sequence.hashCode() : 0)
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

    public static ExtensionVersion fetchByKnownMaxSequence(String resource,String known) {
        ExtensionVersion extensionVersion
        ExtensionVersion.withSession { session ->
            extensionVersion = session.getNamedQuery('ExtensionVersion.fetchByKnownMaxSequence').setString('resource', resource).setString('known', known).list()[0]
        }
        return extensionVersion
    }
}
