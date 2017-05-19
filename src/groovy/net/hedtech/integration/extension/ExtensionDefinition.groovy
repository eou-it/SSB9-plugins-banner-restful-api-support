/******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 ******************************************************************************/
package net.hedtech.integration.extension

import javax.persistence.*

/**
 * Ethos API Extension Definition
 */

@Entity
@Table(name = "GURAPEX")
class ExtensionDefinition implements Serializable {

    /**
     * Surrogate ID for GURAPEX
     */
    @Id
    @Column(name = "GURAPEX_SURROGATE_ID")
    @SequenceGenerator(name = "GURAPEX_SEQ_GEN", allocationSize = 1, sequenceName = "GURAPEX_SURROGATE_ID_SEQUENCE")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "GURAPEX_SEQ_GEN")
    Long id

    @Column(name = "GURAPEX_EXTENSION_TYPE")
    String extensionType

    @Column(name = "GURAPEX_RESOURCE_NAME")
    String resourceName

    @Column(name = "GURAPEX_DESC")
    String description

    @Column(name = "GURAPEX_EXTENSION_CODE")
    String extensionCode

    @Column(name = "GURAPEX_JSON_PATH")
    String jsonPath

    @Column(name = "GURAPEX_JSON_LABEL")
    String jsonLabel

    @Column(name = "GURAPEX_JSON_TYPE")
    String jsonType

    @Column(name = "GURAPEX_SELECT_COLUMN_NAME")
    String selectColumnName

    @Column(name = "GURAPEX_SQL_PROCESS_CODE")
    String sqlProcessCode

    @Column(name = "GURAPEX_SQL_RULE_CODE")
    String sqlRuleCode

    /**
     * Optimistic lock token for GURAPEX
     */
    @Version
    @Column(name = "GURAPEX_VERSION")
    Long version

    /**
     * ACTIVITY DATE: The date that the information for the row was inserted or updated in the GOBINTL table.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "GURAPEX_ACTIVITY_DATE")
    Date lastModified

    /**
     * USER IDENTIFICATION: The unique identification of the user who changed the record.
     */
    @Column(name = "GURAPEX_USER_ID")
    String lastModifiedBy

    /**
     * Data origin column for GURAPEX
     */
    @Column(name = "GURAPEX_DATA_ORIGIN")
    String dataOrigin

    /**
     * Return a string represenation
     * @return
     */
    public String toString() {
        """ExtensionDefinition[
					id=$id,
                    resourceName=$resourceName,
                    extensionCode=$extensionCode,
                    jsonLabel=$jsonLabel,
					version=$version,
					lastModified=$lastModified,
					lastModifiedBy=$lastModifiedBy,
					dataOrigin=$dataOrigin"""
    }

    /**
     * Equals operator
     * @param o
     * @return
     */
    boolean equals(o) {
        if (this.is(o)) return true
        if (!(o instanceof ExtensionDefinition)) return false
        ExtensionDefinition that = (ExtensionDefinition) o
        if (id != that.id) return false
        if (resourceName != that.resourceName) return false
        if (extensionCode != that.extensionCode) return false
        if (jsonLabel != that.jsonLabel) return false
        if (version != that.version) return false
        if (lastModified != that.lastModified) return false
        if (lastModifiedBy != that.lastModifiedBy) return false
        if (dataOrigin != that.dataOrigin) return false

        return true
    }

    /**
     * Return a hash summary of this instance
     * @return
     */
    int hashCode() {
        int result
        result = (id != null ? id.hashCode() : 0)
        result = 31 * result + (resourceName != null ? resourceName.hashCode() : 0)
        result = 31 * result + (extensionCode != null ? extensionCode.hashCode() : 0)
        result = 31 * result + (version != null ? version.hashCode() : 0)
        result = 31 * result + (lastModified != null ? lastModified.hashCode() : 0)
        result = 31 * result + (lastModifiedBy != null ? lastModifiedBy.hashCode() : 0)
        result = 31 * result + (dataOrigin != null ? dataOrigin.hashCode() : 0)

        return result
    }

    static constraints = {
        selectColumnName(nullable:true)
        sqlProcessCode(nullable:true)
        extensionCode(nullable:true)
        description(nullable:true)
        sqlRuleCode(nullable:true)
        lastModified(nullable: true)
        lastModifiedBy(nullable: true, maxSize: 30)
        dataOrigin(nullable: true, maxSize: 30)

    }

    //Read Only fields that should be protected against update
    public static readonlyProperties = []
}
