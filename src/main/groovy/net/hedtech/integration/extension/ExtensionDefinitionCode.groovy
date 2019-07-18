/******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 ******************************************************************************/
package net.hedtech.integration.extension

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.hibernate.CacheMode

import javax.persistence.*

/**
 * Ethos API Extension Definition Code
 */

@Entity
@Table(name = "GTVAPEC")
@NamedQueries(value = [
        @NamedQuery(name = "ExtensionDefinitionCode.countAll",
                query = """select count(*) FROM ExtensionDefinitionCode a"""),
        @NamedQuery(name = "ExtensionDefinitionCode.fetchAll",
                query = """FROM ExtensionDefinitionCode a""")
])
@EqualsAndHashCode(includeFields = true)
@ToString(includeNames = true, includeFields = true)
class ExtensionDefinitionCode {

    public static final String EXT_CACHE_NAME = "extensibilityCache";

    @Id
    @Column(name = "GTVAPEC_SURROGATE_ID")
    @SequenceGenerator(name = "GTVAPEC_SEQ_GEN", allocationSize = 1, sequenceName = "GTVAPEC_SURROGATE_ID_SEQUENCE")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "GTVAPEC_SEQ_GEN")
    Long id


    @Column(name = "GTVAPEC_CODE")
    String code

    @Column(name = "GTVAPEC_DESC")
    String description

    /**
     * Optimistic lock token for GTVAPEC
     */
    @Version
    @Column(name = "GTVAPEC_VERSION")
    Long version

    /**
     * ACTIVITY DATE: The date that the information for the row was inserted or updated in the GTVAPEC table.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "GTVAPEC_ACTIVITY_DATE")
    Date lastModified

    /**
     * USER IDENTIFICATION: The unique identification of the user who changed the record.
     */
    @Column(name = "GTVAPEC_USER_ID")
    String lastModifiedBy

    /**
     * Data origin column for GURAPEX
     */
    @Column(name = "GTVAPEC_DATA_ORIGIN")
    String dataOrigin

    static constraints = {

        lastModified(nullable: true)
        lastModifiedBy(nullable: true, maxSize: 30)
        dataOrigin(nullable: true, maxSize: 30)

    }

    //Read Only fields that should be protected against update
    public static readonlyProperties = []


    static long countAll(){
        def result
        ExtensionDefinitionCode.withSession { session ->
            result = session.getNamedQuery('ExtensionDefinitionCode.countAll').setCacheMode(CacheMode.IGNORE)
                    .setCacheable(false).setCacheRegion(ExtensionDefinitionCode.EXT_CACHE_NAME).uniqueResult()
        }
        return result
    }

    static def fetchAll(){
        List<ExtensionDefinitionCode> extensionDefinitionCodeList = null

        extensionDefinitionCodeList = ExtensionDefinitionCode.withSession { session ->
            extensionDefinitionCodeList = session.getNamedQuery('ExtensionDefinitionCode.fetchAll').setCacheMode(CacheMode.IGNORE)
                    .setCacheable(false).setCacheRegion(ExtensionDefinitionCode.EXT_CACHE_NAME).list()
        }
        return extensionDefinitionCodeList
    }
}
