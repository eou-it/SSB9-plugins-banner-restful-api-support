/******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 ******************************************************************************/
package net.hedtech.integration.diagnostic

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

import javax.persistence.*

/**
 * Resource diagnostic message.
 **/
@Entity
@Table(name = "GURETHD")
@NamedNativeQueries(value = [
        @NamedNativeQuery(name = "ResourceDiagnosticMessage.submitDiagnosticsJob",
                query = """BEGIN gokhedd.p_submit_diagnostics_job; END;""",
                resultSetMapping="ResourceDiagnosticMessage.void")
])
@SqlResultSetMappings(value = [
        @SqlResultSetMapping(name = "ResourceDiagnosticMessage.void")
])
@ToString(includeFields = true, includeNames = true)
@EqualsAndHashCode
public class ResourceDiagnosticMessage implements Serializable {

    @Id
    @Column(name = "GURETHD_SURROGATE_ID")
    Long id

    @Version
    @Column(name = "GURETHD_VERSION")
    Long version

    @Column(name = "GURETHD_RESOURCE_NAME")
    String resourceName

    @Column(name = "GURETHD_MSG_LEVEL")
    String messageLevel

    @Column(name = "GURETHD_MESSAGE")
    String message

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "GURETHD_ACTIVITY_DATE")
    Date lastModified

}
