/******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 ******************************************************************************/
package net.hedtech.integration.extension

import org.hibernate.CacheMode
import org.hibernate.annotations.CacheConcurrencyStrategy

import javax.persistence.*

/**
 * Ethos API Extension Definition
 */

@Cacheable(true)
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = ExtensionDefinition.EXT_CACHE_NAME)
@Entity
@Table(name = "GURAPEX")
@NamedQueries(value = [
        @NamedQuery(name = "ExtensionDefinition.fetchAllByResourceNameAndExtensionCode",
                query = """FROM ExtensionDefinition a
                              WHERE a.resourceName = :resourceName
                                AND a.extensionCode = :extensionCode""")
])
class ExtensionDefinition implements Serializable {

    public static final String EXT_CACHE_NAME = "extensibilityCache";

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

    @Column(name = "GURAPEX_COLUMN_NAME")
    String columnName

    @Column(name = "GURAPEX_SQL_PROCESS_CODE")
    String sqlProcessCode

    @Column(name = "GURAPEX_READ_SQL_RULE_CODE")
    String sqlReadRuleCode

    @Column(name = "GURAPEX_WRITE_SQL_RULE_CODE")
    String sqlWriteRuleCode

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
        columnName(nullable:true)
        sqlProcessCode(nullable:true)
        extensionCode(nullable:true)
        description(nullable:true)
        sqlReadRuleCode(nullable:true)
        sqlWriteRuleCode(nullable:true)
        lastModified(nullable: true)
        lastModifiedBy(nullable: true, maxSize: 30)
        dataOrigin(nullable: true, maxSize: 30)

    }

    //Read Only fields that should be protected against update
    public static readonlyProperties = []


    static List<ExtensionDefinition> fetchAllByResourceNameAndExtensionCode(String resourceName, String extensionCode) {
        List<ExtensionDefinition> extensionDefinitionList = null
        if( !resourceName  ||  !extensionCode ) {
            return extensionDefinitionList
        }

        extensionDefinitionList = ExtensionDefinition.withSession { session ->
            extensionDefinitionList = session.getNamedQuery('ExtensionDefinition.fetchAllByResourceNameAndExtensionCode').setCacheMode(CacheMode.GET)
                    .setString('resourceName', resourceName).setString('extensionCode', extensionCode).setCacheable(true).setCacheRegion(ExtensionDefinition.EXT_CACHE_NAME).list()
        }
        return extensionDefinitionList
    }


}
